package br.com.gwaya.jopy;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by marcelorosa on 10/01/15.
 */
public class HistoricoActivity extends ActionBarActivity {

    private View mProgressView;

    private String codForn;
    private List<PedidoCompra> _pedidos;
    private PopulateTask mTask;
    private PedidoCompraDataSource dataSource;

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK, new Intent());
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.historico);

        mProgressView = findViewById(R.id.historico_progress);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            codForn = extras.getString("codForn");
        }

        dataSource = new PedidoCompraDataSource(this);

        showProgress(true);
        mTask = new PopulateTask(codForn);
        mTask.execute((Void) null);

        //CUSTOM VIEW ACTIONBAR
        ActionBar mActionBar;
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.actionbar_default, null);
        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_main);
        mTitleTextView.setText("Histórico");

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    public class PopulateTask extends AsyncTask<Void, Void, List<PedidoCompra>> {

        private String codForn;

        public PopulateTask(String cod) {
            this.codForn = cod;
        }

        @Override
        protected List<PedidoCompra> doInBackground(Void... params) {
            List<PedidoCompra> pedidos = null;

            try {
                dataSource.open();
                List<PedidoCompra> list = dataSource.getAllPedidoCompra(MySQLiteHelper.COD_FORN + " = '" + codForn
                        + "' AND " + MySQLiteHelper.STATUS_PEDIDO + " IN ('aprovado', 'rejeitado')", " 3 ");
                dataSource.close();
                pedidos = list;
            } catch(Exception e) {
                e.printStackTrace();
            } finally {
                if (pedidos == null || (pedidos != null && pedidos.size() == 0)) {
                    //Toast toast = Toast.makeText(HistoricoActivity.this, "Não foram encontrados pedidos com este fornecedor.", Toast.LENGTH_LONG);
                    //toast.show();
                }
            }

            return pedidos;
        }

        @Override
        protected void onPostExecute(final List<PedidoCompra> pedidos) {
            mTask = null;
            showProgress(false);

            if (pedidos == null) {

            } else if (pedidos != null && pedidos.size() == 0) {
                Toast toast = Toast.makeText(HistoricoActivity.this, "Nenhum histórico deste fornecedor.", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                _pedidos = pedidos;

                ListView pedidoList = (ListView)HistoricoActivity.this.findViewById(R.id.listViewHistorico);

                HistoricoAdapterItem adapter = new HistoricoAdapterItem(HistoricoActivity.this,
                        R.layout.rowitem_historico, _pedidos);

                pedidoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        Intent intent = new Intent(HistoricoActivity.this, DetalheHistoricoActivity.class);
                        intent.putExtra("indice", position);
                        intent.putExtra("pedidos", new Gson().toJson(_pedidos));
                        HistoricoActivity.this.startActivity(intent);
                    }
                });
                pedidoList.setAdapter(adapter);
            }
        }

        protected void onCancelled() {
            mTask = null;
            showProgress(false);
        }
    }
}
