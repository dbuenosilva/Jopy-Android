package br.com.gwaya.jopy.tasks;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import br.com.gwaya.jopy.App;
import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.dao.PedidoCompraDAO;
import br.com.gwaya.jopy.model.Acesso;

/**
 * Created by diego on 20/03/15.
 */

public class LogoutAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private Context context;
    private PedidoCompraDAO dao;
    private Acesso acesso;
    private boolean running = true;

    public LogoutAsyncTask(Context context, Acesso acesso) {
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
                + context.getResources().getString(R.string.logout_path);

        HttpGet httpGet = new HttpGet(url);
        if (acesso != null) {
            httpGet.setHeader("Authorization", acesso.getToken_Type() + " " + acesso.getAccess_Token());

            try {
                httpclient.execute(httpGet);
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
            running = false;
        } else {
            onCancelled();
        }
    }
}