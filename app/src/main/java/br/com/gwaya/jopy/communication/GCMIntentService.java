package br.com.gwaya.jopy.communication;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.google.android.gcm.GCMBaseIntentService;

import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.activity.ActivityMain;

import static br.com.gwaya.jopy.utils.CommonUtilities.SENDER_ID;
import static br.com.gwaya.jopy.utils.CommonUtilities.displayMessage;

public class GCMIntentService extends GCMBaseIntentService {

    public GCMIntentService() {
        super(SENDER_ID);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private static void generateNotification(Context context, String message) {
        int icon = R.drawable.concebra_pro_64;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification.Builder builder =
                new Notification.Builder(context)
                        .setSmallIcon(icon)
                                //.setLargeIcon()
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(message)
                        .setDefaults(Notification.DEFAULT_ALL) // requires VIBRATE permission
                        .setStyle(new Notification.BigTextStyle()
                                        .bigText(message)
                                //.setSummaryText(message)
                        );
        //builder.setVisibility(Notification.VISIBILITY_PUBLIC);
        Notification notification;
        Intent notificationIntent;
        PendingIntent intent;

        notification = builder.build();
        //notification.contentView.setImageViewResource(android.R.id.icon, R.drawable.concebra_pro_24);
        notificationIntent = new Intent(context, ActivityMain.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, context.getString(R.string.app_name), message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);

        /*
        notification = new Notification(icon, message, when);
        String title = context.getString(R.string.app_name);
        notificationIntent = new Intent(context, MainActivity.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);*/
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {

    }

    @Override
    protected void onUnregistered(Context context, String s) {

    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        String message = intent.getExtras().getString("message");
        displayMessage(context, message);
        generateNotification(context, message);
    }

    @Override
    protected void onDeletedMessages(Context context, int total) {
        String message = getString(R.string.gcm_deleted, total);
        displayMessage(context, message);
        generateNotification(context, message);

        Intent intent = new Intent(context, PedidoCompraService.class);
        context.startService(intent);
    }

    @Override
    public void onError(Context context, String errorId) {
        displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        displayMessage(context, getString(R.string.gcm_recoverable_error,
                errorId));
        return super.onRecoverableError(context, errorId);
    }


}
