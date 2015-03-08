package br.com.gwaya.jopy.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;

import java.util.Arrays;
import java.util.List;

import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.communication.PedidoCompraService;
import br.com.gwaya.jopy.dao.AcessoDAO;
import br.com.gwaya.jopy.dao.MySQLiteHelper;
import br.com.gwaya.jopy.model.Acesso;
import br.com.gwaya.jopy.model.PedidoCompra;

public class ActivityPendentes extends ActivityAba {

    private Acesso acesso;

    private DownloadTask downloadTask;

    private SaveAllTask saveAllTask;

    private Boolean login;

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String strPedidos = bundle.getString(PedidoCompraService.PEDIDOS_EMITIDOS);
                if (strPedidos != null && !strPedidos.equals("")) {
                    GsonBuilder gsonb = new GsonBuilder();
                    Gson gson = gsonb.create();
                    JSONArray j;
                    List<PedidoCompra> pedidos = null;
                    try {
                        j = new JSONArray(strPedidos);
                        pedidos = Arrays.asList(gson.fromJson(strPedidos, PedidoCompra[].class));
                        setPedidos(pedidos);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    };

    @Override
    public void atualizarListView() {
        super.atualizarListView();
        new DownloadTask().execute();
    }

    @Override
    public String getTheTitle() {
        return "Pedidos Pendentes";
    }

    @Override
    public String _statusPedido() {
        return "emitido";
    }

    @Override
    public ListView setPedidos(List<PedidoCompra> pedidos) {

        ListView pedidoList = super.setPedidos(pedidos);

        pedidoList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Activity tab = ActivityPendentes.this.getParent();

                PedidoCompra pedido = getPedidoCompraList().get(position);
                Intent intent = new Intent(tab, ActivityDetalhe.class);
                intent.putExtra("pedidocompra", new Gson().toJson(pedido));

                ActivityPendentes.this.startActivityForResult(intent, 101);
            }
        });

        pedidoList.setDivider(new ColorDrawable(this.getResources().getColor(R.color.emitido)));
        pedidoList.setDividerHeight(1);

        return pedidoList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();

        login = extras.getBoolean("login");

        AcessoDAO AcessoDAO = new AcessoDAO();
        List<Acesso> lst = AcessoDAO.getAllAcesso();
        if (lst.size() > 0) {
            acesso = lst.get(0);
        }

        if (login) {
            if (downloadTask == null) {
                downloadTask = new DownloadTask();
                downloadTask.execute((Void) null);
            }
        } else if (getUpdateAsyncTask() == null) {
            setUpdateAsyncTask(new UpdateAsyncTask(_statusPedido()));
            getUpdateAsyncTask().execute((Void) null);
        }

        registerReceiver(receiver, new IntentFilter(PedidoCompraService.NOTIFICATION));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (receiver != null) {
                unregisterReceiver(receiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class SaveAllTask extends AsyncTask<Void, Void, Boolean> {

        private final List<PedidoCompra> mPedidos;

        public SaveAllTask(List<PedidoCompra> pedidos) {
            mPedidos = pedidos;
        }

        @Override
        public Boolean doInBackground(Void... params) {
            boolean retorno = true;
            try {
                getPedidoCompraDAO().createUpdatePedidoCompra(mPedidos.toArray(new PedidoCompra[mPedidos.size()]));
            } catch (Exception e) {
                retorno = false;
                e.printStackTrace();
            }
            return retorno;
        }

        @Override
        public void onPostExecute(final Boolean success) {
            saveAllTask = null;
            if (success) {
                setPedidos(getPedidoCompraDAO().getAllPedidoCompra(MySQLiteHelper.STATUS_PEDIDO + " = 'emitido'", null));
                Toast.makeText(ActivityPendentes.this, getString(R.string.a_lista_de_pedidos_pendentes_foi_atualizada), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onCancelled() {
            saveAllTask = null;
        }
    }

    public class DownloadTask extends AsyncTask<Void, Void, List<PedidoCompra>> {

        @Override
        public List<PedidoCompra> doInBackground(Void... params) {

            List<PedidoCompra> pedidos = null;

            HttpClient httpclient = new DefaultHttpClient();

            String url = getResources().getString(R.string.protocolo)
                    + getResources().getString(R.string.rest_api_url)
                    + getResources().getString(R.string.pedidocompra_path),
                    dtMod = getPedidoCompraDAO().ultimoSync();

            if (dtMod != null) {
                url += "?gte=" + dtMod;
            }

            HttpGet httpGet = new HttpGet(url);

            httpGet.setHeader("Authorization", acesso.getToken_Type() + " " + acesso.getAccess_Token());

//            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            try {
//                String str = httpclient.execute(httpGet, responseHandler);
                HttpResponse response = httpclient.execute(httpGet);

                // Obtem codigo de retorno HTTP
                int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode >= 200 && statusCode <= 202) {
                    // Obtem string do Body retorno HTTP
                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
                    String responseBody = responseHandler.handleResponse(response);

                    GsonBuilder gsonb = new GsonBuilder();
                    Gson gson = gsonb.create();
                    PedidoCompra[] array = null;

                    array = gson.fromJson(responseBody, PedidoCompra[].class);

                    pedidos = Arrays.asList(array);
                } else {
                    // mensagem
                    // logout
                    acesso.logoff(ActivityPendentes.this);
                }


            } catch (Exception e) {
                getSwipyRefreshLayout().setRefreshing(false);
                e.printStackTrace();
            }

            return pedidos;
        }

        @Override
        public void onPostExecute(final List<PedidoCompra> pedidos) {
            if (pedidos != null && pedidos.size() > 0) {
                setPedidos(pedidos);

                downloadTask = null;
                if (saveAllTask == null) {
                    saveAllTask = new SaveAllTask(pedidos);
                    saveAllTask.execute();
                }
            } else {
                Toast.makeText(ActivityPendentes.this, getString(R.string.nao_existe_novos_pedidos), Toast.LENGTH_SHORT).show();
            }

            getSwipyRefreshLayout().setRefreshing(false);
        }

        @Override
        public void onCancelled() {
            downloadTask = null;

            getSwipyRefreshLayout().setRefreshing(false);
        }
    }
}