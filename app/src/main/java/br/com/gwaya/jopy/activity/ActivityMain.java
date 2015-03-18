package br.com.gwaya.jopy.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.gwaya.jopy.App;
import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.communication.PedidoCompraService;
import br.com.gwaya.jopy.dao.PedidoCompraDAO;
import br.com.gwaya.jopy.enums.StatusPedido;
import br.com.gwaya.jopy.model.Acesso;
import br.com.gwaya.jopy.model.PedidoCompra;


public class ActivityMain extends TabActivity {

    public Acesso acesso;

    private PedidoCompraDAO dataSource;
    private Boolean login;

    private TabHost tabHost;

    private List<Aba> listaAbas = new ArrayList<>();

    private void publishResults(PedidoCompra[] pedidos, String tipo) {
        Intent intent = new Intent(PedidoCompraService.NOTIFICATION);
        intent.putExtra(tipo, new Gson().toJson(pedidos));
        ActivityMain.this.sendBroadcast(intent);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataSource = new PedidoCompraDAO();

        String jsonMyObject = "";

        Bundle extras = getIntent().getExtras();
        login = false;

        if (extras != null) {
            login = extras.getBoolean("login");
            jsonMyObject = extras.getString("ACESSO");
        }
        if (!jsonMyObject.equals("")) {
            acesso = new Gson().fromJson(jsonMyObject, Acesso.class);
        }
        if (login) {
            dataSource.deleteAll();
        }

        tabHost = getTabHost();

        popularListaDeAbas();

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {

                for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
                    ((TextView) tabHost.getTabWidget().getChildAt(i).findViewById(R.id.title))
                            .setTextColor(Color.parseColor("#FFFFFF")); //unselected
                }

                View indicator = tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab());
                TextView title = (TextView) indicator.findViewById(R.id.title);

                switch (tabId) {
                    case "Pedidos Pendentes":
                        title.setTextColor(getResources().getColor(R.color.emitido));
                        App.ABA_ATUAL = 0;
                        break;
                    case "Pedidos Aprovados":
                        title.setTextColor(getResources().getColor(R.color.aprovado));
                        App.ABA_ATUAL = 1;
                        break;
                    case "Pedidos Rejeitados":
                        title.setTextColor(getResources().getColor(R.color.rejeitado));
                        App.ABA_ATUAL = 2;
                        break;
                    case "Sobre":
                        title.setTextColor(getResources().getColor(R.color.emitido));
                        App.ABA_ATUAL = 3;
                        break;
                }
            }
        });

        tabHost.setCurrentTab(App.ABA_ATUAL);

        //Add por Thiago A.Sousa
        //new DownloadTask().execute();
    }

    private void popularListaDeAbas() {

        if (listaAbas != null && listaAbas.size() == 0) {
            listaAbas.add(new ActivityPendentes());
            listaAbas.add(new ActivityAprovados());
            listaAbas.add(new ActivityRejeitados());
            listaAbas.add(new ActivityOpcoes());

            for (Aba aba : listaAbas) {

                Intent intent = new Intent(this, aba.getClass());
                intent.putExtra("login", login);

                View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_indicator, getTabWidget(), false);

                TextView title = (TextView) tabIndicator.findViewById(R.id.title);
                ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);

                title.setText(aba.getTheTitle());
                icon.setImageResource(aba.getIconTabID());

                if ("Pedidos Pendentes".equals(aba.getTheTitle())) {
                    title.setTextColor(getResources().getColor(R.color.emitido));
                } else {
                    title.setTextColor(Color.parseColor("#FFFFFF"));
                }

                TabHost.TabSpec spec = tabHost.newTabSpec(aba.getTheTitle());
                spec.setIndicator(tabIndicator);
                spec.setContent(intent);

                tabHost.addTab(spec);
            }
        }
    }

    public class DownloadTask extends AsyncTask<Void, Void, List<PedidoCompra>> {

        @Override
        protected List<PedidoCompra> doInBackground(Void... params) {
            List<PedidoCompra> lst = null;
            try {
                String url = getResources().getString(R.string.protocolo)
                        + App.API_REST
                        + getResources().getString(R.string.pedidocompra_path);

                String responseData = "";

                responseData = PedidoCompraService.loadFromNetwork(url, acesso, ActivityMain.this.getApplicationContext());

                GsonBuilder gsonb = new GsonBuilder();
                Gson gson = gsonb.create();
                JSONArray j;
                PedidoCompra[] pedidos = gson.fromJson(responseData, PedidoCompra[].class);

                dataSource.deleteAll();
                dataSource.createUpdatePedidoCompra(pedidos);

                List<PedidoCompra> emitidos = dataSource.getAllPedidoCompra(StatusPedido.EMITIDO);
                List<PedidoCompra> aprovados = dataSource.getAllPedidoCompra(StatusPedido.APROVADO);
                List<PedidoCompra> rejeitados = dataSource.getAllPedidoCompra(StatusPedido.REJEITADO);

                if (emitidos.size() > 0) {
                    publishResults(emitidos.toArray(new PedidoCompra[emitidos.size()]), PedidoCompraService.PEDIDOS_EMITIDOS);
                }
                if (aprovados.size() > 0) {
                    publishResults(aprovados.toArray(new PedidoCompra[aprovados.size()]), PedidoCompraService.PEDIDOS_APROVADOS);
                }
                if (rejeitados.size() > 0) {
                    publishResults(rejeitados.toArray(new PedidoCompra[rejeitados.size()]), PedidoCompraService.PEDIDOS_REJEITADOS);
                }

                lst = Arrays.asList(pedidos);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return lst;
        }
    }
}
