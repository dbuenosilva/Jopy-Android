package br.com.gwaya.jopy.activity;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.adapter.AdapterDetalhePedidoCompra;
import br.com.gwaya.jopy.dao.FilaPedidoCompraDAO;
import br.com.gwaya.jopy.dao.PedidoCompraDAO;
import br.com.gwaya.jopy.model.PedidoCompra;
import br.com.gwaya.jopy.model.PedidoCompraItem;


public class ActivityDetalhe extends ActionBarActivity {

    public static final String APROVAR = "APROVAR";
    public static final String REJEITAR = "REJEITAR";
    public static final String STATUS_APROVADO = "aprovado";

    private PedidoCompra pedido;
    private ScrollView scrollView;
    private View relStatusRdp;
    private TextView txtStatusRdp;
    private View imgView;

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

    private void setRodapeEmitido() {

        Button buttonRejeitar = (Button) findViewById(R.id.buttonRejeitar);
        Button buttonAproButton = (Button) findViewById(R.id.buttonAprovar);

        final Context context = getApplicationContext();
        final int duration = Toast.LENGTH_SHORT;

        OnClickListener click = new OnClickListener() {

            @Override
            public void onClick(View v) {
                String tipo = null;
                try {
                    if (v.getId() == R.id.buttonAprovar) {
                        tipo = APROVAR;

                        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityDetalhe.this);
                        builder.setMessage("Deseja confirmar aprovação ?")
                                .setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        FilaPedidoCompraDAO filaDataSource = new FilaPedidoCompraDAO();
                                        PedidoCompraDAO dataSource = new PedidoCompraDAO();

                                        pedido.setStatusPedido(STATUS_APROVADO);
                                        pedido.setEnviado(0);
                                        pedido.setMotivoRejeicao("");

                                        filaDataSource.createFilaPedidoCompra(pedido);

                                        dataSource.updatePedidoCompra(pedido);

                                        Toast toast = Toast.makeText(context, "Pedido aprovado!", duration);
                                        toast.show();

                                        Intent data = new Intent();
                                        data.putExtra("myData1", "Data 1 value");
                                        data.putExtra("myData2", "Data 2 value");

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
                    }

                    if (v.getId() == R.id.buttonRejeitar) {

                        tipo = REJEITAR;

                        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityDetalhe.this);
                        // Get the layout inflater
                        final LayoutInflater inflater = ActivityDetalhe.this.getLayoutInflater();

                        // Inflate and set the layout for the dialogdxc
                        // Pass null as the parent view because its going in the dialog layout
                        final View view = inflater.inflate(R.layout.dialog_rejeicao, null);
                        final EditText textRej = (EditText) view.findViewById(R.id.txtPutRej);

                        builder.setView(view)
                                // Add action buttons
                                .setPositiveButton("CONFIRMAR", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {

                                        Intent data = new Intent();
                                        FilaPedidoCompraDAO filaDataSource =
                                                new FilaPedidoCompraDAO();
                                        PedidoCompraDAO dataSource =
                                                new PedidoCompraDAO();

                                        pedido.setStatusPedido("rejeitado");
                                        pedido.setEnviado(0);
                                        pedido.setMotivoRejeicao(textRej.getText().toString().replace("\n", "").replace("\r", ""));

                                        filaDataSource.createFilaPedidoCompra(pedido);

                                        dataSource.updatePedidoCompra(pedido);

                                        ActivityDetalhe.this.setResult(101, data);
                                        ActivityDetalhe.this.finish();

                                        Toast toast = Toast.makeText(context, "Rejeição confirmada!", duration);
                                        toast.show();
                                    }
                                })
                                .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Toast toast = Toast.makeText(context, "Rejeição cancelada!", duration);
                                        toast.show();
                                    }
                                });
                        final AlertDialog dialog = builder.create();
                        dialog.show();
                        final Button button = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
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
                        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);


                        /*
                        Intent intent = new Intent(DetalheActivity.this, AprovRejeicaoActivity.class);
                        intent.putExtra(PEDIDO, new Gson().toJson(pedido));
                        intent.putExtra(TIPO, tipo);
                        DetalheActivity.this.startActivity(intent);*/
                    }

                } catch (Exception ignored) {

                }
            }
        };

        buttonAproButton.setOnClickListener(click);
        buttonRejeitar.setOnClickListener(click);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        (new Runnable() {
            @Override
            public void run() {

                String jsonMyObject = "";
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    jsonMyObject = extras.getString("pedidocompra");
                }

                Gson gson = new Gson();

                pedido = gson.fromJson(jsonMyObject, PedidoCompra.class);

                if (pedido.getStatusPedido().equals("emitido")) {
                    setContentView(R.layout.detalhe_main);
                } else {
                    setContentView(R.layout.detalhe_aprov_rej);
                }

                scrollView = (ScrollView) findViewById(R.id.scrollView);

                //CUSTOM VIEW ACTIONBAR
                ActionBar mActionBar;
                mActionBar = getSupportActionBar();
                mActionBar.setDisplayShowHomeEnabled(false);
                mActionBar.setDisplayShowTitleEnabled(false);
                LayoutInflater mInflater = LayoutInflater.from(ActivityDetalhe.this);

                View mCustomView = mInflater.inflate(R.layout.actionbar_custom_title_view_centered, null);
                TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);
                String title = pedido.getNomeForn();

                if (title.length() > 25) {
                    title = title.substring(0, 24) + "...";
                }

                mTitleTextView.setText(title);

                ImageButton imageButton = (ImageButton) mCustomView
                        .findViewById(R.id.imageButton);
                imageButton.setOnClickListener(new OnClickListener() {

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

                AdapterDetalhePedidoCompra adapter = new AdapterDetalhePedidoCompra(ActivityDetalhe.this,
                        pedido.getItens().toArray(new PedidoCompraItem[pedido.getItens().size()]));

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

                // TODO - A variável dtMod e dtAprovRej, já estavam recebendo o mesmo valor (dtMod). Não sei se realmente deveria acontecer isto, checar regra de negócios.
                String dtEmi = pedido.getDtEmi();
                String dtNeces = pedido.getDtNeces();
                String dtMod = pedido.getDtMod();
                String dtAprovRej = pedido.getDtMod();
                String totalPedido = "";

                Date data = null;

                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH':'mm':'ss'.'SSSZ").create();

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

                String strStatus = pedido.getStatusPedido().equals("aprovado") ? "Aprovado" : "Motivo da Rejeição: " + pedido.getMotivo();

                textViewItem = (TextView) findViewById(R.id.txtStatus1);
                if (textViewItem != null) {
                    textViewItem.setText(strStatus);
                    textViewItem.setTag(pedido.get_id());
                }

                TextView txtPedido = (TextView) findViewById(R.id.txtPedido);
                txtPedido.setText(pedido.getIdSistema());
                txtPedido.setTag(pedido.get_id());

                textViewItem = (TextView) findViewById(R.id.txtForn);
                textViewItem.setText(pedido.getNomeForn());
                textViewItem.setTag(pedido.get_id());

                textViewItem = (TextView) findViewById(R.id.txtPagto);
                textViewItem.setText(pedido.getCondPagto());
                textViewItem.setTag(pedido.get_id());

                textViewItem = (TextView) findViewById(R.id.txtDtEmi);
                textViewItem.setText(dtEmi);
                textViewItem.setTag(pedido.get_id());

                textViewItem = (TextView) findViewById(R.id.txtNec);
                textViewItem.setText(dtNeces);
                textViewItem.setTag(pedido.get_id());

                textViewItem = (TextView) findViewById(R.id.txtSolic);
                textViewItem.setText(pedido.getSolicitante());
                textViewItem.setTag(pedido.get_id());

                textViewItem = (TextView) findViewById(R.id.txtCentroCusto);
                textViewItem.setText(pedido.getCentroCusto());
                textViewItem.setTag(pedido.get_id());

                textViewItem = (TextView) findViewById(R.id.txtTotal);
                textViewItem.setText(totalPedido);
                textViewItem.setTag(pedido.get_id());

                textViewItem = (TextView) findViewById(R.id.txtDtMod);
                textViewItem.setText("Data da última modificação: " + dtMod);
                textViewItem.setTag(pedido.get_id());

                RelativeLayout relativeLayout;

                relativeLayout = (RelativeLayout) findViewById(R.id.frmMotivo);

                relativeLayout.setVisibility(RelativeLayout.VISIBLE);
                textViewItem = (TextView) findViewById(R.id.txtMotivoPedido);
                textViewItem.setText(pedido.getMotivo());
                textViewItem.setTag(pedido.get_id());

                relStatusRdp = findViewById(R.id.relStatusRdp);
                txtStatusRdp = (TextView) findViewById(R.id.txtStatusRdp);


                if (pedido.getStatusPedido().equals("emitido")) {
                    setRodapeEmitido();
                } else {

                    imgView = findViewById(R.id.imgUpload);

                    if (pedido.getEnviado() == 1) {
                        imgView.setVisibility(View.INVISIBLE);
                    } else {
                        imgView.setVisibility(View.VISIBLE);
                    }

                    relativeLayout = (RelativeLayout) findViewById(R.id.layoutStatus1);
                    if (pedido.getStatusPedido().equals("aprovado")) {
                        //relativeLayout.setBackgroundResource(R.color.aprovado);
                        relativeLayout.setVisibility(View.GONE);
                        relStatusRdp.setBackgroundResource(R.color.aprovado);
                        txtStatusRdp.setText("Pedido aprovado");
                    } else {
                        relativeLayout.setBackgroundResource(R.color.rejeitado);
                        relativeLayout.setVisibility(View.VISIBLE);
                        relStatusRdp.setBackgroundResource(R.color.rejeitado);
                        txtStatusRdp.setText("Pedido rejeitado" + (dtAprovRej == null ? "" : " em " + dtAprovRej));

                        textViewItem = (TextView) findViewById(R.id.txtStatus2);
                        textViewItem.setText(pedido.getMotivoRejeicao() == null ? "" : pedido.getMotivoRejeicao());
                        textViewItem.setTag(pedido.get_id());
                    }
                }

                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_UP);
                    }
                });
            }
        }).run();

    }

}
