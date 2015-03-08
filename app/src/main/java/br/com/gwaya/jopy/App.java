package br.com.gwaya.jopy;

import android.app.Application;

import br.com.gwaya.jopy.dao.DatabaseManager;
import br.com.gwaya.jopy.dao.MySQLiteHelper;

/**
 * Created by marcelorosa on 14/01/15.
 */
public class App extends Application {

    public static int ABA_ATUAL = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        initSingletons();
    }

    protected void initSingletons() {
        DatabaseManager.initializeInstance(new MySQLiteHelper(getApplicationContext()));
    }

}
