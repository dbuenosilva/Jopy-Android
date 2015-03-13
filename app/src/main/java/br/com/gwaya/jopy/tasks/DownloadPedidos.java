package br.com.gwaya.jopy.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.Arrays;
import java.util.List;

import br.com.gwaya.jopy.App;
import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.dao.PedidoCompraDAO;
import br.com.gwaya.jopy.interfaces.IDownloadPedidos;
import br.com.gwaya.jopy.interfaces.ISalvarPedidosCompraAsyncTask;
import br.com.gwaya.jopy.model.Acesso;
import br.com.gwaya.jopy.model.PedidoCompra;

/**
 * Created by pedrofsn on 08/03/2015.
 */
public class DownloadPedidos extends AsyncTask<Void, Void, List<PedidoCompra>> {

    private Context context;
    private PedidoCompraDAO dao;
    private Acesso acesso;
    private IDownloadPedidos callback;
    private SalvarPedidosCompraAsyncTask asyncTask;
    private ISalvarPedidosCompraAsyncTask callbackNovosPedidos;

    public DownloadPedidos(Context context, Acesso acesso) {
        this.callbackNovosPedidos = (ISalvarPedidosCompraAsyncTask) context;
        this.callback = (IDownloadPedidos) context;
        this.dao = new PedidoCompraDAO();
        this.context = context;
        this.acesso = acesso;
    }

    @Override
    public List<PedidoCompra> doInBackground(Void... params) {

        List<PedidoCompra> pedidos = null;

        HttpClient httpclient = new DefaultHttpClient();

        String url = context.getResources().getString(R.string.protocolo)
                + App.API_REST
                + context.getResources().getString(R.string.pedidocompra_path),
                dtMod = dao.ultimoSync();

        if (dtMod != null) {
            url += "?gte=" + dtMod;
        }

        HttpGet httpGet = new HttpGet(url);
        if (acesso != null) {
            httpGet.setHeader("Authorization", acesso.getToken_Type() + " " + acesso.getAccess_Token());

            try {
                HttpResponse response = httpclient.execute(httpGet);

                // Obtem codigo de retorno HTTP
                int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode >= 200 && statusCode <= 202) {
                    // Obtem string do Body retorno HTTP
                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
                    String responseBody = responseHandler.handleResponse(response);

                    GsonBuilder gsonb = new GsonBuilder();
                    Gson gson = gsonb.create();

                    pedidos = Arrays.asList(gson.fromJson(responseBody, PedidoCompra[].class));
                } else {
                    callback.logoff(context, statusCode);
                }


            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) {
                    callback.showFalhaAoBaixar();
                }
            }
        }
        return pedidos;
    }

    @Override
    public void onPostExecute(final List<PedidoCompra> pedidos) {
        if (pedidos != null && pedidos.size() > 0) {

            if (asyncTask == null) {
                asyncTask = new SalvarPedidosCompraAsyncTask(callbackNovosPedidos, pedidos);
                asyncTask.execute();
            } else {
                if (!asyncTask.isRunning()) {
                    asyncTask.execute();
                }
            }
        } else {
            if (callback != null) {
                callback.showSemNovosProdutos();
            }
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (callback != null) {
            callback.showFalhaAoBaixar();
        }
    }
}
