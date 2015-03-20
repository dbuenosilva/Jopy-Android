package br.com.gwaya.jopy.activity.abas;

import android.content.Intent;

import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.activity.abstracoes.AbaPedidoCompra;
import br.com.gwaya.jopy.enums.StatusPedido;

public class ActivityPendentes extends AbaPedidoCompra {

    @Override
    public void dispararIntetClickItem(Intent intent) {
        startActivityForResult(intent, 101);
    }

    @Override
    public int getColorIntBackgroundPedido() {
        return R.color.header;
    }

    @Override
    public int getColorInt() {
        return R.color.emitido;
    }

    @Override
    public StatusPedido getStatusPedido() {
        return StatusPedido.EMITIDO;
    }

    @Override
    public String getNomeAba() {
        return "Pedidos Pendentes";
    }

    @Override
    public int getIconTabID() {
        return R.drawable.tab_pendentes;
    }

}