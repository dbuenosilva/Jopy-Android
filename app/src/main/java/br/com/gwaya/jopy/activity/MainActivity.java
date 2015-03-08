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

import java.util.Arrays;
import java.util.List;

import br.com.gwaya.jopy.Acesso;
import br.com.gwaya.jopy.MySQLiteHelper;
import br.com.gwaya.jopy.model.PedidoCompra;
import br.com.gwaya.jopy.PedidoCompraDataSource;
import br.com.gwaya.jopy.PedidoCompraService;
import br.com.gwaya.jopy.R;


public class MainActivity extends TabActivity {

    public Acesso acesso;

    private DownloadTask downloadTask;

    private PedidoCompraDataSource dataSource;
    private Boolean login;

    private void publishResults(PedidoCompra[] pedidos, String tipo) {
        Intent intent = new Intent(PedidoCompraService.NOTIFICATION);
        intent.putExtra(tipo, new Gson().toJson(pedidos));
        MainActivity.this.sendBroadcast(intent);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataSource = new PedidoCompraDataSource(this.getApplicationContext());

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
            dataSource.open();
            dataSource.deleteAll();
            dataSource.close();
        }
        setTabs();

        //Add por Thiago A.Sousa
        //new DownloadTask().execute();
    }

    private void setTabs() {
        addTab("Pendentes", R.drawable.tab_pendentes, EmitidosActivity.class);
        addTab("Aprovados", R.drawable.tab_aprovados, AprovadosActivity.class);
        addTab("Rejeitados", R.drawable.tab_rejeitados, RejeitadosActivity.class);
        addTab("Sobre", R.drawable.tab_opcoes, OpcoesActivity.class);
    }

    private void addTab(String labelId, int drawableId, Class<?> c) {
        final TabHost tabHost = getTabHost();
        Intent intent = new Intent(this, c);
        intent.putExtra("login", login);
        TabHost.TabSpec spec = tabHost.newTabSpec("tab" + labelId);

        View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_indicator, getTabWidget(), false);
        TextView title = (TextView) tabIndicator.findViewById(R.id.title);
        title.setText(labelId);

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
                    ((TextView) tabHost.getTabWidget().getChildAt(i).findViewById(R.id.title))
                            .setTextColor(Color.parseColor("#FFFFFF")); //unselected
                }
                View indicator = tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab());
                TextView title = (TextView) indicator.findViewById(R.id.title);

                if (tabId.equals("tabPendentes")) {
                    title.setTextColor(getResources().getColor(R.color.emitido));
                } else if (tabId.equals("tabAprovados")) {
                    title.setTextColor(getResources().getColor(R.color.aprovado));
                } else if (tabId.equals("tabRejeitados")) {
                    title.setTextColor(getResources().getColor(R.color.rejeitado));
                } else if (tabId.equals("tabOpções")) {
                    title.setTextColor(getResources().getColor(R.color.emitido));
                }
            }
        });

        ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
        icon.setImageResource(drawableId);

        if (labelId.equals("Pendentes")) {
            title.setTextColor(getResources().getColor(R.color.emitido));
        } else {
            title.setTextColor(Color.parseColor("#FFFFFF"));
        }
        spec.setIndicator(tabIndicator);
        spec.setContent(intent);
        tabHost.addTab(spec);
    }

    public class DownloadTask extends AsyncTask<Void, Void, List<PedidoCompra>> {

        @Override
        protected List<PedidoCompra> doInBackground(Void... params) {
            List<PedidoCompra> lst = null;
            try {
                dataSource.open();
                String url = getResources().getString(R.string.protocolo)
                        + getResources().getString(R.string.rest_api_url)
                        + getResources().getString(R.string.pedidocompra_path);

                String responseData = "";

                responseData = PedidoCompraService.loadFromNetwork(url, acesso, MainActivity.this.getApplicationContext());

                GsonBuilder gsonb = new GsonBuilder();
                Gson gson = gsonb.create();
                JSONArray j;
                PedidoCompra[] pedidos = null;

                //j = new JSONArray(responseData);
                pedidos = gson.fromJson(responseData, PedidoCompra[].class);

                dataSource.deleteAll();
                dataSource.createUpdatePedidoCompra(pedidos, false);

                List<PedidoCompra> emitidos = dataSource.getAllPedidoCompra(MySQLiteHelper.STATUS_PEDIDO + " = 'emitido'", null);
                List<PedidoCompra> aprovados = dataSource.getAllPedidoCompra(MySQLiteHelper.STATUS_PEDIDO + " = 'aprovado'", null);
                List<PedidoCompra> rejeitados = dataSource.getAllPedidoCompra(MySQLiteHelper.STATUS_PEDIDO + " = 'rejeitado'", null);

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
            } finally {
                dataSource.close();
            }
            return lst;
        }

        @Override
        protected void onPostExecute(final List<PedidoCompra> pedidos) {
            downloadTask = null;

            try {
                //dataSource.openRead();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //dataSource.close();
            }
        }

        protected void onCancelled() {
            downloadTask = null;
        }
    }
}
