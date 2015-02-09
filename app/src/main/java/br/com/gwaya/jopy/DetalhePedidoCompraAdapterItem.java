package br.com.gwaya.jopy;

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


public class DetalhePedidoCompraAdapterItem extends ArrayAdapter<PedidoCompraItem> {
	
	Context mContext;
	int layoutResourceId;
	PedidoCompraItem data[] = null;
	
	public DetalhePedidoCompraAdapterItem(Context mContext, int layoutResourceId, PedidoCompraItem[] data) {
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
        
        String qtde = String.format("%.2f", pedido.qtde);
        String valor = String.format("%.2f", pedido.valor);
        String subTotal = String.format("%.2f", pedido.total);

        valor = NumberFormat.getCurrencyInstance().format(pedido.valor);
        subTotal = NumberFormat.getCurrencyInstance().format(pedido.total);

        valor = NumberFormat.getCurrencyInstance().format(pedido.valor);
        subTotal = NumberFormat.getCurrencyInstance().format(pedido.total);

        TextView textViewItem = (TextView) convertView.findViewById(R.id.textViewProduto);
        textViewItem.setText(pedido.produto);
        textViewItem.setTag(pedido._id);
        
        textViewItem = (TextView) convertView.findViewById(R.id.textViewQtde);
        textViewItem.setText(qtde);
        textViewItem.setTag(pedido._id);
        
        textViewItem = (TextView) convertView.findViewById(R.id.textViewSubTotal);
        textViewItem.setText(valor);
        textViewItem.setTag(pedido._id);
        
        textViewItem = (TextView) convertView.findViewById(R.id.txtSubTotal);
        textViewItem.setText(subTotal);
        textViewItem.setTag(pedido._id);

        ImageButton imageButton = (ImageButton) convertView.findViewById(R.id.imgBtnObs);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = "Não há nenhuma observação.";
                if (pedido.obs != null && !pedido.obs.equals("")) {
                    msg = pedido.obs;
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
