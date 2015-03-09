package br.com.gwaya.jopy.tasks;

import android.os.AsyncTask;

import java.util.List;

import br.com.gwaya.jopy.dao.MySQLiteHelper;
import br.com.gwaya.jopy.dao.PedidoCompraDAO;
import br.com.gwaya.jopy.interfaces.ICarregarPedidosDoBancoAsyncTask;
import br.com.gwaya.jopy.model.PedidoCompra;

/**
 * Created by pedrofsn on 08/03/2015.
 */
public class CarregarPedidosDoBancoAsyncTask extends AsyncTask<Void, Void, List<PedidoCompra>> {

    private final String statusPedido;
    private ICarregarPedidosDoBancoAsyncTask callback;
    private boolean running;

    public CarregarPedidosDoBancoAsyncTask(ICarregarPedidosDoBancoAsyncTask callback, String statusPedido) {
        this.statusPedido = statusPedido;
        this.callback = callback;
    }

    @Override
    public List<PedidoCompra> doInBackground(Void... params) {
        running = true;
        List<PedidoCompra> pedidos = null;
        try {
            pedidos = new PedidoCompraDAO().getAllPedidoCompra(MySQLiteHelper.STATUS_PEDIDO + " = '" + statusPedido + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pedidos;
    }

    @Override
    public void onPostExecute(List<PedidoCompra> pedidos) {
        if (pedidos != null) {
            callback.setListaPedidoCompraDoBanco(pedidos);
        } else {
            callback.showFalhaAoCarregarPedidosDoBanco();
        }
        running = false;
    }

    @Override
    public void onCancelled() {
        callback.showFalhaAoCarregarPedidosDoBanco();
        running = false;
    }

    public boolean isRunning() {
        return running;
    }
}