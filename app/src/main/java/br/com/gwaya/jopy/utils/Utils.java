package br.com.gwaya.jopy.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;

/**
 * Created by pedro on 13/03/15.
 */
public class Utils {

    public static boolean isDebuggable(Context context) {
        return (0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
    }

}
