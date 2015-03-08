package br.com.gwaya.jopy.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.util.List;

import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.adapter.AdapterPedidoCompra;
import br.com.gwaya.jopy.dao.MySQLiteHelper;
import br.com.gwaya.jopy.dao.PedidoCompraDAO;
import br.com.gwaya.jopy.model.PedidoCompra;

public class AbaPedidoCompra extends MasterActivity {

    private ListView listView;

    private PedidoCompraDAO pedidoCompraDAO;

    private List<PedidoCompra> pedidoCompraList;

    private CarregaPedidosAsyncTask carregaPedidosAsyncTask;
    private UpdateAsyncTask updateAsyncTask;

    private SwipyRefreshLayout mSwipyRefreshLayout;

    public UpdateAsyncTask getUpdateAsyncTask() {
        return updateAsyncTask;
    }

    public void setUpdateAsyncTask(UpdateAsyncTask updateAsyncTask) {
        this.updateAsyncTask = updateAsyncTask;
    }

    public PedidoCompraDAO getPedidoCompraDAO() {
        return pedidoCompraDAO;
    }

    public List<PedidoCompra> getPedidoCompraList() {
        return pedidoCompraList;
    }

    public SwipyRefreshLayout getSwipyRefreshLayout() {
        return mSwipyRefreshLayout;
    }


    public String getTheTitle() {
        return "";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        pedidoCompraDAO = new PedidoCompraDAO();

        setContentView(R.layout.aba_pedidocompra);

        FrameLayout frameLayoutCorPedido = (FrameLayout) findViewById(R.id.frameLayoutCorPedido);
        listView = (ListView) findViewById(R.id.listViewPedidoCompraEmitido);
        mSwipyRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.swipyrefreshlayout);

        mSwipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection swipyRefreshLayoutDirection) {
                atualizarListView();
            }
        });

        carregaPedidosAsyncTask = new CarregaPedidosAsyncTask();

        setCorBackgroundComBaseNoPedido(frameLayoutCorPedido);

        configurarActionBar();
    }

    private void setCorBackgroundComBaseNoPedido(FrameLayout frameLayoutCorPedido) {
        if (getStatusPedido().equals("emitido")) {
            frameLayoutCorPedido.setBackgroundResource(R.color.header);
        } else if (getStatusPedido().equals("aprovado")) {
            frameLayoutCorPedido.setBackgroundResource(R.color.aprovado);
        } else {
            frameLayoutCorPedido.setBackgroundResource(R.color.rejeitado);
        }
    }

    private void configurarActionBar() {
        ActionBar mActionBar;
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View customView = mInflater.inflate(R.layout.actionbar_main, null);
        TextView mTitleTextView = (TextView) customView.findViewById(R.id.title_main);
        mTitleTextView.setText(getTheTitle());

        ImageButton imageButton = (ImageButton) customView.findViewById(R.id.refreshButton);
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (updateAsyncTask == null) {
                    updateAsyncTask = new UpdateAsyncTask(getStatusPedido());
                    updateAsyncTask.execute((Void) null);
                }

                Toast.makeText(getApplicationContext(), "Pedidos atualizados!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        mActionBar.setCustomView(customView);
        mActionBar.setDisplayShowCustomEnabled(true);
    }

    public void atualizarListView() {
        getSwipyRefreshLayout().setRefreshing(false);
    }

    public ListView setPedidos(List<PedidoCompra> pedidos) {
        pedidoCompraList = pedidos;

        if (pedidos != null) {
            AdapterPedidoCompra adapter = new AdapterPedidoCompra(this, pedidos);
            listView.setAdapter(adapter);
        }

        setDividerListView();

        return listView;
    }

    private void setDividerListView() {
        if (getStatusPedido().equals("aprovado")) {
            listView.setDivider(new ColorDrawable(getResources().getColor(R.color.aprovado)));
        } else if (getStatusPedido().equals("rejeitado")) {
            listView.setDivider(new ColorDrawable(getResources().getColor(R.color.rejeitado)));
        } else {
            listView.setDivider(new ColorDrawable(getResources().getColor(R.color.header)));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 101 || requestCode == 101) {
            try {
                AdapterPedidoCompra adapter = (AdapterPedidoCompra) listView.getAdapter();
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (carregaPedidosAsyncTask != null) {
                if (carregaPedidosAsyncTask.getStatus() == Status.RUNNING) {
                    carregaPedidosAsyncTask.cancel(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getStatusPedido() {
        return "";
    }

    public class CarregaPedidosAsyncTask extends AsyncTask<Void, Void, List<PedidoCompra>> {

        @Override
        public void onPostExecute(List<PedidoCompra> result) {
            super.onPostExecute(result);

            pedidoCompraList = result;

            if (pedidoCompraList != null) {
                setPedidos(pedidoCompraList);
            }
        }

        @Override
        public List<PedidoCompra> doInBackground(Void... params) {
            List<PedidoCompra> pedidos = null;

            try {
                pedidos = pedidoCompraDAO.getAllPedidoCompra(MySQLiteHelper.STATUS_PEDIDO + " = '" + getStatusPedido() + "'", null);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return pedidos;
        }
    }

    public class UpdateAsyncTask extends AsyncTask<Void, Void, List<PedidoCompra>> {
        private final String _status;

        public UpdateAsyncTask(String status) {
            _status = status;
        }

        @Override
        public List<PedidoCompra> doInBackground(Void... params) {
            List<PedidoCompra> pedidos = null;
            try {
                pedidos = pedidoCompraDAO.getAllPedidoCompra(MySQLiteHelper.STATUS_PEDIDO + " = '" + _status + "'", null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return pedidos;
        }

        @Override
        public void onPostExecute(final List<PedidoCompra> pedidos) {
            pedidoCompraList = pedidos;
            setPedidos(pedidos);
            updateAsyncTask = null;
        }

        @Override
        public void onCancelled() {
            updateAsyncTask = null;
        }
    }
}
