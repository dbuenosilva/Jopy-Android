package br.com.gwaya.jopy.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;

import java.util.Arrays;
import java.util.List;

import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.StatusPedido;
import br.com.gwaya.jopy.communication.PedidoCompraService;
import br.com.gwaya.jopy.model.PedidoCompra;

/**
 * @author Adil Soomro
 */
public class ActivityAprovados extends AbaPedidoCompra {

    public static final String NOVA_APROV = "NOVA_APROV";

    private final BroadcastReceiver receiverNovaAprov = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String strPedidos = bundle.getString(NOVA_APROV);
                if (strPedidos != null && !strPedidos.equals("")) {
                    GsonBuilder gsonb = new GsonBuilder();
                    Gson gson = gsonb.create();
                    JSONArray jsonArray;
                    try {
                        jsonArray = new JSONArray(strPedidos);
                        setPedidos(Arrays.asList(gson.fromJson(jsonArray.toString(), PedidoCompra[].class)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    };
    private final BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String strPedidos = bundle.getString(PedidoCompraService.PEDIDOS_APROVADOS);
                if (strPedidos != null && !strPedidos.equals("")) {
                    GsonBuilder gsonb = new GsonBuilder();
                    Gson gson = gsonb.create();
                    JSONArray jsonArray;
                    List<PedidoCompra> pedidos = null;
                    try {
                        jsonArray = new JSONArray(strPedidos);
                        pedidos = Arrays.asList(gson.fromJson(jsonArray.toString(), PedidoCompra[].class));
                        setPedidos(pedidos);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    };

    @Override
    public void onResume() {
        super.onResume();

        new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(ActivityAprovados.this, PedidoCompraService.class);
                startService(intent);
            }
        }.run();

        registerReceiver(receiver, new IntentFilter(PedidoCompraService.NOTIFICATION));
        registerReceiver(receiverNovaAprov, new IntentFilter(NOVA_APROV));
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
        unregisterReceiver(receiverNovaAprov);
    }

    @Override
    public void clickOnItemListView(AdapterView<?> parent, View view, int position, long id, PedidoCompra pedidoCompra) {
        Intent intent = new Intent(ActivityAprovados.this, ActivityDetalhe.class);
        intent.putExtra("pedidocompra", new Gson().toJson(pedidoCompra));
        startActivityForResult(intent, 101);
    }

    @Override
    public void configureListViewDivider(ListView listView) {
        listView.setDivider(new ColorDrawable(this.getResources().getColor(R.color.aprovado)));
        listView.setDividerHeight(1);
    }

    @Override
    public StatusPedido getStatusPedido() {
        return StatusPedido.APROVADO;
    }

    @Override
    public String getTheTitle() {
        return "Pedidos Aprovados";
    }
}