package br.com.gwaya.jopy.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import br.com.gwaya.jopy.App;
import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.dao.PedidoCompraDAO;
import br.com.gwaya.jopy.interfaces.IDownloadPedidos;
import br.com.gwaya.jopy.model.Acesso;
import br.com.gwaya.jopy.model.PedidoCompra;

/**
 * Created by pedrofsn on 08/03/2015.
 */
public class DownloadPedidosAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private Context context;
    private PedidoCompraDAO dao;
    private Acesso acesso;
    private IDownloadPedidos callback;
    private boolean running = true;

    public DownloadPedidosAsyncTask(Context context, Acesso acesso) {
        this.callback = (IDownloadPedidos) context;
        this.dao = new PedidoCompraDAO();
        this.context = context;
        this.acesso = acesso;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        running = true;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
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

                    JsonObject jsPedidosObj = gson.fromJson(responseBody, JsonObject.class);

                    JsonArray jsPedidosArray = jsPedidosObj.getAsJsonArray("pedidos");

                    PedidoCompra[] array = gson.fromJson(jsPedidosArray, PedidoCompra[].class);

                    if (array.length > 0 && running) {
                        dao.createUpdatePedidoCompra(array);
                        return true;
                    }
                } else {
                    callback.logoff(context, statusCode);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (result && running) {
            callback.pedidosBaixadosForamSalvosNoBancoComSucesso();
            running = false;
        } else {
            onCancelled();
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (callback != null && running) {
            callback.showFalhaAoBaixar();
        }
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
