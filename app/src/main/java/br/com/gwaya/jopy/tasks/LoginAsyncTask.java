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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.com.gwaya.jopy.App;
import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.dao.DadosAcessoDAO;
import br.com.gwaya.jopy.interfaces.ILoginAsyncTask;
import br.com.gwaya.jopy.model.DadosAcesso;
import br.com.gwaya.jopy.model.RespostaLogin;

/**
 * Created by pedrofsn on 03/04/15.
 */
public class LoginAsyncTask extends AsyncTask<Void, Void, Integer> {

    private Context context;
    private String regid;
    private String mEmail;
    private String mPassword;

    private ILoginAsyncTask callback;
    private DadosAcesso dadosAcesso;

    public LoginAsyncTask(Context context, String regid, String email, String password) {
        this.context = context;
        this.regid = regid;
        this.mEmail = email;
        this.mPassword = password;

        try {
            this.callback = ((ILoginAsyncTask) context);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Integer doInBackground(Void... params) {

        String usuario = mEmail;
        String senha = mPassword;
        Integer statusCode = null;

        if (!usuario.equals("") && !senha.equals("")) {

            HttpClient httpclient = new DefaultHttpClient();
            String url = context.getString(R.string.protocolo)
                    + App.API_REST
                    + context.getString(R.string.oauth_path);
            HttpPost httpPost = new HttpPost(url);

            try {

                List<NameValuePair> nameValuePairs = new ArrayList<>(4);
                    /*nameValuePairs.add(new BasicNameValuePair(context.getString(R.string.grant_type_key),
                            context.getString(R.string.grant_type)));
                    nameValuePairs.add(new BasicNameValuePair(context.getString(R.string.client_id_key),
                            context.getString(R.string.client_id)));
                    nameValuePairs.add(new BasicNameValuePair(context.getString(R.string.client_secret_key),
                            context.getString(R.string.client_secret)));
                    */
                nameValuePairs.add(new BasicNameValuePair(context.getString(R.string.username_key),
                        usuario));
                nameValuePairs.add(new BasicNameValuePair(context.getString(R.string.password_key),
                        senha));
                nameValuePairs.add(new BasicNameValuePair(context.getString(R.string.deviceKey),
                        regid));
                nameValuePairs.add(new BasicNameValuePair(context.getString(R.string.deviceType),
                        android.os.Build.MODEL));
                nameValuePairs.add(new BasicNameValuePair(context.getString(R.string.osType),
                        "android"));
                nameValuePairs.add(new BasicNameValuePair(context.getString(R.string.osVersion),
                        android.os.Build.VERSION.RELEASE));
                nameValuePairs.add(new BasicNameValuePair(context.getString(R.string.AppVersion),
                        "v1"));

                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                try {
                    //ResponseHandler<String> responseHandler = new BasicResponseHandler();
                    //String responseData = httpclient.execute(httpPost, responseHandler);

                    HttpResponse response = httpclient.execute(httpPost);

                    // Obtem codigo de retorno HTTP
                    statusCode = response.getStatusLine().getStatusCode();

                    if (statusCode >= 200 && statusCode <= 202) {
                        // Obtem string do Body retorno HTTP
                        ResponseHandler<String> responseHandler = new BasicResponseHandler();
                        String responseBody = responseHandler.handleResponse(response);

                        GsonBuilder gsonb = new GsonBuilder();
                        Gson gson = gsonb.create();
                        JSONObject j = new JSONObject(responseBody);
                        RespostaLogin resp = gson.fromJson(j.toString(), RespostaLogin.class);

                        dadosAcesso = new DadosAcessoDAO().createDadosAcesso(resp, usuario, senha);
                    }
                } catch (Exception e) {
                    statusCode = -1;

                    if (e.getMessage().contains("refuse")) {
                        statusCode = -2;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            statusCode = -10;
        }

        return statusCode;
    }

    @Override
    public void onPostExecute(Integer statusCode) {
        callback.onLogon(statusCode, dadosAcesso);
    }

    @Override
    public void onCancelled() {
        callback.onLogonFail();
    }

}
