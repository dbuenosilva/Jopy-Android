package br.com.gwaya.jopy;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.crashlytics.android.Crashlytics;

import br.com.gwaya.jopy.activity.ActivityMain;
import br.com.gwaya.jopy.activity.abas.ActivityPendentes;
import br.com.gwaya.jopy.dao.DatabaseManager;
import br.com.gwaya.jopy.dao.MySQLiteHelper;
import br.com.gwaya.jopy.utils.Utils;
import io.fabric.sdk.android.Fabric;

/**
 * Modified by pedro on 20/03/15.
 */
public class App extends Application {

    public static final int NOTIFICATION_ID = 123456;

    public static int ABA_ATUAL = ActivityPendentes.ID;

    public static String API_REST;
    public static String TAG = "teste";
    private static Context context;

    public static Context getContext() {
        return context;
    }

    public static void sendNotification(String mensagem) {

        Notification note;

        Intent intent = new Intent(context, ActivityMain.class);
        if (mensagem != null) {
            intent.putExtra("msg", mensagem);
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        Bitmap background = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_launcher);

        NotificationCompat.Builder mNotification = new NotificationCompat.Builder(context)
                .setLargeIcon(background)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(context.getString(R.string.app_name))
                .setDefaults(Notification.DEFAULT_ALL)
                .setSound(getAlarmSound())
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(mensagem))
                .setContentText(mensagem);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
        mNotification.setContentIntent(pi);

        note = mNotification.build();
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, note);

        ABA_ATUAL = ActivityPendentes.ID;
    }

    private static Uri getAlarmSound() {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (alarmSound == null) {
            alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            if (alarmSound == null) {
                alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        return alarmSound;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        context = this;

        if (Utils.isDebuggable(this)) {
            API_REST = "homologacao.api.jopy.gwaya.com.br";
        } else {
            API_REST = "api.jopy.gwaya.com.br";
        }

        DatabaseManager.initializeInstance(new MySQLiteHelper(getApplicationContext()));
    }
}
