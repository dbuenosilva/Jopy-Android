package br.com.gwaya.jopy.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.InetAddress;

import br.com.gwaya.jopy.App;

/**
 * Created by pedro on 13/03/15.
 */
public class Utils {

    public static boolean isDebuggable(Context context) {
        return (0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
    }

    public static boolean isConectado() {
        ConnectivityManager cm = (ConnectivityManager) App.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return !(ni == null) && hasConectividade();
    }

    private static boolean hasConectividade() {
        try {
            return !("".equals(InetAddress.getByName("www.google.com")));
        } catch (Exception e) {
            return false;
        }

    }
}
