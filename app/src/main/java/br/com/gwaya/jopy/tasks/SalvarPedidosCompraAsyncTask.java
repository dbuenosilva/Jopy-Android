package br.com.gwaya.jopy.tasks;

import android.os.AsyncTask;

import java.util.List;

import br.com.gwaya.jopy.dao.MySQLiteHelper;
import br.com.gwaya.jopy.dao.PedidoCompraDAO;
import br.com.gwaya.jopy.interfaces.ISalvarPedidosCompraAsyncTask;
import br.com.gwaya.jopy.model.PedidoCompra;

/**
 * Created by pedrofsn on 08/03/2015.
 */
public class SalvarPedidosCompraAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private final List<PedidoCompra> mPedidos;
    private ISalvarPedidosCompraAsyncTask callback;
    private PedidoCompraDAO dao;
    private boolean running;

    public SalvarPedidosCompraAsyncTask(ISalvarPedidosCompraAsyncTask callback, List<PedidoCompra> pedidos) {
        this.dao = new PedidoCompraDAO();
        this.callback = callback;
        this.mPedidos = pedidos;
    }

    @Override
    public Boolean doInBackground(Void... params) {
        running = true;
        boolean retorno = true;
        try {
            dao.createUpdatePedidoCompra(mPedidos.toArray(new PedidoCompra[mPedidos.size()]));
        } catch (Exception e) {
            retorno = false;
            e.printStackTrace();
        }
        return retorno;
    }

    @Override
    public void onPostExecute(final Boolean success) {
        if (success) {
            callback.setListaPedidoCompraDoBanco(dao.getAllPedidoCompra(MySQLiteHelper.STATUS_PEDIDO + " = 'emitido'", null));
        } else {
            callback.showFalhaAoSetarPedidosRecemBaixados();
        }
        running = false;
    }

    @Override
    public void onCancelled() {
        callback.showFalhaAoSetarPedidosRecemBaixados();
        running = false;
    }

    public boolean isRunning() {
        return running;
    }
}
