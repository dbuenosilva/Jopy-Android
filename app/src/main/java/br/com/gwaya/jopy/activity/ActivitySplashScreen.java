package br.com.gwaya.jopy.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;

import br.com.gwaya.jopy.R;

/**
 * Created by marcelorosa on 20/01/15.
 */
public class ActivitySplashScreen extends Activity {

    private static final int SEGUNDOS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_splash_screen);

        Thread background = new Thread() {
            public void run() {

                try {

                    if (!isDebuggable()) {
                        sleep(SEGUNDOS * 1000);
                    }

                    Intent intent = new Intent(ActivitySplashScreen.this, ActivityLogin.class);
                    ActivitySplashScreen.this.startActivity(intent);

                    finish();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        background.start();
    }

    private boolean isDebuggable() {
        return (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
    }
}
