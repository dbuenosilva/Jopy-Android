package br.com.gwaya.jopy.communication;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import br.com.gwaya.jopy.App;
import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.activity.ActivityMain;

import static br.com.gwaya.jopy.utils.CommonUtilities.displayMessage;

public class GCMIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 1;

    public GCMIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " +
                        extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                for (int i = 0; i < 5; i++) {
                    Log.i(App.TAG, "Working... " + (i + 1)
                            + "/5 @ " + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ignored) {
                    }
                }
                Log.i(App.TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
                sendNotification(extras.get("message").toString());
                Log.i(App.TAG, "Received: " + extras.get("message").toString());
                displayMessage(this.getApplicationContext(), extras.get("message").toString());


            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        App.ABA_ATUAL = 0; // Seta aba Pendentes

        Intent intent = new Intent(this, ActivityMain.class);
        intent.putExtra("msg", msg);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                intent, 0);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if(alarmSound == null){
            alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            if(alarmSound == null){
                alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(getString(R.string.app_name))
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setSound(alarmSound)
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        // DIEGO - Tentei criando o metodo getDialogPendingIntent mas também não deu certo...
        mBuilder.setContentIntent( getDialogPendingIntent(msg, intent.toString()) );
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }


    private PendingIntent getDialogPendingIntent(String dialogText,
                                                 String intentname) {
        return PendingIntent.getActivity(
                this,
                dialogText.hashCode(),
                new Intent()
                        .putExtra(Intent.EXTRA_TEXT, dialogText)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .setAction(intentname), 0);

    }
}
