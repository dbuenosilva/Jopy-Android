package br.com.gwaya.jopy;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;

import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import static br.com.gwaya.jopy.CommonUtilities.SENDER_ID;
import static br.com.gwaya.jopy.CommonUtilities.displayMessage;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {

    private PedidoCompraDataSource dataSource;
    public Acesso acesso;
    private Boolean login;

    @SuppressWarnings("hiding")
    private static final String TAG = "GCMIntentService";

    public GCMIntentService() {
        super(SENDER_ID);
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);
        //displayMessage(context, getString(R.string.gcm_registered));
        //ServerUtilities.register(context, registrationId, ((MainTab)context).acesso);
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
        //displayMessage(context, getString(R.string.gcm_unregistered));
        if (GCMRegistrar.isRegisteredOnServer(context)) {
            //ServerUtilities.unregister(context, registrationId, ((MainTab)context).acesso);
        } else {
            // This callback results from the call to unregister made on
            // ServerUtilities when the registration to the server failed.
            Log.i(TAG, "Ignoring unregister callback");
        }
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.i(TAG, "Received message");
        //String message = getString(R.string.gcm_message);
        String message = intent.getExtras().getString("message").toString();
        displayMessage(context, message);
        // notifies user
        generateNotification(context, message);

        //ADD POR THIAGO A.SOUSA
        //CHAMA O METODO RESPONSAVEL POR ATUALIZAR O CONTEUDO DO BANCO DE DADOS

        //RECEBE O USUARIO QUE ESTA LOGADO
        AcessoDataSource acessoDatasource = new AcessoDataSource(this.getApplicationContext());
        List<Acesso> lstAcesso = acessoDatasource.getAllAcesso();

        //VERIFICA SE EXISTE USARIO LOGADO
        if (lstAcesso.size() > 0){
            refreshDb(lstAcesso);
        }else{
            Log.i(TAG, "Não existe usuario Autenticado no momento... Impossivel Atualizar o banco de dados!");
        }
    }

    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        displayMessage(context, message);
        // notifies user
        generateNotification(context, message);

        Intent intent = new Intent(context, PedidoCompraService.class);
        context.startService(intent);
    }

    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
        displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        displayMessage(context, getString(R.string.gcm_recoverable_error,
                errorId));
        return super.onRecoverableError(context, errorId);
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
        notificationIntent = new Intent(context, MainActivity.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, context.getString(R.string.app_name), message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);

        return;
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
    private void publishResults(PedidoCompra[] pedidos, String tipo) {
        Intent intent = new Intent(PedidoCompraService.NOTIFICATION);
        intent.putExtra(tipo, new Gson().toJson(pedidos));
        GCMIntentService.this.sendBroadcast(intent);
    }

    //CRIADO POR THIAGO A. SOUSA
    //METODO RESPONSAVEL POR ATUALIZAR O BANCO DE DADOS APOS RECEBER UM PUSH
    public void refreshDb(List<Acesso> lstAcesso){
        login = true; //FORÇA O LOGIN SER SEMPRE TRUE   --ISTO SERA MODIFICADO EM BREVE

        //AcessoDataSource acessoDatasource = new AcessoDataSource(this.getApplicationContext());
        //List<Acesso> lstAcesso = acessoDatasource.getAllAcesso();

        dataSource = new PedidoCompraDataSource(this.getApplicationContext());
        String jsonMyObject = "";

        if (!jsonMyObject.equals("")) {
            acesso = new Gson().fromJson(jsonMyObject, Acesso.class);
        }
        if (login) {
            dataSource.open();
            dataSource.deleteAll();
            dataSource.close();
        }

        //setTabs();
        List<PedidoCompra> lst = null;
        try {
            dataSource.open();
            String url = getResources().getString(R.string.protocolo)
                    + getResources().getString(R.string.rest_api_url)
                    + getResources().getString(R.string.pedidocompra_path);

            String responseData = "";

            responseData = PedidoCompraService.loadFromNetwork(url, lstAcesso.get(0));

            GsonBuilder gsonb = new GsonBuilder();
            Gson gson = gsonb.create();
            JSONArray j;
            PedidoCompra[] pedidos = null;

            pedidos = gson.fromJson(responseData, PedidoCompra[].class);

            dataSource.deleteAll();
            dataSource.createUpdatePedidoCompra(pedidos, false);


            List<PedidoCompra> emitidos = dataSource.getAllPedidoCompra(MySQLiteHelper.STATUS_PEDIDO + " = 'emitido'", null);
            List<PedidoCompra> aprovados = dataSource.getAllPedidoCompra(MySQLiteHelper.STATUS_PEDIDO + " = 'aprovado'", null);
            List<PedidoCompra> rejeitados = dataSource.getAllPedidoCompra(MySQLiteHelper.STATUS_PEDIDO + " = 'rejeitado'", null);


            if (emitidos.size() > 0) {
                publishResults(emitidos.toArray(new PedidoCompra[emitidos.size()]), PedidoCompraService.PEDIDOS_EMITIDOS);
            }
            if (aprovados.size() > 0) {
                publishResults(aprovados.toArray(new PedidoCompra[aprovados.size()]), PedidoCompraService.PEDIDOS_APROVADOS);
            }
            if (rejeitados.size() > 0) {
                publishResults(rejeitados.toArray(new PedidoCompra[rejeitados.size()]), PedidoCompraService.PEDIDOS_REJEITADOS);
            }

            lst = Arrays.asList(pedidos);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dataSource.close();
        }
    }
}
