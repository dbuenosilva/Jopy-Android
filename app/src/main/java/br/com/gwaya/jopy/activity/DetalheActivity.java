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

import br.com.gwaya.jopy.DetalhePedidoCompraAdapterItem;
import br.com.gwaya.jopy.FilaPedidoCompraDataSource;
import br.com.gwaya.jopy.PedidoCompra;
import br.com.gwaya.jopy.PedidoCompraDataSource;
import br.com.gwaya.jopy.PedidoCompraItem;
import br.com.gwaya.jopy.R;


public class DetalheActivity extends ActionBarActivity {

    public static final String PEDIDO = "PEDIDO";
    public static final String TIPO = "TIPO";
    public static final String APROVAR = "APROVAR";
    public static final String REJEITAR = "REJEITAR";
    public static final String STATUS_APROVADO = "aprovado";

    private PedidoCompra pedido;
    private String codForn;
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

                        AlertDialog.Builder builder = new AlertDialog.Builder(DetalheActivity.this);
                        builder.setMessage("Deseja confirmar aprovação ?")
                                .setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        FilaPedidoCompraDataSource filaDataSource = new FilaPedidoCompraDataSource(DetalheActivity.this);
                                        PedidoCompraDataSource dataSource = new PedidoCompraDataSource(DetalheActivity.this);

                                        pedido.statusPedido = STATUS_APROVADO;
                                        pedido.enviado = 0;
                                        pedido.motivoRejeicao = "";

                                        filaDataSource.open();
                                        filaDataSource.createFilaPedidoCompra(pedido);
                                        filaDataSource.close();

                                        dataSource.open();
                                        dataSource.updatePedidoCompra(pedido);
                                        dataSource.close();

                                        Toast toast = Toast.makeText(context, "Pedido aprovado!", duration);
                                        toast.show();

                                        Intent data = new Intent();
                                        data.putExtra("myData1", "Data 1 value");
                                        data.putExtra("myData2", "Data 2 value");

                                        DetalheActivity.this.setResult(101, data);
                                        DetalheActivity.this.finish();
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

                        AlertDialog.Builder builder = new AlertDialog.Builder(DetalheActivity.this);
                        // Get the layout inflater
                        final LayoutInflater inflater = DetalheActivity.this.getLayoutInflater();

                        // Inflate and set the layout for the dialogdxc
                        // Pass null as the parent view because its going in the dialog layout
                        final View view = (View) inflater.inflate(R.layout.dialog_rejeicao, null);
                        final EditText textRej = (EditText) view.findViewById(R.id.txtPutRej);

                        builder.setView(view)
                                // Add action buttons
                                .setPositiveButton("CONFIRMAR", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {

                                        Intent data = new Intent();
                                        FilaPedidoCompraDataSource filaDataSource =
                                                new FilaPedidoCompraDataSource(DetalheActivity.this);
                                        PedidoCompraDataSource dataSource =
                                                new PedidoCompraDataSource(DetalheActivity.this);

                                        pedido.statusPedido = "rejeitado";
                                        pedido.enviado = 0;
                                        pedido.motivoRejeicao = textRej.getText().toString().replace("\n", "").replace("\r", "");

                                        filaDataSource.open();
                                        filaDataSource.createFilaPedidoCompra(pedido);
                                        filaDataSource.close();

                                        dataSource.open();
                                        dataSource.updatePedidoCompra(pedido);
                                        dataSource.close();

                                        DetalheActivity.this.setResult(101, data);
                                        DetalheActivity.this.finish();

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

                } catch (Exception e) {

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

                if (pedido.statusPedido.equals("emitido")) {
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
                LayoutInflater mInflater = LayoutInflater.from(DetalheActivity.this);

                View mCustomView = mInflater.inflate(R.layout.actionbar_custom_title_view_centered, null);
                TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);
                String title = pedido.nomeForn;

                if (title.length() > 25) {
                    title = title.substring(0, 24) + "...";
                }

                mTitleTextView.setText(title);

                ImageButton imageButton = (ImageButton) mCustomView
                        .findViewById(R.id.imageButton);
                imageButton.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(DetalheActivity.this, HistoricoActivity.class);
                        intent.putExtra("codForn", pedido.codForn);
                        DetalheActivity.this.startActivityForResult(intent, RESULT_OK);
                    }
                });

                mActionBar.setCustomView(mCustomView);
                mActionBar.setDisplayShowCustomEnabled(true);
                //CUSTOM VIEW ACTIONBAR

                //setTitle(pedido.nomeForn);

                ListView pedidoList = (ListView) findViewById(R.id.listViewItens);

                DetalhePedidoCompraAdapterItem adapter = new DetalhePedidoCompraAdapterItem(DetalheActivity.this,
                        R.layout.list_view_row_item_detalhe, pedido.itens.toArray(new PedidoCompraItem[pedido.itens.size()]));

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

                String dtEmi = pedido.dtEmi;
                String dtNeces = pedido.dtNeces;
                String dtMod = pedido.dtMod;
                String dtAprovRej = pedido.dtMod;
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

                    if (dtAprovRej != null && !dtAprovRej.equals("")) {
                        data = (Date) isoFormat.parse(dtAprovRej);
                        dtAprovRej = dateFormat.format(data);
                    }

                    totalPedido = NumberFormat.getCurrencyInstance().format(pedido.totalPedido);

                } catch (Exception ex) {
                    String str = ex.getMessage();
                    Log.i("erro", str);
                }

                TextView textViewItem;

                String strStatus = pedido.statusPedido.equals("aprovado") ? "Aprovado" : "Motivo da Rejeição: " + pedido.motivo;

                textViewItem = (TextView) findViewById(R.id.txtStatus1);
                if (textViewItem != null) {
                    textViewItem.setText(strStatus);
                    textViewItem.setTag(pedido._id);
                }

                TextView txtPedido = (TextView) findViewById(R.id.txtPedido);
                txtPedido.setText(pedido.idSistema);
                txtPedido.setTag(pedido._id);

                textViewItem = (TextView) findViewById(R.id.txtForn);
                textViewItem.setText(pedido.nomeForn);
                textViewItem.setTag(pedido._id);

                textViewItem = (TextView) findViewById(R.id.txtPagto);
                textViewItem.setText(pedido.condPagto);
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

                RelativeLayout relativeLayout;

                relativeLayout = (RelativeLayout) findViewById(R.id.frmMotivo);

                relativeLayout.setVisibility(RelativeLayout.VISIBLE);
                textViewItem = (TextView) findViewById(R.id.txtMotivoPedido);
                textViewItem.setText(pedido.motivo);
                textViewItem.setTag(pedido._id);

                relStatusRdp = findViewById(R.id.relStatusRdp);
                txtStatusRdp = (TextView) findViewById(R.id.txtStatusRdp);


                if (pedido.statusPedido.equals("emitido")) {
                    setRodapeEmitido();
                } else {

                    imgView = findViewById(R.id.imgUpload);

                    if (pedido.enviado == 1) {
                        imgView.setVisibility(View.INVISIBLE);
                    } else {
                        imgView.setVisibility(View.VISIBLE);
                    }

                    relativeLayout = (RelativeLayout) findViewById(R.id.layoutStatus1);
                    if (pedido.statusPedido.equals("aprovado")) {
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
                        textViewItem.setText(pedido.motivoRejeicao == null ? "" : pedido.motivoRejeicao);
                        textViewItem.setTag(pedido._id);
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
