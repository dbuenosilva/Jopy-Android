package br.com.gwaya.jopy;

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


public class RecebeNotificacaoActivity extends TabActivity {

    public Acesso acesso;

    private DownloadTask downloadTask;

    private PedidoCompraDataSource dataSource;

    private void publishResults(PedidoCompra[] pedidos, String tipo) {
        Intent intent = new Intent(PedidoCompraService.NOTIFICATION);
        intent.putExtra(tipo, new Gson().toJson(pedidos));
        RecebeNotificacaoActivity.this.sendBroadcast(intent);
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

                responseData = PedidoCompraService.loadFromNetwork(url, acesso);

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

    private Boolean login;

    public void onCreate(Bundle savedInstanceState) {
        dataSource = new PedidoCompraDataSource(this.getApplicationContext());
        super.onCreate(savedInstanceState);


        //setContentView(R.layout.activity_main);

        //Add por Thiago A.Sousa
        new DownloadTask().execute();
    }

}
