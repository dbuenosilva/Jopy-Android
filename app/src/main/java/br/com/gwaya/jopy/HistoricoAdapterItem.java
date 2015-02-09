package br.com.gwaya.jopy;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class HistoricoAdapterItem extends ArrayAdapter<PedidoCompra> {

    Context mContext;
    int layoutResourceId;
    List<PedidoCompra> data = null;

    public HistoricoAdapterItem(Context mContext, int layoutResourceId, List<PedidoCompra> data) {
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

        //convertView.setAlpha(new Float(0.1));

        PedidoCompra pedido = data.get(position);

        final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        final String ISOFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        Date date;
        try {
            FrameLayout frameLayout = (FrameLayout) convertView.findViewById(R.id.frmStatus);
            int resourceColor = R.color.emitido;
            if (pedido.statusPedido.equals("aprovado")) {
                resourceColor = R.color.aprovado;
            } else if (pedido.statusPedido.equals("rejeitado")) {
                resourceColor = R.color.rejeitado;
            } else {
                resourceColor = R.color.emitido;
            }

            //convertView.setBackgroundResource(resourceColor);
            frameLayout.setBackgroundResource(resourceColor);

            date = (new SimpleDateFormat(ISOFormat)).parse(pedido.dtEmi);

            String dtEmi = format.format(date);
            String totalPedido = String.format("%.2f", pedido.totalPedido);

            totalPedido = NumberFormat.getCurrencyInstance().format(pedido.totalPedido);

            // get the TextView and then set the text (item name) and tag (item ID) values
            TextView textViewItem = (TextView) convertView.findViewById(R.id.txtHNomeFor);
            textViewItem.setText(pedido.nomeForn);
            textViewItem.setTag(pedido._id);

            textViewItem = (TextView) convertView.findViewById(R.id.txtHTotal);
            textViewItem.setText(totalPedido);
            textViewItem.setTag(pedido._id);

            textViewItem = (TextView) convertView.findViewById(R.id.txtDtEmi);
            textViewItem.setText(dtEmi);
            textViewItem.setTag(pedido._id);

        } catch (Exception e) {

        }

        return convertView;
    }
}
