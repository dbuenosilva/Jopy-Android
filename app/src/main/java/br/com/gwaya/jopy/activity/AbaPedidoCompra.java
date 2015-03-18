package br.com.gwaya.jopy.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.util.ArrayList;
import java.util.List;

import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.adapter.AdapterPedidoCompra;
import br.com.gwaya.jopy.dao.AcessoDAO;
import br.com.gwaya.jopy.enums.StatusPedido;
import br.com.gwaya.jopy.interfaces.ICarregarPedidosDoBancoAsyncTask;
import br.com.gwaya.jopy.interfaces.IDownloadPedidos;
import br.com.gwaya.jopy.model.Acesso;
import br.com.gwaya.jopy.model.PedidoCompra;
import br.com.gwaya.jopy.tasks.CarregarPedidosDoBancoAsyncTask;
import br.com.gwaya.jopy.tasks.DownloadPedidosAsyncTask;

public abstract class AbaPedidoCompra extends Aba implements ICarregarPedidosDoBancoAsyncTask, IDownloadPedidos, AdapterView.OnItemClickListener {

    private ListView listView;
    private SwipyRefreshLayout mSwipyRefreshLayout;
    private TextView textViewStatusLista;
    private Acesso acesso;

    private List<PedidoCompra> listaPedidosCompra = new ArrayList<>();

    private DownloadPedidosAsyncTask asyncTaskDownloadPedidos;
    private CarregarPedidosDoBancoAsyncTask asyncTaskCarregarPedidosDoBanco;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configurarActionBar();
        setContentView(R.layout.aba_pedidocompra);

        AcessoDAO AcessoDAO = new AcessoDAO();
        List<Acesso> lst = AcessoDAO.getAllAcesso();
        if (lst.size() > 0) {
            acesso = lst.get(0);
        }

        FrameLayout frameLayoutCorPedido = (FrameLayout) findViewById(R.id.frameLayoutCorPedido);
        listView = (ListView) findViewById(R.id.listViewPedidoCompraEmitido);
        mSwipyRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.swipyrefreshlayout);
        textViewStatusLista = (TextView) findViewById(R.id.textViewStatusLista);

        mSwipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection swipyRefreshLayoutDirection) {
                pullToRefresh();
            }
        });

        setCorBackgroundComBaseNoPedido(frameLayoutCorPedido);

        listView.setOnItemClickListener(this);
        configureListViewDivider(listView);

        textViewStatusLista.setText(textViewStatusLista.getText() + " " + getStatusPedido().toString().toLowerCase() + "s");
    }

    @Override
    protected void onResume() {
        super.onResume();
        pullToRefresh();
    }

    private void setCorBackgroundComBaseNoPedido(FrameLayout frameLayoutCorPedido) {
        if (StatusPedido.EMITIDO == getStatusPedido()) {
            frameLayoutCorPedido.setBackgroundResource(R.color.header);
        } else if (StatusPedido.APROVADO == getStatusPedido()) {
            frameLayoutCorPedido.setBackgroundResource(R.color.aprovado);
        } else {
            frameLayoutCorPedido.setBackgroundResource(R.color.rejeitado);
        }
    }

    private void configurarActionBar() {
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        LayoutInflater mInflater = LayoutInflater.from(this);

        View customView = mInflater.inflate(R.layout.actionbar_main, null);

        ((TextView) customView.findViewById(R.id.title_main)).setText(getTheTitle());

        getSupportActionBar().setCustomView(customView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
    }

    private void pullToRefresh() {
        cancelarTasks();
        asyncTaskDownloadPedidos = new DownloadPedidosAsyncTask(this, acesso);
        asyncTaskDownloadPedidos.execute();
    }

    private void setDividerListView() {
        if (listView != null) {
            if ("aprovado".equals(getStatusPedido().getTexto())) {
                listView.setDivider(new ColorDrawable(getResources().getColor(R.color.aprovado)));
            } else if ("rejeitado".equals(getStatusPedido().getTexto())) {
                listView.setDivider(new ColorDrawable(getResources().getColor(R.color.rejeitado)));
            } else {
                listView.setDivider(new ColorDrawable(getResources().getColor(R.color.header)));
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 101 || requestCode == 101) {
            try {
                ((AdapterPedidoCompra) listView.getAdapter()).notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        asyncTaskDownloadPedidos = null;
        mSwipyRefreshLayout.setRefreshing(false);
    }

    @Override
    public void setListaPedidoCompraDoBanco(List<PedidoCompra> pedidos) {
        if (pedidos != null) {
            if (pedidos.size() > 0) {
                listView.setAdapter(new AdapterPedidoCompra(this, pedidos));
                listaPedidosCompra = pedidos;

                textViewStatusLista.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            } else {
                textViewStatusLista.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            }
        }

        setDividerListView();

        mSwipyRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PedidoCompra pedidoCompra = listaPedidosCompra.get(position);
        if (pedidoCompra != null) {
            clickOnItemListView(parent, view, position, id, pedidoCompra);
        }
    }

    @Override
    public void showFalhaAoCarregarPedidosDoBanco() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textViewStatusLista.setText(getString(R.string.sem_pedidos_no_banco_de_dados));
                mSwipyRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void showFalhaAoBaixar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textViewStatusLista.setText(getString(R.string.falha_ao_baixar_pedidos_de_compra));
                mSwipyRefreshLayout.setRefreshing(false);

                carregarPedidosDoBanco();
            }
        });
    }

    private void carregarPedidosDoBanco() {
        if (listaPedidosCompra.size() == 0) {
            textViewStatusLista.setText(getString(R.string.carregando_pedidos_do_banco));
            asyncTaskCarregarPedidosDoBanco = new CarregarPedidosDoBancoAsyncTask(AbaPedidoCompra.this, getStatusPedido());
            asyncTaskCarregarPedidosDoBanco.execute();
        }
    }

    public void cancelarTasks() {
        if (asyncTaskCarregarPedidosDoBanco != null && asyncTaskCarregarPedidosDoBanco.isRunning()) {
            asyncTaskCarregarPedidosDoBanco.setRunning(false);
            asyncTaskCarregarPedidosDoBanco.cancel(false);
        }
        if (asyncTaskDownloadPedidos != null && asyncTaskDownloadPedidos.isRunning()) {
            asyncTaskDownloadPedidos.setRunning(false);
            asyncTaskDownloadPedidos.cancel(false);
        }

        asyncTaskCarregarPedidosDoBanco = null;
        asyncTaskDownloadPedidos = null;

        if (mSwipyRefreshLayout != null) {
            mSwipyRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void pedidosBaixadosForamSalvosNoBancoComSucesso() {
        cancelarTasks();
        carregarPedidosDoBanco();
    }

    @Override
    public void logoff(Context context, Integer statusCode) {
        Acesso.logoff(context, statusCode);
    }

    public abstract void clickOnItemListView(AdapterView<?> parent, View view, int position, long id, PedidoCompra pedidoCompra);

    public abstract void configureListViewDivider(ListView listView);

    public abstract StatusPedido getStatusPedido();
}
