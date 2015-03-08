package br.com.gwaya.jopy.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.com.gwaya.jopy.MySQLiteHelper;
import br.com.gwaya.jopy.model.PedidoCompra;
import br.com.gwaya.jopy.adapter.PedidoCompraAdapterItem;
import br.com.gwaya.jopy.PedidoCompraDataSource;
import br.com.gwaya.jopy.R;

public class MyBaseActivity extends ActionBarActivity {

    protected PedidoCompraDataSource dataSource;

    protected UpdateTask updateTask;
    protected List<PedidoCompra> _pedidos;
    protected FrameLayout frmTipo;
    protected ListView listView;
    protected int currentPosition;
    PedidoCompraDataSource pedidoDataSource;
    CarregaPedidos carregaPedidos;
    private View mProgressView;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
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
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    protected String _statusPedido() {
        return "";
    }

    protected String getTheTitle() {
        return "";
    }

    protected int getResourceLayout() {
        return R.layout.list_view_pedido_compra_emitido;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        dataSource = new PedidoCompraDataSource(this);

        setContentView(getResourceLayout());

        mProgressView = findViewById(R.id.baseProgress);

        if (pedidoDataSource == null) {
            pedidoDataSource = new PedidoCompraDataSource(this);
        }
        if (carregaPedidos == null) {
            carregaPedidos = new CarregaPedidos();
        }

        frmTipo = (FrameLayout) findViewById(R.id.frmTipo);

        listView = (ListView) findViewById(R.id.listViewPedidoCompraEmitido);

        if (_statusPedido().equals("emitido")) {
            frmTipo.setBackgroundResource(R.color.header);
        } else if (_statusPedido().equals("aprovado")) {
            frmTipo.setBackgroundResource(R.color.aprovado);
        } else {
            frmTipo.setBackgroundResource(R.color.rejeitado);
        }

        //CUSTOM VIEW ACTIONBAR
        ActionBar mActionBar;
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.actionbar_main, null);
        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_main);
        mTitleTextView.setText(getTheTitle());

        ImageButton imageButton = (ImageButton) mCustomView
                .findViewById(R.id.refreshButton);
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (updateTask == null) {
                    showProgress(true);
                    updateTask = new UpdateTask(_statusPedido());
                    updateTask.execute((Void) null);
                }

                Toast.makeText(getApplicationContext(), "Pedidos atualizados!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);
        //CUSTOM VIEW ACTIONBAR

        //Intent service = new Intent(this, PedidoCompraService.class);
        //this.startService(service);
    }

    protected ListView setPedidos(List<PedidoCompra> pedidos) {

        _pedidos = pedidos;

        if (pedidos != null) {

            PedidoCompraAdapterItem adapter = new PedidoCompraAdapterItem(this,
                    R.layout.list_view_row_item, pedidos);

            listView.setAdapter(adapter);
        }

        if (_statusPedido().equals("aprovado")) {
            listView.setDivider(new ColorDrawable(getResources().getColor(R.color.aprovado)));
        } else if (_statusPedido().equals("rejeitado")) {
            listView.setDivider(new ColorDrawable(getResources().getColor(R.color.rejeitado)));
        } else {
            listView.setDivider(new ColorDrawable(getResources().getColor(R.color.header)));
        }

        return listView;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 101 || requestCode == 101) {
            try {
                PedidoCompraAdapterItem adapter = (PedidoCompraAdapterItem) listView.getAdapter();
                adapter.remove(_pedidos.get(currentPosition));
                adapter.notifyDataSetChanged();
                currentPosition = -1;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*
        try {
	    	if (carregaPedidos != null) {
	    		if (carregaPedidos.getStatus() == Status.RUNNING) {
	    			carregaPedidos.cancel(true);
	    		}
		    	carregaPedidos.execute();
		    }
	    } catch(Exception e) {
	    	String str = e.getMessage();
	    }*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (carregaPedidos != null) {
                if (carregaPedidos.getStatus() == Status.RUNNING) {
                    carregaPedidos.cancel(true);
                }
            }
        } catch (Exception e) {
            String str = e.getMessage();
        }
    }

    public class CarregaPedidos extends AsyncTask<Void, Void, List<PedidoCompra>> {

        @Override
        protected void onPostExecute(List<PedidoCompra> result) {
            super.onPostExecute(result);

            _pedidos = result;

            if (_pedidos != null) {
                setPedidos(_pedidos);
            }
        }

        @Override
        protected List<PedidoCompra> doInBackground(Void... params) {
            List<PedidoCompra> pedidos = null;

            try {
                pedidoDataSource.open();

                List<PedidoCompra> lst = pedidoDataSource.getAllPedidoCompra(MySQLiteHelper.STATUS_PEDIDO + " = '" + _statusPedido() + "'", null);

                pedidos = lst;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                pedidoDataSource.close();
            }

            return pedidos;
        }
    }

    public class UpdateTask extends AsyncTask<Void, Void, List<PedidoCompra>> {
        private final String _status;

        UpdateTask(String status) {
            _status = status;
        }

        @Override
        protected List<PedidoCompra> doInBackground(Void... params) {
            List<PedidoCompra> pedidos = null;
            try {
                dataSource.open();
                pedidos = dataSource.getAllPedidoCompra(MySQLiteHelper.STATUS_PEDIDO + " = '" + _status + "'", null);
                dataSource.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                dataSource.close();
            }

            return pedidos;
        }

        @Override
        protected void onPostExecute(final List<PedidoCompra> pedidos) {
            _pedidos = pedidos;
            setPedidos(pedidos);
            updateTask = null;
            showProgress(false);
        }

        @Override
        protected void onCancelled() {
            updateTask = null;
            showProgress(false);
        }
    }
}
