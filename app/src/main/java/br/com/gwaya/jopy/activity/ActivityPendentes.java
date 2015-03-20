package br.com.gwaya.jopy.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;

import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.enums.StatusPedido;
import br.com.gwaya.jopy.model.PedidoCompra;

public class ActivityPendentes extends AbaPedidoCompra {

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
    public StatusPedido getStatusPedido() {
        return StatusPedido.EMITIDO;
    }

    @Override
    public String getTheTitle() {
        return "Pedidos Pendentes";
    }

    @Override
    public int getIconTabID() {
        return R.drawable.tab_pendentes;
    }

    @Override
    public int getNumeroAba() {
        return 0;
    }
}