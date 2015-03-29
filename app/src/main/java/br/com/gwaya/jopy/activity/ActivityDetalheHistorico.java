package br.com.gwaya.jopy.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;

import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.adapter.AdapterPedidoCompraItens;
import br.com.gwaya.jopy.model.PedidoCompra;
import br.com.gwaya.jopy.model.PedidoCompraItem;


public class ActivityDetalheHistorico extends ActionBarActivity implements OnClickListener {

    private Button buttonPrev;
    private Button buttonNext;
    private ImageView imgPrev;
    private ImageView imgNext;
    private TextView txtPedido;
    private ListView pedidoList;
    private TextView txtStatus;
    private TextView textViewNomeForn;
    private TextView textViewDtEmi;
    private TextView textViewDtNeces;
    private TextView textViewSolicitante;
    private TextView textViewCentroCusto;
    private TextView textViewTotalPedido;
    private TextView textViewDtMod;
    private TextView textViewMotivo;
    private RelativeLayout relativeLayout;
    private ScrollView scrollView;

    private int indice;
    private PedidoCompra[] arrayPedidoCompra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalhe_historico);

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

        buttonNext.setOnClickListener(this);
        buttonPrev.setOnClickListener(this);
        imgPrev.setOnClickListener(this);
        imgNext.setOnClickListener(this);
    }

    private void initViews() {
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        buttonPrev = (Button) findViewById(R.id.buttonPrev);
        buttonNext = (Button) findViewById(R.id.buttonNext);
        imgPrev = (ImageView) findViewById(R.id.imgPrev);
        imgNext = (ImageView) findViewById(R.id.imgNext);
        txtPedido = (TextView) findViewById(R.id.txtPedido);
        pedidoList = (ListView) findViewById(R.id.listViewItens);
        relativeLayout = (RelativeLayout) findViewById(R.id.layoutStatus);
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
        ActionBar mActionBar;
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.actionbar_default, null);
        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_main);
        mTitleTextView.setText("Histórico " + String.valueOf(indice + 1) + " de " + String.valueOf(arrayPedidoCompra.length));
        mTitleTextView.setGravity(Gravity.CENTER);

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);
    }

    private void setPedido(PedidoCompra pedido) {

        AdapterPedidoCompraItens adapter = new AdapterPedidoCompraItens(this, pedido.getItens().toArray(new PedidoCompraItem[pedido.getItens().size()]));

        pedidoList.setAdapter(adapter);

        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, pedidoList);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams _params = pedidoList.getLayoutParams();

        _params.height = totalHeight + (pedidoList.getDividerHeight() * (adapter.getCount() - 1));

        pedidoList.setLayoutParams(_params);
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
        relativeLayout.setVisibility(RelativeLayout.VISIBLE);

        String strStatus = pedido.getStatusPedido().equals("aprovado") ? "Aprovado" : "Motivo da Rejeição: " + pedido.getMotivoRejeicao();

        if (pedido.getStatusPedido().equals("aprovado")) {
            relativeLayout.setBackgroundResource(R.color.aprovado_forte);
        } else {
            relativeLayout.setBackgroundResource(R.color.rejeitado_forte);
        }

        txtStatus.setText(strStatus);
        txtStatus.setTag(pedido.get_id());

        if (indice == 0) {
            buttonPrev.setVisibility(Button.INVISIBLE);
            imgPrev.setVisibility(Button.INVISIBLE);
        } else {
            buttonPrev.setVisibility(Button.VISIBLE);
            imgPrev.setVisibility(Button.VISIBLE);
        }
        if (indice == arrayPedidoCompra.length - 1) {
            buttonNext.setVisibility(Button.INVISIBLE);
            imgNext.setVisibility(Button.INVISIBLE);
        } else {
            buttonNext.setVisibility(Button.VISIBLE);
            imgNext.setVisibility(Button.VISIBLE);
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
        try {
            if (v.getId() == R.id.buttonPrev || v.getId() == R.id.imgPrev) {
                if (indice > 0) {
                    indice--;
                }
            }

            if (v.getId() == R.id.buttonNext || v.getId() == R.id.imgNext) {
                indice++;
            }
            setPedido(arrayPedidoCompra[indice]);
        } catch (Exception ignored) {

        }
    }
}
