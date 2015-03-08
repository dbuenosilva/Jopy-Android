package br.com.gwaya.jopy.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.NumberFormat;

import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.model.PedidoCompraItem;

public class AdapterDetalhePedidoCompra extends ArrayAdapter<PedidoCompraItem> {

    final Context mContext;
    final int layoutResourceId;
    PedidoCompraItem data[] = null;

    public AdapterDetalhePedidoCompra(Context mContext, PedidoCompraItem[] data) {
        super(mContext, R.layout.list_view_row_item_detalhe, data);

        this.layoutResourceId = R.layout.list_view_row_item_detalhe;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
        }

        final PedidoCompraItem pedido = data[position];

        String qtde = String.format("%.2f", pedido.getQtde());
        String valor = NumberFormat.getCurrencyInstance().format(pedido.getValor());
        String subTotal = "Total: ".concat(NumberFormat.getCurrencyInstance().format(pedido.getTotal()));

        TextView textViewProduto = (TextView) convertView.findViewById(R.id.textViewProduto);
        TextView textViewQtde = (TextView) convertView.findViewById(R.id.textViewQtde);
        TextView textViewSubTotal = (TextView) convertView.findViewById(R.id.textViewSubTotal);
        TextView txtSubTotal = (TextView) convertView.findViewById(R.id.txtSubTotal);
        ImageView imageButton = (ImageView) convertView.findViewById(R.id.imgBtnObs);

        textViewProduto.setText(pedido.getProduto());
        textViewQtde.setText(qtde);
        textViewSubTotal.setText(valor);
        txtSubTotal.setText(subTotal);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = "Não há nenhuma observação.";
                if (pedido.getObs() != null && !pedido.getObs().equals("")) {
                    msg = pedido.getObs();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(msg)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                // Create the AlertDialog object and return it
                Dialog dialog = builder.create();
                dialog.show();
            }
        });

        return convertView;
    }
}
