package br.com.gwaya.jopy.communication;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.List;

import br.com.gwaya.jopy.App;
import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.StatusPedido;
import br.com.gwaya.jopy.dao.AcessoDAO;
import br.com.gwaya.jopy.dao.FilaPedidoCompraDAO;
import br.com.gwaya.jopy.dao.MySQLiteHelper;
import br.com.gwaya.jopy.dao.PedidoCompraDAO;
import br.com.gwaya.jopy.model.Acesso;
import br.com.gwaya.jopy.model.PedidoCompra;

public class PedidoCompraService extends IntentService {

    public static final String NOTIFICATION = "br.com.gwaya.android.service.receiver";
    public static final String PEDIDOS_EMITIDOS = "PEDIDOS_EMITIDOS";
    public static final String PEDIDOS_REJEITADOS = "PEDIDOS_REJEITADOS";
    public static final String PEDIDOS_APROVADOS = "PEDIDOS_APROVADOS";
    private final IBinder mBinder = new MyBinder();
    private final List<PedidoCompra> list = new ArrayList<>();
    private AcessoDAO acessoDatasource;
    private PedidoCompraDAO pedidoCompraDatasource;

    public PedidoCompraService() {
        super("PedidoCompraService");
    }

    public static String loadFromNetwork(String urlString, Acesso acesso, Context context) {
        String responseBody = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(urlString);

            httpGet.setHeader("Authorization", acesso.getToken_Type() + " " + acesso.getAccess_Token());

//	        ResponseHandler<String> responseHandler = new BasicResponseHandler();
//	        str = httpclient.execute(httpGet, responseHandler);
            HttpResponse response = httpclient.execute(httpGet);

            // Obtem codigo de retorno HTTP
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode >= 200 && statusCode <= 202) {
                // Obtem string do Body retorno HTTP
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                responseBody = responseHandler.handleResponse(response);
            } else {
                Acesso.logoff(context, statusCode);
            }

        } catch (Exception e) {
            Log.e("", "");
        }

        return responseBody;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (acessoDatasource == null) {
            acessoDatasource = new AcessoDAO();
        }
        try {

            List<Acesso> lstAcesso = acessoDatasource.getAllAcesso();

            pedidoCompraDatasource = new PedidoCompraDAO();

            if (lstAcesso.size() > 0) {

                Acesso acesso = lstAcesso.get(0);

                String url = getResources().getString(R.string.protocolo)
                        + App.API_REST
                        + getResources().getString(R.string.pedidocompra_path),
                        dtMod = pedidoCompraDatasource.ultimoSync();

                if (dtMod != null) {
                    url += "?gte=" + dtMod;
                }

                descarregaFila(acesso);

                String responseData = "";

                responseData = loadFromNetwork(url, acesso, this.getApplicationContext());

                GsonBuilder gsonb = new GsonBuilder();
                Gson gson = gsonb.create();
                PedidoCompra[] pedidos = null;

                pedidos = gson.fromJson(responseData, PedidoCompra[].class);

                if (pedidos != null && pedidos.length > 0) {

                    // Diego Bueno - 10/02/2015 - verifica se pedido já existe, caso sim, deleta e inclui com nova alteração
                    for (PedidoCompra pedido : pedidos) {
                        if (pedidoCompraDatasource.ExistePedidoCompra(pedido.get_id())) {
                            pedidoCompraDatasource.deletePedidoCompra(pedido);
                            pedidoCompraDatasource.createUpdatePedidoCompra(pedido);
                        } else {
                            pedidoCompraDatasource.createUpdatePedidoCompra(pedidos);
                        }
                    }

                    List<PedidoCompra> emitidos = pedidoCompraDatasource.getAllPedidoCompra(StatusPedido.EMITIDO);
                    List<PedidoCompra> aprovados = pedidoCompraDatasource.getAllPedidoCompra(StatusPedido.APROVADO);
                    List<PedidoCompra> rejeitados = pedidoCompraDatasource.getAllPedidoCompra(StatusPedido.REJEITADO);

                    if (emitidos.size() > 0) {
                        //publishResults(emitidos.toArray(new PedidoCompra[emitidos.size()]), PEDIDOS_EMITIDOS);
                        publishResults(emitidos, PEDIDOS_EMITIDOS);
                    }
                    if (aprovados.size() > 0) {
                        //publishResults(aprovados.toArray(new PedidoCompra[aprovados.size()]), PEDIDOS_APROVADOS);
                    }
                    if (rejeitados.size() > 0) {
                        //publishResults(rejeitados.toArray(new PedidoCompra[rejeitados.size()]), PEDIDOS_REJEITADOS);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void descarregaFila(Acesso acesso) {
        FilaPedidoCompraDAO filaDataSource = null;
        try {

            String url = getResources().getString(R.string.protocolo)
                    + App.API_REST
                    + getResources().getString(R.string.pedidocompra_path);

            filaDataSource = new FilaPedidoCompraDAO();

            List<PedidoCompra> pedidos = filaDataSource.getAllPedidoCompra();

            if (pedidos != null && pedidos.size() > 0) {

                List<PedidoCompra> tmpPedidos = new ArrayList<>();

                for (PedidoCompra pedidoCompra : pedidos) {
                    PedidoCompra tmp = new PedidoCompra();
                    tmp.set_id(pedidoCompra.get_id());
                    tmp.setStatusPedido(pedidoCompra.getStatusPedido());
                    tmp.setMotivoRejeicao(pedidoCompra.getMotivoRejeicao());
                    tmpPedidos.add(tmp);
                }

                for (PedidoCompra pedidoCompra : tmpPedidos) {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPut httpPut = new HttpPut(url + "/" + pedidoCompra.get_id());
                    httpPut.setHeader("Authorization", acesso.getToken_Type() + " " + acesso.getAccess_Token());

                    StringEntity entity = new StringEntity(new Gson().toJson(pedidoCompra), HTTP.UTF_8);

                    entity.setContentType("application/json");

                    httpPut.setEntity(entity);

                    // ResponseHandler<String> responseHandler = new BasicResponseHandler();
                    //  String str = httpclient.execute(httpPut, responseHandler);

                    HttpResponse response = httpclient.execute(httpPut);

                    // Obtem codigo de retorno HTTP
                    int statusCode = response.getStatusLine().getStatusCode();

                    if (statusCode >= 200 && statusCode <= 202) {
                        // Obtem string do Body retorno HTTP
                        ResponseHandler<String> responseHandler = new BasicResponseHandler();
                        String responseBody = responseHandler.handleResponse(response);

                        filaDataSource.deleteFilaPedidoCompra(pedidoCompra);

                        if (pedidoCompraDatasource == null) {
                            pedidoCompraDatasource = new PedidoCompraDAO();
                        }
                        pedidoCompra.setEnviado(1);
                        pedidoCompraDatasource.updatePedidoCompra(pedidoCompra);
                    } else {
                        // mensagem
                        //acesso.logoff( tem que descobri qual é a active que esta ativa na tela do usuario ); // logout
                    }
                }

            } else {
                //todo
            }
        } catch (HttpResponseException e) {
            switch (e.getStatusCode()) {
                case 404:
                    break;
                case 403:
                case 401:
                    break;
            }
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void publishResults(List<PedidoCompra> pedidos, String tipo) {
        Intent intent = new Intent(NOTIFICATION);
        String strJson = new Gson().toJson(pedidos);
        intent.putExtra(tipo, strJson);
        sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    public List<PedidoCompra> getAll() {
        return list;
    }

    public class MyBinder extends Binder {
        PedidoCompraService getService() {
            return PedidoCompraService.this;
        }
    }
}
