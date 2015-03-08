package br.com.gwaya.jopy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class PedidoCompraReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, PedidoCompraService.class);
        context.startService(service);
    }
}
