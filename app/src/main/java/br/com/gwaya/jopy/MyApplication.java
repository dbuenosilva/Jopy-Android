package br.com.gwaya.jopy;

import android.app.Application;

/**
 * Created by marcelorosa on 14/01/15.
 */
public class MyApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();

        // Initialize the singletons so their instances
        // are bound to the application process.
        initSingletons();
    }

    protected void initSingletons()
    {
        // Initialize the instance of MySingleton
        DatabaseManager.initializeInstance(new MySQLiteHelper(getApplicationContext()));
    }

    public void customAppMethod()
    {
        // Custom application method
    }
}
