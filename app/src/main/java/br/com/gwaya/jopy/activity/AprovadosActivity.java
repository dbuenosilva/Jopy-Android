package br.com.gwaya.jopy.activity;

import android.app.Activity;
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

import br.com.gwaya.jopy.PedidoCompraService;
import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.dao.MySQLiteHelper;
import br.com.gwaya.jopy.dao.PedidoCompraDataSource;
import br.com.gwaya.jopy.model.PedidoCompra;

/**
 * @author Adil Soomro
 */
public class AprovadosActivity extends MyBaseActivity {

    public static String NOVA_APROV = "NOVA_APROV";
    private BroadcastReceiver receiverNovaAprov = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String strPedidos = bundle.getString(NOVA_APROV);
                if (strPedidos != null && strPedidos != "") {
                    GsonBuilder gsonb = new GsonBuilder();
                    Gson gson = gsonb.create();
                    JSONArray j;
                    List<PedidoCompra> pedidos = null;
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
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String strPedidos = bundle.getString(PedidoCompraService.PEDIDOS_APROVADOS);
                if (strPedidos != null && strPedidos != "") {
                    GsonBuilder gsonb = new GsonBuilder();
                    Gson gson = gsonb.create();
                    JSONArray j;
                    List<PedidoCompra> pedidos = null;
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
    protected ListView setPedidos(List<PedidoCompra> pedidos) {

        ListView pedidoList = super.setPedidos(pedidos);

        pedidoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                currentPosition = position;

                Activity tab = (Activity) AprovadosActivity.this.getParent();

                PedidoCompra pedido = _pedidos.get(position);
                Intent intent = new Intent(tab, DetalheActivity.class);
                intent.putExtra("pedidocompra", new Gson().toJson(pedido));

                AprovadosActivity.this.startActivityForResult(intent, 101);
            }
        });

        pedidoList.setDivider(new ColorDrawable(this.getResources().getColor(R.color.aprovado)));
        pedidoList.setDividerHeight(1);

        return pedidoList;
    }

    @Override
    protected String getTheTitle() {
        return "Pedidos Aprovados";
    }

    @Override
    protected void onResume() {
        super.onResume();

        (new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(AprovadosActivity.this, PedidoCompraService.class);
                startService(intent);
            }
        }).run();

        registerReceiver(receiver, new IntentFilter(PedidoCompraService.NOTIFICATION));
        registerReceiver(receiverNovaAprov, new IntentFilter(NOVA_APROV));

        (new Runnable() {
            @Override
            public void run() {

                PedidoCompraDataSource dataSource = new PedidoCompraDataSource(getApplicationContext());

                List<PedidoCompra> aprovados = dataSource.getAllPedidoCompra(MySQLiteHelper.STATUS_PEDIDO + " = 'aprovado'", null);

                setPedidos(aprovados);
            }

        }).run();
    }

    @Override
    protected String _statusPedido() {
        return "aprovado";
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
        unregisterReceiver(receiverNovaAprov);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
}