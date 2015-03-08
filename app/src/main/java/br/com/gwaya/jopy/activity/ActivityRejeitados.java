package br.com.gwaya.jopy.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.communication.PedidoCompraService;
import br.com.gwaya.jopy.dao.MySQLiteHelper;
import br.com.gwaya.jopy.dao.PedidoCompraDAO;
import br.com.gwaya.jopy.model.PedidoCompra;

public class ActivityRejeitados extends ActivityAba {

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String strPedidos = bundle.getString(PedidoCompraService.PEDIDOS_REJEITADOS);
                if (strPedidos != null && strPedidos != "") {
                    GsonBuilder gsonb = new GsonBuilder();
                    Gson gson = gsonb.create();
                    JSONArray j;
                    List<PedidoCompra> pedidos = new ArrayList<PedidoCompra>();
                    try {
                        j = new JSONArray(strPedidos);
                        pedidos = Arrays.asList(gson.fromJson(j.toString(), PedidoCompra[].class));
                        setPedidos(pedidos);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    };

    @Override
    public String _statusPedido() {
        return "rejeitado";
    }

    @Override
    public ListView setPedidos(List<PedidoCompra> pedidos) {

        ListView pedidoList = super.setPedidos(pedidos);

        pedidoList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                PedidoCompra pedido = getPedidoCompraList().get(position);
                Intent intent = new Intent(ActivityRejeitados.this, ActivityDetalhe.class);
                intent.putExtra("pedidocompra", new Gson().toJson(pedido));
                ActivityRejeitados.this.startActivity(intent);
            }
        });

        pedidoList.setDivider(new ColorDrawable(this.getResources().getColor(R.color.rejeitado)));
        pedidoList.setDividerHeight(1);

        return pedidoList;
    }

    @Override
    public void onResume() {
        super.onResume();

        (new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(ActivityRejeitados.this, PedidoCompraService.class);
                startService(intent);
            }
        }).run();

        registerReceiver(receiver, new IntentFilter(PedidoCompraService.NOTIFICATION));

        List<PedidoCompra> rejeitados = new PedidoCompraDAO().getAllPedidoCompra(MySQLiteHelper.STATUS_PEDIDO + " = 'rejeitado'", null);

        setPedidos(rejeitados);
    }

    @Override
    public String getTheTitle() {
        return "Pedidos Rejeitados";
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }
}