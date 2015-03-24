package br.com.gwaya.jopy.activity.abas;

import android.content.Intent;

import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.activity.abstracoes.AbaPedidoCompra;
import br.com.gwaya.jopy.enums.StatusPedido;

public class ActivityRejeitados extends AbaPedidoCompra {

    public static final int ID = 2;

    @Override
    public void dispararIntetClickItem(Intent intent) {
        startActivity(intent);
    }

    @Override
    public int getColorIntBackgroundPedido() {
        return R.color.rejeitado;
    }

    @Override
    public int getColorInt() {
        return R.color.rejeitado;
    }

    @Override
    public StatusPedido getStatusPedido() {
        return StatusPedido.REJEITADO;
    }

    @Override
    public String getNomeAba() {
        return "Pedidos Rejeitados";
    }

    @Override
    public int getIconTabID() {
        return R.drawable.tab_rejeitados;
    }

}