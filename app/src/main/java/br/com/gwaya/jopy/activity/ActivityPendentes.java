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
import br.com.gwaya.jopy.communication.PedidoCompraService;
import br.com.gwaya.jopy.model.PedidoCompra;

public class ActivityPendentes extends AbaPedidoCompra {

    private final BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String strPedidos = bundle.getString(PedidoCompraService.PEDIDOS_EMITIDOS);
                if (strPedidos != null && !strPedidos.equals("")) {
                    GsonBuilder gsonb = new GsonBuilder();
                    Gson gson = gsonb.create();
                    JSONArray j;
                    List<PedidoCompra> pedidos = null;
                    try {
                        j = new JSONArray(strPedidos);
                        pedidos = Arrays.asList(gson.fromJson(strPedidos, PedidoCompra[].class));
                        setPedidos(pedidos);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    };

    @Override
    public void clickOnItemListView(AdapterView<?> parent, View view, int position, long id, PedidoCompra pedidoCompra) {
        Intent intent = new Intent(ActivityPendentes.this, ActivityDetalhe.class);
        intent.putExtra("pedidocompra", new Gson().toJson(pedidoCompra));
        startActivityForResult(intent, 101);
    }

    @Override
    public void configureListViewDivider(ListView listView) {
        listView.setDivider(new ColorDrawable(this.getResources().getColor(R.color.emitido)));
        listView.setDividerHeight(1);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver(receiver, new IntentFilter(PedidoCompraService.NOTIFICATION));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }


    @Override
    public String getStatusPedido() {
        return "emitido";
    }

    @Override
    public String getTheTitle() {
        return "Pedidos Pendentes";
    }
}