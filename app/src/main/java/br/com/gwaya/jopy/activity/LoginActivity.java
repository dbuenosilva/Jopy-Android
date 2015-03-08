package br.com.gwaya.jopy.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import br.com.gwaya.jopy.CommonUtilities;
import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.dao.AcessoDAO;
import br.com.gwaya.jopy.model.Acesso;
import br.com.gwaya.jopy.model.RespostaLogin;
import br.com.gwaya.jopy.model.RespostaPadrao;

import static br.com.gwaya.jopy.CommonUtilities.SENDER_ID;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements LoaderCallbacks<Cursor> {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "contato@gwaya.com", "contato@gwaya.com:jopy"
    };
    public Acesso acesso;
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    //
    private String regId;
    private AcessoDAO acessoDatasource;
    private BroadcastReceiver mHandleMessageReceiver;

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandleMessageReceiver =
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String newMessage = intent.getExtras().getString(CommonUtilities.EXTRA_MESSAGE);
                    }
                };

        setContentView(R.layout.activity_login);

        GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);

        /*
        if (mHandleMessageReceiver != null) {
            registerReceiver(mHandleMessageReceiver,new IntentFilter(DISPLAY_MESSAGE_ACTION));
        }
        */
        GCMRegistrar.register(this, CommonUtilities.SENDER_ID);

        GCMRegistrar.setRegisteredOnServer(this, true);

        mLoginFormView = findViewById(R.id.email_login_form);
        mProgressView = findViewById(R.id.login_progress);

        acessoDatasource = new AcessoDAO(this.getApplicationContext());

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        Button btnEsqueceu = (Button) findViewById(R.id.btnEsqueceu);

        btnEsqueceu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                mEmailView.setError(null);

                final String email = mEmailView.getText().toString();

                if (!isEmailValid(email)) {
                    Toast toast = Toast.makeText(LoginActivity.this, "Por favor, preencha o usuário corretamente.", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    showProgress(true);
                    new EsqueceuTask(email).execute((Void) null);
                }
            }
        });

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        //(new Runnable() {
        //@Override
        //public void run() {
        acessoDatasource.open();
        List<Acesso> lst = acessoDatasource.getAllAcesso();
        acessoDatasource.close();

        if (lst.size() > 0) {
            acesso = lst.get(0);
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("ACESSO", new Gson().toJson(acesso));
            intent.putExtra("login", false);
            LoginActivity.this.startActivity(intent);
        }
        //}
        //}).run();
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        hideKeyboard();
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 0;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        public Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            String usuario = mEmail,
                    senha = mPassword;

            Boolean retorno = false;

            if (usuario != "" && senha != "") {

                GCMRegistrar.checkDevice(LoginActivity.this);
                GCMRegistrar.checkManifest(LoginActivity.this);

                regId = GCMRegistrar.getRegistrationId(LoginActivity.this.getApplicationContext());
                if (true || regId.equals("")) {
                    GCMRegistrar.register(LoginActivity.this.getApplicationContext(), SENDER_ID);
                }

                HttpClient httpclient = new DefaultHttpClient();
                String url = getResources().getString(R.string.protocolo)
                        + getResources().getString(R.string.rest_api_url)
                        + getResources().getString(R.string.oauth_path);
                HttpPost httpPost = new HttpPost(url);

                try {

                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
                    nameValuePairs.add(new BasicNameValuePair(getResources().getString(R.string.grant_type_key),
                            getResources().getString(R.string.grant_type)));
                    nameValuePairs.add(new BasicNameValuePair(getResources().getString(R.string.client_id_key),
                            getResources().getString(R.string.client_id)));
                    nameValuePairs.add(new BasicNameValuePair(getResources().getString(R.string.client_secret_key),
                            getResources().getString(R.string.client_secret)));
                    nameValuePairs.add(new BasicNameValuePair(getResources().getString(R.string.username_key),
                            usuario));
                    nameValuePairs.add(new BasicNameValuePair(getResources().getString(R.string.password_key),
                            senha));
                    nameValuePairs.add(new BasicNameValuePair(getResources().getString(R.string.deviceKey),
                            regId));
                    nameValuePairs.add(new BasicNameValuePair(getResources().getString(R.string.deviceType),
                            "galaxyS4mini"));
                    nameValuePairs.add(new BasicNameValuePair(getResources().getString(R.string.osType),
                            "android"));
                    nameValuePairs.add(new BasicNameValuePair(getResources().getString(R.string.osVersion),
                            "v19"));
                    nameValuePairs.add(new BasicNameValuePair(getResources().getString(R.string.AppVersion),
                            "v1"));

                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    try {
                        //ResponseHandler<String> responseHandler = new BasicResponseHandler();
                        //String responseData = httpclient.execute(httpPost, responseHandler);

                        HttpResponse response = httpclient.execute(httpPost);

                        // Obtem codigo de retorno HTTP
                        int statusCode = response.getStatusLine().getStatusCode();


                        if (statusCode >= 200 && statusCode <= 202) {
                            // Obtem string do Body retorno HTTP
                            ResponseHandler<String> responseHandler = new BasicResponseHandler();
                            String responseBody = responseHandler.handleResponse(response);


                            GsonBuilder gsonb = new GsonBuilder();
                            Gson gson = gsonb.create();
                            JSONObject j;
                            RespostaLogin resp = null;

                            j = new JSONObject(responseBody);
                            resp = gson.fromJson(j.toString(), RespostaLogin.class);

                            acessoDatasource.open();

                            acesso = acessoDatasource.createAcesso(resp, usuario, senha);

                            acessoDatasource.close();

                            retorno = true;
                        } else {
                            retorno = false;
                            // mensagem
                        }
                    } catch (Exception e) {
                        Toast toast = Toast.makeText(LoginActivity.this, "Você esta sem conexão com a internet, por favor tente mais tarde.", Toast.LENGTH_SHORT);
                        toast.show();

                        retorno = false;

                        e.printStackTrace();

                        if (e.getMessage().contains("refuse")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setMessage("Serviço indisponível temporariamente.")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //do things
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                    }
                } catch (Exception e) {
                    retorno = false;
                    e.printStackTrace();
                }
            } else {
                retorno = false;
            }

            return retorno;
        }

        @Override
        public void onPostExecute(final Boolean success) {
            mAuthTask = null;
            if (success) {
                //Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("ACESSO", new Gson().toJson(acesso));
                intent.putExtra("login", true);
                LoginActivity.this.startActivity(intent);
                //LoginActivity.this.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                mLoginFormView.setVisibility(View.INVISIBLE);
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
            showProgress(false);
        }

        @Override
        public void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    public class EsqueceuTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private String mensagem;

        EsqueceuTask(String email) {
            mEmail = email;
            mensagem = "Serviço indisponível. Por favor, tente novamnete mais tarde.";
        }

        @Override
        public Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            String usuario = mEmail;

            Boolean retorno = false;

            if (usuario != "") {

                HttpClient httpclient = new DefaultHttpClient();
                String url = getResources().getString(R.string.protocolo)
                        + getResources().getString(R.string.rest_api_url)
                        + getResources().getString(R.string.esqueceu_path);
                HttpPost httpPost = new HttpPost(url);
                try {
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                    nameValuePairs.add(new BasicNameValuePair(getResources().getString(R.string.username_key),
                            mEmail));

                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

//                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
//                    String responseData = httpclient.execute(httpPost, responseHandler);
                    HttpResponse response = httpclient.execute(httpPost);

                    // Obtem codigo de retorno HTTP
                    int statusCode = response.getStatusLine().getStatusCode();

                    if (statusCode >= 200 && statusCode <= 202) {
                        // Obtem string do Body retorno HTTP
                        ResponseHandler<String> responseHandler = new BasicResponseHandler();
                        String responseBody = responseHandler.handleResponse(response);

                        GsonBuilder gsonb = new GsonBuilder();
                        Gson gson = gsonb.create();
                        RespostaPadrao resp = null;

                        resp = gson.fromJson(responseBody, RespostaPadrao.class);

                        retorno = resp.getStatus();
                        mensagem = resp.getMensagem();
                    } else {
                        // mensagem

                    }

                } catch (UnsupportedEncodingException e) {
                    retorno = false;
                    e.printStackTrace();
                } catch (IOException e) {
                    retorno = false;
                    e.printStackTrace();
                } catch (Exception e) {
                    retorno = false;
                    e.printStackTrace();
                }
            }
            return retorno;
        }

        @Override
        public void onPostExecute(final Boolean success) {
            showProgress(false);
            Toast.makeText(getApplicationContext(), mensagem, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancelled() {
            showProgress(false);
            Toast.makeText(getApplicationContext(), mensagem, Toast.LENGTH_SHORT);
        }
    }
}



