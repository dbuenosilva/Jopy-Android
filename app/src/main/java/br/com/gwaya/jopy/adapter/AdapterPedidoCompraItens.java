package br.com.gwaya.jopy.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;

import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.model.PedidoCompraItem;

public class AdapterPedidoCompraItens extends BaseAdapter {

    private final Context context;
    private List<PedidoCompraItem> lista;

    public AdapterPedidoCompraItens(Context context, List<PedidoCompraItem> lista) {
        this.context = context;
        this.lista = lista;
    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public List<PedidoCompraItem> getItem(int i) {
        return lista;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_view_row_item_detalhe, parent, false);

            viewHolder.textViewProduto = (TextView) convertView.findViewById(R.id.textViewProduto);
            viewHolder.textViewQtde = (TextView) convertView.findViewById(R.id.textViewQtde);
            viewHolder.textViewSubTotal = (TextView) convertView.findViewById(R.id.textViewSubTotal);
            viewHolder.txtSubTotal = (TextView) convertView.findViewById(R.id.txtSubTotal);
            viewHolder.imageButton = (ImageView) convertView.findViewById(R.id.imgBtnObs);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final PedidoCompraItem pedido = lista.get(position);

        String qtde = String.format("%.2f", pedido.getQtde());
        String valor = NumberFormat.getCurrencyInstance().format(pedido.getValor());
        String subTotal = "Total: ".concat(NumberFormat.getCurrencyInstance().format(pedido.getTotal()));

        viewHolder.textViewProduto.setText(pedido.getProduto());
        viewHolder.textViewQtde.setText(qtde);
        viewHolder.textViewSubTotal.setText(valor);
        viewHolder.txtSubTotal.setText(subTotal);

        viewHolder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = "Não há nenhuma observação.";
                if (pedido.getObs() != null && !pedido.getObs().equals("")) {
                    msg = pedido.getObs();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
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

    private static class ViewHolder {
        TextView textViewProduto;
        TextView textViewQtde;
        TextView textViewSubTotal;
        TextView txtSubTotal;
        ImageView imageButton;
    }
}