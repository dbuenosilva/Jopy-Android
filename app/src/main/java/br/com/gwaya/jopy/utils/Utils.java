package br.com.gwaya.jopy.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import br.com.gwaya.jopy.App;

/**
 * Created by pedro on 13/03/15.
 */
public class Utils {

    private static Typeface fonteElis;

    public static boolean isDebuggable(Context context) {
        return (0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
    }

    public static boolean isConectado() {
        ConnectivityManager cm = (ConnectivityManager) App.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return !(ni == null);
    }

    public static void aplicarFonteMyriadPro(final Context context, final View view) {

        if (fonteElis == null)
            fonteElis = Typeface.createFromAsset(context.getAssets(), "MyriadPro-Regular.otf");

        if (context != null && view != null) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (view instanceof TextView) {
                        ((TextView) view).setTypeface(fonteElis);
                    } else if (view instanceof EditText) {
                        ((EditText) view).setTypeface(fonteElis);
                    } else if (view instanceof Button) {
                        ((Button) view).setTypeface(fonteElis);
                    }
                }
            });
        }
    }
}
