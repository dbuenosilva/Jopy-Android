package br.com.gwaya.jopy.activity.abas;

import android.content.Intent;

import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.activity.abstracoes.AbaPedidoCompra;
import br.com.gwaya.jopy.enums.StatusPedido;

public class ActivityAprovados extends AbaPedidoCompra {

    public static final int ID = 1;

    @Override
    public void dispararIntetClickItem(Intent intent) {
        startActivityForResult(intent, 101);
    }

    @Override
    public int getColorIntBackgroundPedido() {
        return R.color.aprovado_forte;
    }

    @Override
    public StatusPedido getStatusPedido() {
        return StatusPedido.APROVADO;
    }

    @Override
    public String getNomeAba() {
        return "Aprovados";
    }

    @Override
    public int getIconTabID() {
        return R.drawable.tab_aprovados;
    }

}