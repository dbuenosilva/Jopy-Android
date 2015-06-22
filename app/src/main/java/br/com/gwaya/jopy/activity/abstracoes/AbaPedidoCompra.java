package br.com.gwaya.jopy.activity.abstracoes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.util.ArrayList;
import java.util.List;

import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.activity.ActivityDetalhe;
import br.com.gwaya.jopy.adapter.AdapterPedidoCompra;
import br.com.gwaya.jopy.communication.PedidoCompraService;
import br.com.gwaya.jopy.dao.DadosAcessoDAO;
import br.com.gwaya.jopy.enums.StatusPedido;
import br.com.gwaya.jopy.interfaces.ICarregarPedidosDoBancoAsyncTask;
import br.com.gwaya.jopy.interfaces.IDownloadPedidos;
import br.com.gwaya.jopy.model.DadosAcesso;
import br.com.gwaya.jopy.model.PedidoCompra;
import br.com.gwaya.jopy.tasks.CarregarPedidosDoBancoAsyncTask;
import br.com.gwaya.jopy.tasks.DownloadPedidosAsyncTask;
import br.com.gwaya.jopy.utils.Utils;

public abstract class AbaPedidoCompra extends Aba implements ICarregarPedidosDoBancoAsyncTask, IDownloadPedidos, AdapterView.OnItemClickListener {

    private ListView listView;
    private SwipyRefreshLayout mSwipyRefreshLayout;
    private TextView textViewStatusLista;
    private ProgressBar progressBar;
    private LinearLayout linearLayout;
    private DadosAcesso dadosAcesso;
    private List<PedidoCompra> listaPedidosCompra = new ArrayList<>();
    private DownloadPedidosAsyncTask asyncTaskDownloadPedidos;
    private CarregarPedidosDoBancoAsyncTask asyncTaskCarregarPedidosDoBanco;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                if (getStatusPedido() == StatusPedido.getFromInt(bundle.getInt("statusPedido"))) {
                    pullToRefresh();
                }
            }
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configurarActionBar();
        setContentView(R.layout.aba_pedidocompra);

        List<DadosAcesso> lista = new DadosAcessoDAO().getAllDadosAcesso();
        if (lista.size() > 0) {
            dadosAcesso = lista.get(0);
        }

        listView = (ListView) findViewById(R.id.listViewPedidoCompraEmitido);
        mSwipyRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.swipyrefreshlayout);
        textViewStatusLista = (TextView) findViewById(R.id.textViewStatusLista);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        findViewById(R.id.frameLayoutCorPedido).setBackgroundResource(getColorIntBackgroundPedido());

        mSwipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection swipyRefreshLayoutDirection) {
                pullToRefresh();
            }
        });

        listView.setDivider(new ColorDrawable(getResources().getColor(getColorIntBackgroundPedido())));
        listView.setDividerHeight(1);

        listView.setOnItemClickListener(this);

        textViewStatusLista.setText(textViewStatusLista.getText());
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(new Intent(this, PedidoCompraService.class));
        pullToRefresh();
        registerReceiver(receiver, new IntentFilter(PedidoCompraService.NOTIFICATION));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 101 || requestCode == 101) {
            pedidosBaixadosForamSalvosNoBancoComSucesso();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        asyncTaskDownloadPedidos = null;
        mSwipyRefreshLayout.setRefreshing(false);
        unregisterReceiver(receiver);
        stopService(new Intent(this, PedidoCompraService.class));
    }


    private void configurarActionBar() {
        //getSupportActionBar().setDisplayShowHomeEnabled(false);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);

        LayoutInflater mInflater = LayoutInflater.from(this);

        View customView = mInflater.inflate(R.layout.actionbar_main, null);

        ((TextView) customView.findViewById(R.id.title_main)).setText(getTituloTela());

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(customView);
        //getSupportActionBar().setDisplayShowCustomEnabled(true);

    }

    private void pullToRefresh() {
        carregarPedidosDoBanco();
        if (Utils.isConectado()) {
            cancelarTasks();
            asyncTaskDownloadPedidos = new DownloadPedidosAsyncTask(this, dadosAcesso);
            asyncTaskDownloadPedidos.execute();
        } else {
            textViewStatusLista.setText(getString(R.string.sem_conexao_com_a_internet));
        }
    }

    private void setDividerListView() {
        if (listView != null) {
            if ("aprovado".equals(getStatusPedido().getTexto())) {
                listView.setDivider(new ColorDrawable(getResources().getColor(R.color.aprovado_forte)));
            } else if ("rejeitado".equals(getStatusPedido().getTexto())) {
                listView.setDivider(new ColorDrawable(getResources().getColor(R.color.rejeitado_forte)));
            } else {
                listView.setDivider(new ColorDrawable(getResources().getColor(R.color.header)));
            }
        }
    }

    @Override
    public void setListaPedidoCompraDoBanco(List<PedidoCompra> pedidos) {
        if (pedidos != null) {
            if (pedidos.size() > 0) {
                listView.setAdapter(new AdapterPedidoCompra(this, pedidos, getStatusPedido()));
                listaPedidosCompra = pedidos;

                linearLayout.setVisibility(View.GONE);
                textViewStatusLista.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            } else {
                textViewStatusLista.setText(getString(R.string.sem_pedidos_no_banco_de_dados));
                textViewStatusLista.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }
        }

        setDividerListView();

        mSwipyRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PedidoCompra pedidoCompra = listaPedidosCompra.get(position);
        if (pedidoCompra != null) {
            Intent intent = new Intent(this, ActivityDetalhe.class);
            intent.putExtra("pedidocompra", pedidoCompra);
            dispararIntetClickItem(intent);
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
        textViewStatusLista.setText(getString(R.string.carregando_pedidos_do_banco));
        cancelarTasks();
        asyncTaskCarregarPedidosDoBanco = new CarregarPedidosDoBancoAsyncTask(this, getStatusPedido());
        asyncTaskCarregarPedidosDoBanco.execute();
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
        textViewStatusLista.setText(getString(R.string.pedidos_salvos_no_banco_de_dados));
        carregarPedidosDoBanco();
    }

    @Override
    public void logoff(Context context, Integer statusCode) {
        DadosAcesso.logoff(context, statusCode);
    }

    @Override
    public String getTituloTela() {
        return "Pedidos ".concat(getNomeAba());
    }

    public abstract int getColorIntBackgroundPedido();

    public abstract void dispararIntetClickItem(Intent intent);

    public abstract StatusPedido getStatusPedido();
}
