package br.com.gwaya.jopy;

import android.app.Application;
import android.content.Context;

import br.com.gwaya.jopy.dao.DatabaseManager;
import br.com.gwaya.jopy.dao.MySQLiteHelper;
import br.com.gwaya.jopy.utils.Utils;

/**
 * Modified by pedro on 20/03/15.
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

        if (Utils.isDebuggable(this)) {
            API_REST = "homologacao.api.jopy.gwaya.com.br";
        } else {
            API_REST = "api.jopy.gwaya.com.br";
        }

        DatabaseManager.initializeInstance(new MySQLiteHelper(getApplicationContext()));
    }
}
