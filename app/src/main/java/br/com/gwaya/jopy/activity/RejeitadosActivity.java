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

import br.com.gwaya.jopy.MySQLiteHelper;
import br.com.gwaya.jopy.PedidoCompra;
import br.com.gwaya.jopy.PedidoCompraDataSource;
import br.com.gwaya.jopy.PedidoCompraService;
import br.com.gwaya.jopy.R;

public class RejeitadosActivity extends MyBaseActivity {

    @Override
    protected String _statusPedido(){
        return "rejeitado";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    protected ListView setPedidos(List<PedidoCompra> pedidos) {

        ListView pedidoList = super.setPedidos(pedidos);

        pedidoList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                PedidoCompra pedido = _pedidos.get(position);
                Intent intent = new Intent(RejeitadosActivity.this, DetalheActivity.class);
                intent.putExtra("pedidocompra", new Gson().toJson(pedido));
                RejeitadosActivity.this.startActivity(intent);
            }
        });

        pedidoList.setDivider(new ColorDrawable(this.getResources().getColor(R.color.rejeitado)));
        pedidoList.setDividerHeight(1);

        return pedidoList;
    }

    @Override
    protected void onResume() {
        super.onResume();

        (new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(RejeitadosActivity.this, PedidoCompraService.class);
                startService(intent);
            }
        }).run();

        registerReceiver(receiver, new IntentFilter(PedidoCompraService.NOTIFICATION));

        PedidoCompraDataSource dataSource = new PedidoCompraDataSource(getApplicationContext());

        List<PedidoCompra> rejeitados = dataSource.getAllPedidoCompra(MySQLiteHelper.STATUS_PEDIDO + " = 'rejeitado'", null);

        setPedidos(rejeitados);
    }

    @Override
    protected String getTheTitle(){
        return "Pedidos Rejeitados";
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

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
}