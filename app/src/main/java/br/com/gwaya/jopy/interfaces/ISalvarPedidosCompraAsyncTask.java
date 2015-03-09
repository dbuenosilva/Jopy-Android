package br.com.gwaya.jopy.interfaces;

import java.util.List;

import br.com.gwaya.jopy.model.PedidoCompra;

/**
 * Created by pedrofsn on 08/03/2015.
 */
public interface ISalvarPedidosCompraAsyncTask {

    public void setListaPedidoCompraDoBanco(List<PedidoCompra> pedidos);

    public void showFalhaAoSetarPedidosRecemBaixados();

}
