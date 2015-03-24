package br.com.gwaya.jopy.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
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
import java.util.ArrayList;
import java.util.List;

import br.com.gwaya.jopy.App;
import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.dao.AcessoDAO;
import br.com.gwaya.jopy.model.Acesso;
import br.com.gwaya.jopy.model.RespostaLogin;
import br.com.gwaya.jopy.model.RespostaPadrao;

public class ActivityLogin extends Activity implements LoaderCallbacks<Cursor> {

    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public Acesso acesso;
    private String SENDER_ID = "569142009262";
    private GoogleCloudMessaging gcm;
    private String regid;
    private UserLoginTask mAuthTask = null;

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private AcessoDAO acessoDatasource;
    private int contadorExibicaoMenuSecreto = 0;


    public String GetRegId() {
        return (this.regid);
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupGCM();

        setContentView(R.layout.activity_login);

        mLoginFormView = findViewById(R.id.email_login_form);
        mProgressView = findViewById(R.id.login_progress);

        acessoDatasource = new AcessoDAO();

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
                    Toast toast = Toast.makeText(ActivityLogin.this, "Por favor, preencha o usuário corretamente.", Toast.LENGTH_SHORT);
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

        List<Acesso> lst = acessoDatasource.getAllAcesso();

        if (lst.size() > 0) {
            acesso = lst.get(0);
            Intent intent = new Intent(ActivityLogin.this, ActivityMain.class);
            intent.putExtra("ACESSO", new Gson().toJson(acesso));
            intent.putExtra("login", false);
            ActivityLogin.this.startActivity(intent);
        }

        findViewById(R.id.linearLayout).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ++contadorExibicaoMenuSecreto;
                if (contadorExibicaoMenuSecreto >= 10) {
                    exibirAlertaSecreto();
                } else if (contadorExibicaoMenuSecreto == 5) {
                    Toast.makeText(ActivityLogin.this, getString(R.string.quase_la), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mEmailView.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    mPasswordView.requestFocus();
                }
                return false;
            }
        });
    }

    private void exibirAlertaSecreto() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.app_name))
                .setMessage(App.API_REST)
                .setNeutralButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void setupGCM() {
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(this);

            if (regid.isEmpty()) {
                registerInBackground();
            }
        } else {
            Toast.makeText(this, "Este dispositivo não possui o Google Play Services APK instalado.", Toast.LENGTH_LONG).show();
            finish();
            System.exit(0);
        }
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
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
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
        List<String> emails = new ArrayList<>();
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
                new ArrayAdapter<>(ActivityLogin.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId != null && registrationId.isEmpty()) {
            Log.i(App.TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(App.TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the registration ID in your app is up to you.
        return getSharedPreferences(ActivityLogin.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(ActivityLogin.this);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID = " + regid;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the registration ID - no need to register again.
                    storeRegistrationId(ActivityLogin.this, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Toast.makeText(ActivityLogin.this, msg, Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    private void sendRegistrationIdToBackend() {
        // Your implementation here.
    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(App.TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(App.TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Integer> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        public Integer doInBackground(Void... params) {

            String usuario = mEmail;
            String senha = mPassword;
            Integer statusCode = null;

            if (!usuario.equals("") && !senha.equals("")) {

                HttpClient httpclient = new DefaultHttpClient();
                String url = getResources().getString(R.string.protocolo)
                        + App.API_REST
                        + getResources().getString(R.string.oauth_path);
                HttpPost httpPost = new HttpPost(url);

                try {

                    List<NameValuePair> nameValuePairs = new ArrayList<>(4);
                    /*nameValuePairs.add(new BasicNameValuePair(getResources().getString(R.string.grant_type_key),
                            getResources().getString(R.string.grant_type)));
                    nameValuePairs.add(new BasicNameValuePair(getResources().getString(R.string.client_id_key),
                            getResources().getString(R.string.client_id)));
                    nameValuePairs.add(new BasicNameValuePair(getResources().getString(R.string.client_secret_key),
                            getResources().getString(R.string.client_secret)));
                    */
                    nameValuePairs.add(new BasicNameValuePair(getResources().getString(R.string.username_key),
                            usuario));
                    nameValuePairs.add(new BasicNameValuePair(getResources().getString(R.string.password_key),
                            senha));
                    nameValuePairs.add(new BasicNameValuePair(getResources().getString(R.string.deviceKey),
                            GetRegId())); //regId
                    nameValuePairs.add(new BasicNameValuePair(getResources().getString(R.string.deviceType),
                            android.os.Build.MODEL));
                    nameValuePairs.add(new BasicNameValuePair(getResources().getString(R.string.osType),
                            "android"));
                    nameValuePairs.add(new BasicNameValuePair(getResources().getString(R.string.osVersion),
                            android.os.Build.VERSION.RELEASE));
                    nameValuePairs.add(new BasicNameValuePair(getResources().getString(R.string.AppVersion),
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
                            JSONObject j;
                            RespostaLogin resp = null;

                            j = new JSONObject(responseBody);
                            resp = gson.fromJson(j.toString(), RespostaLogin.class);


                            acesso = acessoDatasource.createAcesso(resp, usuario, senha);
                        }
                    } catch (Exception e) {
                        Toast.makeText(ActivityLogin.this, "Você esta sem conexão com a internet, por favor tente mais tarde.", Toast.LENGTH_SHORT).show();

                        statusCode = -1;

                        e.printStackTrace();

                        if (e.getMessage().contains("refuse")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityLogin.this);
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
                    e.printStackTrace();
                }
            } else {
                statusCode = -10;
            }

            return statusCode;
        }

        @Override
        public void onPostExecute(Integer statusCode) {
            mAuthTask = null;
            if (statusCode != null) {
                switch (statusCode) {
                    case 401:
                        Toast.makeText(ActivityLogin.this, getString(R.string.acesso_nao_autorizado), Toast.LENGTH_SHORT).show();
                        break;
                    case -10:
                        mPasswordView.setError(getString(R.string.error_incorrect_password));
                        mPasswordView.requestFocus();
                        break;
                    default:
                        Intent intent = new Intent(ActivityLogin.this, ActivityMain.class);
                        intent.putExtra("ACESSO", new Gson().toJson(acesso));
                        intent.putExtra("login", true);
                        ActivityLogin.this.startActivity(intent);
                        mLoginFormView.setVisibility(View.INVISIBLE);
                        break;
                }
                showProgress(false);
            }
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
            Boolean retorno = false;

            if (mEmail != null && !mEmail.equals("")) {

                HttpClient httpclient = new DefaultHttpClient();
                String url = getResources().getString(R.string.protocolo)
                        + App.API_REST
                        + getResources().getString(R.string.esqueceu_path);
                HttpPost httpPost = new HttpPost(url);
                try {
                    List<NameValuePair> nameValuePairs = new ArrayList<>(1);
                    nameValuePairs.add(new BasicNameValuePair(getResources().getString(R.string.username_key),
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
                        RespostaPadrao resp = null;

                        resp = gson.fromJson(responseBody, RespostaPadrao.class);

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
        public void onPostExecute(final Boolean success) {
            showProgress(false);
            Toast.makeText(getApplicationContext(), mensagem, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancelled() {
            showProgress(false);
            Toast.makeText(getApplicationContext(), mensagem, Toast.LENGTH_SHORT).show();
        }
    }
}



