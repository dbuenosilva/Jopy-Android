package br.com.gwaya.jopy.activity.abas;

import android.content.Intent;

import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.activity.abstracoes.AbaPedidoCompra;
import br.com.gwaya.jopy.enums.StatusPedido;

public class ActivityPendentes extends AbaPedidoCompra {

    public static final int ID = 0;

    @Override
    public void dispararIntetClickItem(Intent intent) {
        startActivityForResult(intent, 101);
    }

    @Override
    public int getColorIntBackgroundPedido() {
        return R.color.emitido;
    }

    @Override
    public StatusPedido getStatusPedido() {
        return StatusPedido.EMITIDO;
    }

    @Override
    public String getNomeAba() {
        return "Pendentes";
    }

    @Override
    public int getIconTabID() {
        return R.drawable.tab_pendentes;
    }

}