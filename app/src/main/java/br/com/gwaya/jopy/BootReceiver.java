package br.com.gwaya.jopy;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

/**
 * This BroadcastReceiver automatically (re)starts the alarm when the device is
 * rebooted. This receiver is set to be disabled (android:enabled="false") in the
 * application's manifest file. When the user sets the alarm, the receiver is enabled.
 * When the user cancels the alarm, the receiver is disabled, so that rebooting the
 * device will not trigger this receiver.
 */
// BEGIN_INCLUDE(autostart)
public class BootReceiver extends BroadcastReceiver {
	
 // restart service every 30 seconds
    private static final long REPEAT_TIME = 1000 * 60 * 60;

    @Override
    public void onReceive(Context context, Intent intent) {
      AlarmManager service = (AlarmManager) context
          .getSystemService(Context.ALARM_SERVICE);
      Intent i = new Intent(context, PedidoCompraReceiver.class);
      PendingIntent pending = PendingIntent.getBroadcast(context, 0, i,
          PendingIntent.FLAG_CANCEL_CURRENT);
      Calendar cal = Calendar.getInstance();
      // start 30 seconds after boot completed
      cal.add(Calendar.SECOND, 30);
      // fetch every 30 seconds
      // InexactRepeating allows Android to optimize the energy consumption
      service.setInexactRepeating(AlarmManager.RTC_WAKEUP,
          cal.getTimeInMillis(), REPEAT_TIME, pending);

      // service.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
      // REPEAT_TIME, pending);

    }
}
//END_INCLUDE(autostart)
