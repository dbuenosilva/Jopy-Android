package br.com.gwaya.jopy.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

    public static void ocultarTeclado(Activity activity) {
        // Check if no view has focus:
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
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

    public static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
}
