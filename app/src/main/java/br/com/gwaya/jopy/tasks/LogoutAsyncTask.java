package br.com.gwaya.jopy.tasks;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import br.com.gwaya.jopy.App;
import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.dao.PedidoCompraDAO;
import br.com.gwaya.jopy.interfaces.ILogout;
import br.com.gwaya.jopy.model.DadosAcesso;

/**
 * Created by diego on 20/03/15.
 */
public class LogoutAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private Context context;
    private PedidoCompraDAO dao;
    private DadosAcesso dadosAcesso;
    private boolean running = true;
    private ILogout callback;

    public LogoutAsyncTask(Context context, DadosAcesso dadosAcesso) {
        this.dao = new PedidoCompraDAO();
        this.context = context;
        this.dadosAcesso = dadosAcesso;
        this.callback = (ILogout) context;
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
                + context.getResources().getString(R.string.logout_path);

        HttpGet httpGet = new HttpGet(url);
        if (dadosAcesso != null) {
            httpGet.setHeader("Authorization", dadosAcesso.getToken_Type() + " " + dadosAcesso.getAccess_Token());

            try {
                HttpResponse response = httpclient.execute(httpGet);

                int statusCode = response.getStatusLine().getStatusCode();

                return statusCode >= 200 && statusCode <= 202;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        callback.logout(result && running);
        running = false;
    }

    public boolean isRunning() {
        return running;
    }
}