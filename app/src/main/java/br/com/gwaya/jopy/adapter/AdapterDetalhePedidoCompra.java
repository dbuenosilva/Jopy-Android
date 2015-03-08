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
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.NumberFormat;

import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.model.PedidoCompraItem;


public class AdapterDetalhePedidoCompra extends ArrayAdapter<PedidoCompraItem> {

    Context mContext;
    int layoutResourceId;
    PedidoCompraItem data[] = null;

    public AdapterDetalhePedidoCompra(Context mContext, int layoutResourceId, PedidoCompraItem[] data) {
        super(mContext, layoutResourceId, data);

        this.layoutResourceId = layoutResourceId;
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
        String valor = String.format("%.2f", pedido.getValor());
        String subTotal = String.format("%.2f", pedido.getTotal());

        valor = NumberFormat.getCurrencyInstance().format(pedido.getValor());
        subTotal = NumberFormat.getCurrencyInstance().format(pedido.getTotal());

        valor = NumberFormat.getCurrencyInstance().format(pedido.getValor());
        subTotal = NumberFormat.getCurrencyInstance().format(pedido.getTotal());

        TextView textViewItem = (TextView) convertView.findViewById(R.id.textViewProduto);
        textViewItem.setText(pedido.getProduto());
        textViewItem.setTag(pedido.get_id());

        textViewItem = (TextView) convertView.findViewById(R.id.textViewQtde);
        textViewItem.setText(qtde);
        textViewItem.setTag(pedido.get_id());

        textViewItem = (TextView) convertView.findViewById(R.id.textViewSubTotal);
        textViewItem.setText(valor);
        textViewItem.setTag(pedido.get_id());

        textViewItem = (TextView) convertView.findViewById(R.id.txtSubTotal);
        textViewItem.setText(subTotal);
        textViewItem.setTag(pedido.get_id());

        ImageButton imageButton = (ImageButton) convertView.findViewById(R.id.imgBtnObs);
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
