package br.com.gwaya.jopy.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;

import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.activity.abstracoes.ActivityGeneric;
import br.com.gwaya.jopy.adapter.AdapterPedidoCompraItens;
import br.com.gwaya.jopy.model.PedidoCompra;


public class ActivityDetalheHistorico extends ActivityGeneric implements OnClickListener {

    private RelativeLayout relativeLayoutAnterior;
    private RelativeLayout relativeLayoutProximo;
    private TextView txtPedido;
    private ListView pedidoList;
    private TextView txtStatus;
    private TextView mTitleTextView;
    private TextView textViewNomeForn;
    private TextView textViewDtEmi;
    private TextView textViewDtNeces;
    private TextView textViewSolicitante;
    private TextView textViewCentroCusto;
    private TextView textViewTotalPedido;
    private TextView textViewDtMod;
    private TextView textViewMotivo;
    private RelativeLayout relativeLayoutStatus;
    private ScrollView scrollView;

    private int indice;
    private PedidoCompra[] arrayPedidoCompra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_historico);

        initViews();

        String jsonArrayPedidoCompra = "";

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            jsonArrayPedidoCompra = extras.getString("pedidos");
            indice = extras.getInt("indice");
        }

        arrayPedidoCompra = new Gson().fromJson(jsonArrayPedidoCompra, PedidoCompra[].class);

        configActionBar();

        if (arrayPedidoCompra != null) {
            setPedido(arrayPedidoCompra[indice]);
        }

        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_UP);
            }
        });

        relativeLayoutProximo.setOnClickListener(this);
        relativeLayoutAnterior.setOnClickListener(this);
    }

    private void initViews() {
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        relativeLayoutAnterior = (RelativeLayout) findViewById(R.id.relativeLayoutAnterior);
        relativeLayoutProximo = (RelativeLayout) findViewById(R.id.relativeLayoutProximo);
        txtPedido = (TextView) findViewById(R.id.txtPedido);
        pedidoList = (ListView) findViewById(R.id.listViewItens);
        relativeLayoutStatus = (RelativeLayout) findViewById(R.id.relativeLayoutStatus);
        txtStatus = (TextView) findViewById(R.id.txtStatus);
        textViewNomeForn = (TextView) findViewById(R.id.txtForn);
        textViewDtEmi = (TextView) findViewById(R.id.txtDtEmi);
        textViewDtNeces = (TextView) findViewById(R.id.txtNec);
        textViewSolicitante = (TextView) findViewById(R.id.txtSolic);
        textViewCentroCusto = (TextView) findViewById(R.id.txtCentroCusto);
        textViewTotalPedido = (TextView) findViewById(R.id.txtTotal);
        textViewDtMod = (TextView) findViewById(R.id.txtDtMod);
        textViewMotivo = (TextView) findViewById(R.id.txtMotivoPedido);
    }

    private void configActionBar() {
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.actionbar_default, null);
        mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_main);
        mTitleTextView.setText("Histórico " + String.valueOf(indice + 1) + " de " + String.valueOf(arrayPedidoCompra.length));
        mTitleTextView.setGravity(Gravity.CENTER);

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);
    }

    private void setPedido(PedidoCompra pedido) {

        AdapterPedidoCompraItens adapter = new AdapterPedidoCompraItens(this, pedido.getItens());

        pedidoList.setAdapter(adapter);

        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, pedidoList);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = pedidoList.getLayoutParams();

        params.height = totalHeight + (pedidoList.getDividerHeight() * (adapter.getCount() - 1));

        pedidoList.setLayoutParams(params);
        pedidoList.requestLayout();

        String dtEmi = pedido.getDtEmi();
        String dtNeces = pedido.getDtNeces();
        String dtMod = pedido.getDtMod();
        String totalPedido = "";

        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        try {
            dtEmi = dateFormat.format(isoFormat.parse(dtEmi));

            dtNeces = dateFormat.format(isoFormat.parse(dtNeces));

            dtMod = dateFormat.format(isoFormat.parse(dtMod));

            totalPedido = String.format("%.2f", pedido.getTotalPedido());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        txtPedido.setText(pedido.getIdSistema());
        textViewNomeForn.setText(pedido.getNomeForn());
        textViewDtEmi.setText(dtEmi);
        textViewDtNeces.setText(dtNeces);
        textViewSolicitante.setText(pedido.getSolicitante());
        textViewCentroCusto.setText(pedido.getCentroCusto());
        textViewTotalPedido.setText(totalPedido);
        textViewDtMod.setText("Data da última modificação: " + dtMod);
        textViewMotivo.setText(pedido.getMotivo());
        relativeLayoutStatus.setVisibility(RelativeLayout.VISIBLE);


        if (pedido.getStatusPedido().equals("aprovado")) {
            relativeLayoutStatus.setBackgroundResource(R.color.aprovado_forte);
            txtStatus.setText("Pedido Aprovado");
        } else {
            relativeLayoutStatus.setBackgroundResource(R.color.rejeitado_forte);
            txtStatus.setText("Pedido Rejeitado");
        }

        if (indice == 0) {
            relativeLayoutAnterior.setVisibility(Button.INVISIBLE);
        } else {
            relativeLayoutAnterior.setVisibility(Button.VISIBLE);
        }

        if (indice == arrayPedidoCompra.length - 1) {
            relativeLayoutProximo.setVisibility(Button.INVISIBLE);
        } else {
            relativeLayoutProximo.setVisibility(Button.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK, new Intent());
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.relativeLayoutAnterior) {
            if (indice > 0) {
                indice--;
            }
        }

        if (v.getId() == R.id.relativeLayoutProximo) {
            indice++;
        }
        mTitleTextView.setText("Histórico " + String.valueOf(indice + 1) + " de " + String.valueOf(arrayPedidoCompra.length));
        setPedido(arrayPedidoCompra[indice]);
    }
}
