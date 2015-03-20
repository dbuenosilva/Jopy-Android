package br.com.gwaya.jopy.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.utils.Utils;

/**
 * Modified by pedro on 20/03/15.
 */
public class ActivitySplashScreen extends Activity {

    private static final int SEGUNDOS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        Thread background = new Thread() {
            public void run() {

                try {

                    if (!Utils.isDebuggable(ActivitySplashScreen.this)) {
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
}
