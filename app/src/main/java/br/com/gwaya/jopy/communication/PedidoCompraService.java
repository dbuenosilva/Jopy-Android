package br.com.gwaya.jopy.communication;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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
import br.com.gwaya.jopy.dao.DadosAcessoDAO;
import br.com.gwaya.jopy.dao.FilaPedidoCompraDAO;
import br.com.gwaya.jopy.dao.PedidoCompraDAO;
import br.com.gwaya.jopy.enums.StatusPedido;
import br.com.gwaya.jopy.model.DadosAcesso;
import br.com.gwaya.jopy.model.PedidoCompra;

public class PedidoCompraService extends IntentService {

    public static final String NOTIFICATION = "br.com.gwaya.android.service.receiver";

    private PedidoCompraDAO pedidoCompraDatasource = new PedidoCompraDAO();

    public PedidoCompraService() {
        super("PedidoCompraService");
    }

    public static String loadFromNetwork(String urlString, DadosAcesso dadosAcesso, Context context) {
        String responseBody = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(urlString);

            httpGet.setHeader("Authorization", dadosAcesso.getToken_Type() + " " + dadosAcesso.getAccess_Token());

            HttpResponse response = httpclient.execute(httpGet);

            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode >= 200 && statusCode <= 202) {
                // Obtem string do Body retorno HTTP
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                responseBody = responseHandler.handleResponse(response);
            } else {
                DadosAcesso.logoff(context, statusCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return responseBody;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            List<DadosAcesso> lstDadosAcesso = new DadosAcessoDAO().getAllDadosAcesso();

            if (lstDadosAcesso.size() > 0) {

                DadosAcesso dadosAcesso = lstDadosAcesso.get(0);

                String url = getResources().getString(R.string.protocolo)
                        + App.API_REST
                        + getResources().getString(R.string.pedidocompra_path),
                        dtMod = pedidoCompraDatasource.ultimoSync();

                if (dtMod != null) {
                    url += "?gte=" + dtMod;
                }

                descarregaFila(dadosAcesso);

                String responseBody = loadFromNetwork(url, dadosAcesso, this.getApplicationContext());

                GsonBuilder gsonb = new GsonBuilder();
                Gson gson = gsonb.create();

                JsonObject jsPedidosObj = gson.fromJson(responseBody, JsonObject.class);
                JsonArray jsPedidosArray = jsPedidosObj.getAsJsonArray("pedidos");

                PedidoCompra[] pedidos = gson.fromJson(jsPedidosArray, PedidoCompra[].class);

                if (pedidos != null && pedidos.length > 0) {

                    // Diego Bueno - 10/02/2015 - verifica se pedido já existe, caso sim, deleta e inclui com nova alteração
                    for (PedidoCompra pedido : pedidos) {
                        if (pedidoCompraDatasource.ExistePedidoCompra(pedido.get_id())) {
                            pedidoCompraDatasource.deletePedidoCompra(pedido);

                            // Se nao estiver deletado, atualiza
                            if (!pedido.getStatusPedido().equals("deletado")) {
                                pedidoCompraDatasource.createUpdatePedidoCompra(pedido);
                            }

                        } else {

                            // Se nao estiver deletado, inclui
                            if (!pedido.getStatusPedido().equals("deletado")) {
                                pedidoCompraDatasource.createUpdatePedidoCompra(pedidos);
                            }

                        }
                    }

                    publishResults(StatusPedido.getFromText(pedidos[0].getStatusPedido()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void descarregaFila(DadosAcesso dadosAcesso) {
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
                    httpPut.setHeader("Authorization", dadosAcesso.getToken_Type() + " " + dadosAcesso.getAccess_Token());

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
                        //String responseBody = responseHandler.handleResponse(response);

                        filaDataSource.deleteFilaPedidoCompra(pedidoCompra);

                        pedidoCompra.setEnviado(1);
                        new PedidoCompraDAO().updatePedidoCompra(pedidoCompra);
                    } else {
                        // mensagem
                        //dadosAcesso.logoff( tem que descobri qual é a active que esta ativa na tela do usuario ); // logout
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

    private void publishResults(StatusPedido statusPedido) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra("statusPedido", statusPedido.getValor());
        sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}
