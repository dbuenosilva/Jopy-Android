package br.com.gwaya.jopy.activity;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.activity.abstracoes.ActivityGeneric;
import br.com.gwaya.jopy.adapter.AdapterPedidoCompraItens;
import br.com.gwaya.jopy.dao.FilaPedidoCompraDAO;
import br.com.gwaya.jopy.dao.PedidoCompraDAO;
import br.com.gwaya.jopy.model.PedidoCompra;

public class ActivityDetalhe extends ActivityGeneric implements OnClickListener {

    public static final String STATUS_APROVADO = "aprovado";

    private PedidoCompra pedido;
    private ScrollView scrollView;
    private View relStatusRdp;
    private TextView txtStatusRdp;
    private View imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            pedido = (PedidoCompra) extras.getSerializable("pedidocompra");
        }

        if (pedido != null) {
            if (pedido.getStatusPedido().equals("emitido")) {
                setContentView(R.layout.activity_detalhe);
            } else {
                setContentView(R.layout.activity_detalhe_aprov_rej);
            }

            scrollView = (ScrollView) findViewById(R.id.scrollView);

            //CUSTOM VIEW ACTIONBAR
            ActionBar mActionBar;
            mActionBar = getSupportActionBar();
            mActionBar.setDisplayShowHomeEnabled(false);
            mActionBar.setDisplayShowTitleEnabled(false);
            LayoutInflater mInflater = LayoutInflater.from(ActivityDetalhe.this);

            View mCustomView = mInflater.inflate(R.layout.actionbar_activity_detalhes, null);
            TextView title_main = (TextView) mCustomView.findViewById(R.id.title_main);
            String title = pedido.getNomeForn().trim();

            if (title.length() > 25) {
                title = title.substring(0, 24) + "...";
            }

            title_main.setText(title);

            (mCustomView
                    .findViewById(R.id.imageButton)).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ActivityDetalhe.this, ActivityHistorico.class);
                    intent.putExtra("codForn", pedido.getCodForn());
                    ActivityDetalhe.this.startActivityForResult(intent, RESULT_OK);
                }
            });

            mActionBar.setCustomView(mCustomView);
            mActionBar.setDisplayShowCustomEnabled(true);
            //CUSTOM VIEW ACTIONBAR

            ListView pedidoList = (ListView) findViewById(R.id.listViewItens);

            AdapterPedidoCompraItens adapter = new AdapterPedidoCompraItens(this, pedido.getItens());

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
            String dtAprovRej = pedido.getDtAprov();
            String totalPedido = "";

            Date data = null;

            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            try {
                //data = gson.fromJson(dtEmi, Date.class);
                data = isoFormat.parse(dtEmi);
                dtEmi = dateFormat.format(data);

                data = isoFormat.parse(dtNeces);
                dtNeces = dateFormat.format(data);

                data = isoFormat.parse(dtMod);
                dtMod = dateFormat.format(data);

                if (dtAprovRej != null && !dtAprovRej.equals("")) {
                    data = isoFormat.parse(dtAprovRej);
                    dtAprovRej = dateFormat.format(data);
                }

                totalPedido = NumberFormat.getCurrencyInstance().format(pedido.getTotalPedido());

            } catch (Exception ex) {
                String str = ex.getMessage();
                Log.i("erro", str);
            }

            TextView textViewItem;

            TextView txtPedido = (TextView) findViewById(R.id.txtPedido);
            txtPedido.setText(pedido.getIdSistema());

            textViewItem = (TextView) findViewById(R.id.txtForn);
            textViewItem.setText(pedido.getNomeForn());


            textViewItem = (TextView) findViewById(R.id.txtPagto);
            textViewItem.setText(pedido.getCondPagto());


            textViewItem = (TextView) findViewById(R.id.txtDtEmi);
            textViewItem.setText(dtEmi);


            textViewItem = (TextView) findViewById(R.id.txtNec);
            textViewItem.setText(dtNeces);


            textViewItem = (TextView) findViewById(R.id.txtSolic);
            textViewItem.setText(pedido.getSolicitante());


            textViewItem = (TextView) findViewById(R.id.txtCentroCusto);
            textViewItem.setText(pedido.getCentroCusto());


            textViewItem = (TextView) findViewById(R.id.txtTotal);
            textViewItem.setText(totalPedido);


            textViewItem = (TextView) findViewById(R.id.txtDtMod);
            textViewItem.setText("Data da última modificação: " + dtMod);


            textViewItem = (TextView) findViewById(R.id.txtMotivoPedido);
            textViewItem.setText(pedido.getMotivo());


            relStatusRdp = findViewById(R.id.relStatusRdp);
            txtStatusRdp = (TextView) findViewById(R.id.txtStatusRdp);


            if (pedido.getStatusPedido().equals("emitido")) {
                findViewById(R.id.relativeLayoutRejeitar).setOnClickListener(this);
                findViewById(R.id.relativeLayoutAprovar).setOnClickListener(this);
            } else {

                imgView = findViewById(R.id.imgUpload);

                if (pedido.getEnviado() == 1) {
                    imgView.setVisibility(View.INVISIBLE);
                } else {
                    imgView.setVisibility(View.VISIBLE);
                }

                if (pedido.getStatusPedido().equals("aprovado")) {
                    relStatusRdp.setBackgroundResource(R.color.aprovado_forte);
                    txtStatusRdp.setText("Pedido aprovado");
                } else {
                    relStatusRdp.setBackgroundResource(R.color.rejeitado_forte);
                    txtStatusRdp.setText("Pedido rejeitado" + (dtAprovRej == null ? "" : " em " + dtAprovRej));

                    textViewItem.setText(pedido.getMotivoRejeicao() == null ? "" : pedido.getMotivoRejeicao());

                }
            }

            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(View.FOCUS_UP);
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.relativeLayoutAprovar:

                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityDetalhe.this);
                builder.setMessage("Deseja confirmar aprovação ?")
                        .setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                FilaPedidoCompraDAO filaDataSource = new FilaPedidoCompraDAO();
                                PedidoCompraDAO dataSource = new PedidoCompraDAO();

                                Calendar c = Calendar.getInstance();
                                TimeZone gmt = TimeZone.getTimeZone("GMT");
                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                df.setTimeZone(gmt);
                                String dataFormatada = df.format(c.getTime());

                                pedido.setStatusPedido(STATUS_APROVADO);
                                pedido.setEnviado(0);
                                pedido.setMotivoRejeicao("");
                                pedido.setDtAprov(dataFormatada);

                                filaDataSource.createFilaPedidoCompra(pedido);

                                dataSource.updatePedidoCompra(pedido);

                                Toast.makeText(ActivityDetalhe.this, "Pedido aprovado!", Toast.LENGTH_SHORT).show();

                                Intent data = new Intent();
                                ActivityDetalhe.this.setResult(101, data);
                                ActivityDetalhe.this.finish();
                            }
                        })
                        .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                // Create the AlertDialog object and return it
                Dialog dialog = builder.create();
                dialog.show();
                break;

            case R.id.relativeLayoutRejeitar:

                AlertDialog.Builder builderRejeitar = new AlertDialog.Builder(ActivityDetalhe.this);
                // Get the layout inflater
                final LayoutInflater inflater = ActivityDetalhe.this.getLayoutInflater();

                // Inflate and set the layout for the dialogdxc
                // Pass null as the parent view because its going in the dialog layout
                final View viewDialog = inflater.inflate(R.layout.dialog_rejeicao, null);
                final EditText textRej = (EditText) viewDialog.findViewById(R.id.txtPutRej);

                builderRejeitar.setView(viewDialog)
                        // Add action buttons
                        .setPositiveButton("CONFIRMAR", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                                Intent data = new Intent();
                                FilaPedidoCompraDAO filaDataSource = new FilaPedidoCompraDAO();
                                PedidoCompraDAO dataSource = new PedidoCompraDAO();

                                Calendar c = Calendar.getInstance();
                                TimeZone gmt = TimeZone.getTimeZone("GMT");
                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                df.setTimeZone(gmt);
                                String dataFormatada = df.format(c.getTime());

                                pedido.setStatusPedido("rejeitado");
                                pedido.setEnviado(0);
                                pedido.setMotivoRejeicao(textRej.getText().toString().replace("\n", "").replace("\r", ""));
                                pedido.setDtRej(dataFormatada);

                                filaDataSource.createFilaPedidoCompra(pedido);

                                dataSource.updatePedidoCompra(pedido);

                                ActivityDetalhe.this.setResult(101, data);
                                ActivityDetalhe.this.finish();

                                Toast.makeText(ActivityDetalhe.this, "Rejeição confirmada!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(ActivityDetalhe.this, "Rejeição cancelada!", Toast.LENGTH_SHORT).show();
                            }
                        });

                final AlertDialog alertDialog = builderRejeitar.create();
                alertDialog.show();
                final Button button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                button.setEnabled(true);
                textRej.setImeOptions(EditorInfo.IME_ACTION_DONE);
                textRej.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        button.setEnabled(textRej.getText().length() > 0);
                    }
                });
                alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        scrollView.fullScroll(View.FOCUS_UP);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK, new Intent());
        finish();
    }
}
