package br.com.gwaya.jopy;

import android.app.Application;
import android.content.Context;

import br.com.gwaya.jopy.dao.DatabaseManager;
import br.com.gwaya.jopy.dao.MySQLiteHelper;

/**
 * Created by marcelorosa on 14/01/15.
 */
public class App extends Application {

    public static int ABA_ATUAL = 0;
    public static String API_REST;
    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
/*
        if (new Utils().isDebuggable(this)) {
  */
        API_REST = "homologacao.api.jopy.gwaya.com.br";
        /*} else {
            API_REST = "api.jopy.gwaya.com.br";
        }
*/
        initSingletons();
    }

    protected void initSingletons() {
        DatabaseManager.initializeInstance(new MySQLiteHelper(getApplicationContext()));
    }
}
