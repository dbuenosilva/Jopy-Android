package br.com.gwaya.jopy.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.util.ArrayList;
import java.util.List;

import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.StatusPedido;
import br.com.gwaya.jopy.adapter.AdapterPedidoCompra;
import br.com.gwaya.jopy.dao.AcessoDAO;
import br.com.gwaya.jopy.interfaces.ICarregarPedidosDoBancoAsyncTask;
import br.com.gwaya.jopy.interfaces.IDownloadPedidos;
import br.com.gwaya.jopy.interfaces.ISalvarPedidosCompraAsyncTask;
import br.com.gwaya.jopy.model.Acesso;
import br.com.gwaya.jopy.model.PedidoCompra;
import br.com.gwaya.jopy.tasks.CarregarPedidosDoBancoAsyncTask;
import br.com.gwaya.jopy.tasks.DownloadPedidos;

public abstract class AbaPedidoCompra extends ActionBarActivity implements ICarregarPedidosDoBancoAsyncTask, ISalvarPedidosCompraAsyncTask, IDownloadPedidos, AdapterView.OnItemClickListener {

    private ListView listView;
    private SwipyRefreshLayout mSwipyRefreshLayout;
    private TextView textViewSemPedidos;

    private AdapterPedidoCompra adapter;

    private CarregarPedidosDoBancoAsyncTask carregarPedidosDoBancoAsyncTask;
    private Acesso acesso;

    private List<PedidoCompra> listaPedidosCompra = new ArrayList<>();

    private DownloadPedidos asyncTask;
    private boolean alertarUsuario = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aba_pedidocompra);

        Bundle extras = getIntent().getExtras();

        //boolean login = extras.getBoolean("login");

        AcessoDAO AcessoDAO = new AcessoDAO();
        List<Acesso> lst = AcessoDAO.getAllAcesso();
        if (lst.size() > 0) {
            acesso = lst.get(0);
        }

        FrameLayout frameLayoutCorPedido = (FrameLayout) findViewById(R.id.frameLayoutCorPedido);
        listView = (ListView) findViewById(R.id.listViewPedidoCompraEmitido);
        mSwipyRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.swipyrefreshlayout);
        textViewSemPedidos = (TextView) findViewById(R.id.textViewSemPedidos);

        mSwipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection swipyRefreshLayoutDirection) {
                pullToRefresh(true);
            }
        });

        setCorBackgroundComBaseNoPedido(frameLayoutCorPedido);

        configurarActionBar();

        if (carregarPedidosDoBancoAsyncTask == null) {
            carregarPedidosDoBancoAsyncTask = new CarregarPedidosDoBancoAsyncTask(AbaPedidoCompra.this, getStatusPedido());
            carregarPedidosDoBancoAsyncTask.execute();
        } else {
            if (!carregarPedidosDoBancoAsyncTask.isRunning()) {
                carregarPedidosDoBancoAsyncTask.execute();
            }
        }

        listView.setOnItemClickListener(this);
        configureListViewDivider(listView);

        textViewSemPedidos.setText(textViewSemPedidos.getText() + " " + getStatusPedido().toString().toLowerCase() + "s");
    }

    @Override
    protected void onResume() {
        super.onResume();
        pullToRefresh(false);
    }

    public List<PedidoCompra> getListaPedidosCompra() {
        return listaPedidosCompra;
    }

    public void setListaPedidosCompra(List<PedidoCompra> listaPedidosCompra) {
        this.listaPedidosCompra = listaPedidosCompra;
    }

    private void setCorBackgroundComBaseNoPedido(FrameLayout frameLayoutCorPedido) {
        if (StatusPedido.EMITIDO.getTexto().equals(getStatusPedido())) {
            frameLayoutCorPedido.setBackgroundResource(R.color.header);
        } else if (StatusPedido.APROVADO.getTexto().equals(getStatusPedido())) {
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

        mActionBar.setCustomView(customView);
        mActionBar.setDisplayShowCustomEnabled(true);
    }

    private void pullToRefresh(boolean alertarUsuario) {
        if (asyncTask != null) {
            asyncTask = null;
        }
        asyncTask = new DownloadPedidos(this, acesso);
        asyncTask.execute();
        this.alertarUsuario = alertarUsuario;
    }

    public ListView setPedidos(List<PedidoCompra> pedidos) {
        if (pedidos != null) {
            if (pedidos.size() > 0) {
                setListaPedidosCompra(pedidos);
                adapter = new AdapterPedidoCompra(this, pedidos);
                listView.setAdapter(adapter);

                textViewSemPedidos.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            } else {
                textViewSemPedidos.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            }
        }

        setDividerListView();

        mSwipyRefreshLayout.setRefreshing(false);
        return listView;
    }

    private void setDividerListView() {
        if (listView != null) {
            if ("aprovado".equals(getStatusPedido())) {
                listView.setDivider(new ColorDrawable(getResources().getColor(R.color.aprovado)));
            } else if ("rejeitado".equals(getStatusPedido())) {
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
                AdapterPedidoCompra adapter = (AdapterPedidoCompra) listView.getAdapter();
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        asyncTask = null;
        mSwipyRefreshLayout.setRefreshing(false);
    }

    @Override
    public void setListaPedidoCompraDoBanco(List<PedidoCompra> pedidos) {
        if (pedidos != null) {
            if (pedidos.size() > 0) {
                listView.setAdapter(adapter = new AdapterPedidoCompra(this, pedidos));
                setListaPedidosCompra(pedidos);

                textViewSemPedidos.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            } else {
                textViewSemPedidos.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PedidoCompra pedidoCompra = getListaPedidosCompra().get(position);
        if (pedidoCompra != null) {
            clickOnItemListView(parent, view, position, id, pedidoCompra);
        }
    }

    @Override
    public void showFalhaAoCarregarPedidosDoBanco() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSwipyRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void showFalhaAoBaixar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(AbaPedidoCompra.this, getString(R.string.falha_ao_baixar_pedidos_de_compra), Toast.LENGTH_SHORT).show();
                mSwipyRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void showFalhaAoSetarPedidosRecemBaixados() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(AbaPedidoCompra.this, getString(R.string.falha_ao_exibir_pedidos_de_compra_recem_baixados), Toast.LENGTH_SHORT).show();
                mSwipyRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void showSemNovosProdutos() {
        if (alertarUsuario) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSwipyRefreshLayout.setRefreshing(false);
                }
            });
        }
    }

    @Override
    public void logoff(Context context, Integer statusCode) {
        Acesso.logoff(context, statusCode);
    }

    public abstract void clickOnItemListView(AdapterView<?> parent, View view, int position, long id, PedidoCompra pedidoCompra);

    public abstract void configureListViewDivider(ListView listView);

    public abstract StatusPedido getStatusPedido();

    public abstract String getTheTitle();
}
