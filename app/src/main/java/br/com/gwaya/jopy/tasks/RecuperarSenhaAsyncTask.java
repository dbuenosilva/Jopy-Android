package br.com.gwaya.jopy.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import br.com.gwaya.jopy.App;
import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.interfaces.IRecuperarSenhaAsyncTask;
import br.com.gwaya.jopy.model.RespostaPadrao;

/**
 * Created by pedrofsn on 03/04/15.
 */
public class RecuperarSenhaAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private Context context;
    private String mEmail;
    private String mensagem;

    private IRecuperarSenhaAsyncTask callback;

    public RecuperarSenhaAsyncTask(Context context, String email) {
        this.context = context;
        this.mEmail = email;
        this.mensagem = "Serviço indisponível. Por favor, tente novamnete mais tarde.";

        try {
            this.callback = ((IRecuperarSenhaAsyncTask) context);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    @Override
    public Boolean doInBackground(Void... params) {
        Boolean retorno = false;

        if (mEmail != null && !mEmail.equals("")) {

            HttpClient httpclient = new DefaultHttpClient();
            String url = context.getString(R.string.protocolo)
                    + App.API_REST
                    + context.getString(R.string.esqueceu_path);
            HttpPost httpPost = new HttpPost(url);
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<>(1);
                nameValuePairs.add(new BasicNameValuePair(context.getString(R.string.username_key),
                        mEmail));

                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpclient.execute(httpPost);

                // Obtem codigo de retorno HTTP
                int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode >= 200 && statusCode <= 202) {
                    // Obtem string do Body retorno HTTP
                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
                    String responseBody = responseHandler.handleResponse(response);

                    GsonBuilder gsonb = new GsonBuilder();
                    Gson gson = gsonb.create();

                    RespostaPadrao resp = gson.fromJson(responseBody, RespostaPadrao.class);

                    retorno = resp.getStatus();
                    mensagem = resp.getMensagem();
                }

            } catch (Exception e) {
                retorno = false;
                e.printStackTrace();
            }
        }
        return retorno;
    }

    @Override
    public void onPostExecute(Boolean success) {
        callback.onResultadoRecuperarSenha(mensagem);
    }

    @Override
    public void onCancelled() {
        callback.onResultadoRecuperarSenha(mensagem);
    }

}
