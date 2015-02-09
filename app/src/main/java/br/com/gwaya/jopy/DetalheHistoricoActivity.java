package br.com.gwaya.jopy;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;

//import android.app.ActionBar;

//import android.support.v7.app.ActionBarActivity;

public class DetalheHistoricoActivity extends ActionBarActivity {

    private int indice;
    private PedidoCompra[] _pedidos;
    private String codForn;

    Button buttonPrev;
    Button buttonNext;

    ImageView imgPrev;
    ImageView imgNext;

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK, new Intent());
        finish();
    }

    private void setPedido(PedidoCompra pedido) {
        Gson gson = new Gson();

        //CUSTOM VIEW ACTIONBAR
        ActionBar mActionBar;
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.actionbar_default, null);
        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_main);
        mTitleTextView.setText("Histórico " + String.valueOf(indice + 1) + " de " + String.valueOf(_pedidos.length));
        mTitleTextView.setGravity(Gravity.CENTER);

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);
        //CUSTOM VIEW ACTIONBAR

        ListView pedidoList = (ListView)findViewById(R.id.listViewItens);

        DetalhePedidoCompraAdapterItem adapter = new DetalhePedidoCompraAdapterItem(this,
                R.layout.list_view_row_item_detalhe, pedido.itens.toArray(new PedidoCompraItem[pedido.itens.size()]));

        pedidoList.setAdapter(adapter);

        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++)
        {
            View listItem = adapter.getView(i, null, pedidoList);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams _params = pedidoList.getLayoutParams();

        _params.height = totalHeight + (pedidoList.getDividerHeight() * (adapter.getCount() - 1));

        pedidoList.setLayoutParams(_params);
        pedidoList.requestLayout();

        String dtEmi = pedido.dtEmi;
        String dtNeces = pedido.dtNeces;
        String dtMod = pedido.dtMod;
        String totalPedido = "";

        Date data = null;

        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH':'mm':'ss'.'SSSZ").create();

        try {
            //data = gson.fromJson(dtEmi, Date.class);
            data = (Date) isoFormat.parse(dtEmi);
            dtEmi = dateFormat.format(data);

            data = (Date) isoFormat.parse(dtNeces);
            dtNeces = dateFormat.format(data);

            data = (Date) isoFormat.parse(dtMod);
            dtMod = dateFormat.format(data);

            totalPedido = String.format("%.2f", pedido.totalPedido);

        } catch (Exception ex) {
            String str = ex.getMessage();
            Log.i("erro", str);
        }

        TextView txtPedido;

        txtPedido = (TextView) findViewById(R.id.txtPedido);
        txtPedido.setText(pedido.idSistema);
        txtPedido.setTag(pedido._id);

        TextView textViewItem = (TextView) findViewById(R.id.txtForn);
        textViewItem.setText(pedido.nomeForn);
        textViewItem.setTag(pedido._id);

        textViewItem = (TextView) findViewById(R.id.txtDtEmi);
        textViewItem.setText(dtEmi);
        textViewItem.setTag(pedido._id);

        textViewItem = (TextView) findViewById(R.id.txtNec);
        textViewItem.setText(dtNeces);
        textViewItem.setTag(pedido._id);

        textViewItem = (TextView) findViewById(R.id.txtSolic);
        textViewItem.setText(pedido.solicitante);
        textViewItem.setTag(pedido._id);

        textViewItem = (TextView) findViewById(R.id.txtCentroCusto);
        textViewItem.setText(pedido.centroCusto);
        textViewItem.setTag(pedido._id);

        textViewItem = (TextView) findViewById(R.id.txtTotal);
        textViewItem.setText(totalPedido);
        textViewItem.setTag(pedido._id);

        textViewItem = (TextView) findViewById(R.id.txtDtMod);
        textViewItem.setText("Data da última modificação: " + dtMod);
        textViewItem.setTag(pedido._id);

        textViewItem = (TextView) findViewById(R.id.txtMotivoPedido);
        textViewItem.setText(pedido.motivo);
        textViewItem.setTag(pedido._id);

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.layoutStatus);
        relativeLayout.setVisibility(RelativeLayout.VISIBLE);

        String strStatus = pedido.statusPedido.equals("aprovado") ? "Aprovado" : "Motivo da Rejeição: " + pedido.motivoRejeicao;

        if (pedido.statusPedido.equals("aprovado")) {
            relativeLayout.setBackgroundResource(R.color.aprovado);
        } else {
            relativeLayout.setBackgroundResource(R.color.rejeitado);
        }

        txtPedido = (TextView) findViewById(R.id.txtStatus);
        txtPedido.setText(strStatus);
        txtPedido.setTag(pedido._id);

        if (indice == 0) {
            buttonPrev.setVisibility(Button.INVISIBLE);
            imgPrev.setVisibility(Button.INVISIBLE);
        } else {
            buttonPrev.setVisibility(Button.VISIBLE);
            imgPrev.setVisibility(Button.VISIBLE);
        }
        if (indice == _pedidos.length - 1) {
            buttonNext.setVisibility(Button.INVISIBLE);
            imgNext.setVisibility(Button.INVISIBLE);
        } else {
            buttonNext.setVisibility(Button.VISIBLE);
            imgNext.setVisibility(Button.VISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalhe_historico);

        buttonPrev = (Button) findViewById(R.id.buttonPrev);
        buttonNext = (Button) findViewById(R.id.buttonNext);

        imgPrev = (ImageView) findViewById(R.id.imgPrev);
        imgNext = (ImageView) findViewById(R.id.imgNext);

        String jsonMyObject = "";

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            jsonMyObject = extras.getString("pedidos");
            indice = extras.getInt("indice");
        }

        Gson gson = new Gson();

        _pedidos = gson.fromJson(jsonMyObject, PedidoCompra[].class);

        setPedido(_pedidos[indice]);

        final Context context = getApplicationContext();
        final int duration = Toast.LENGTH_SHORT;

        OnClickListener click = new OnClickListener() {

            @Override
            public void onClick(View v) {
                String tipo = null;
                try {
                    if (v.getId() == R.id.buttonPrev || v.getId() == R.id.imgPrev) {
                        if (indice > 0) {
                            indice--;
                        }
                    }

                    if (v.getId() == R.id.buttonNext || v.getId() == R.id.imgNext) {
                        indice++;
                    }
                    setPedido(_pedidos[indice]);
                } catch (Exception e) {

                }
            }
        };

        final ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_UP);
            }
        });

        buttonNext.setOnClickListener(click);
        buttonPrev.setOnClickListener(click);

        imgPrev.setOnClickListener(click);
        imgNext.setOnClickListener(click);
    }
}
