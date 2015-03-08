package br.com.gwaya.jopy.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import br.com.gwaya.jopy.R;

/**
 * Created by marcelorosa on 20/01/15.
 */
public class ActivitySplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_splash_screen);

        /*
        Bundle extras = getIntent().getExtras();

        final String strAcesso = extras.getString("ACESSO");
        final Boolean bpar = extras.getBoolean("login");*/

// METHOD 1

        /****** Create Thread that will sleep for 5 seconds *************/
        Thread background = new Thread() {
            public void run() {

                try {

                    // Thread will sleep for 5 seconds
                    sleep(3 * 1000);

                    // After 5 seconds redirect to another intent

                    Intent intent = new Intent(ActivitySplashScreen.this, ActivityLogin.class);
                    ActivitySplashScreen.this.startActivity(intent);

                    //Remove activity
                    finish();

                } catch (Exception e) {

                }
            }
        };

        // start thread
        background.start();

//METHOD 2

        /*
        new Handler().postDelayed(new Runnable() {

            // Using handler with postDelayed called runnable run method

            @Override
            public void run() {
                Intent i = new Intent(MainSplashScreen.this, FirstScreen.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, 5*1000); // wait for 5 seconds
        */
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

    }
}
