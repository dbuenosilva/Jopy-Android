package br.com.gwaya.jopy.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.model.PedidoCompra;


public class PedidoCompraAdapterItem extends ArrayAdapter<PedidoCompra> {

    protected View lnTipo;
    Context mContext;
    int layoutResourceId;
    List<PedidoCompra> data = null;

    public PedidoCompraAdapterItem(Context mContext, int layoutResourceId, List<PedidoCompra> data) {
        super(mContext, layoutResourceId, data);

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            // inflate the layout
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
        }

        PedidoCompra pedido = data.get(position);


        lnTipo = (View) convertView.findViewById(R.id.lnTipo);

        if (pedido.getStatusPedido().equals("emitido")) {
            lnTipo.setBackgroundResource(R.color.emitido2);
        } else if (pedido.getStatusPedido().equals("aprovado")) {
            lnTipo.setBackgroundResource(R.color.aprovado2);
        } else {
            lnTipo.setBackgroundResource(R.color.rejeitado2);
        }

        final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        final String ISOFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        Date date;
        try {
            date = (new SimpleDateFormat(ISOFormat)).parse(pedido.getDtNeces());

            String dtEmi = format.format(date);
            String totalPedido = String.format("%.2f", pedido.getTotalPedido());
            totalPedido = NumberFormat.getCurrencyInstance().format(pedido.getTotalPedido());

            TextView textViewItem = (TextView) convertView.findViewById(R.id.textViewNomeForn);
            textViewItem.setText(pedido.getNomeForn());
            textViewItem.setTag(pedido.get_id());

            textViewItem = (TextView) convertView.findViewById(R.id.textViewTotalPedido);
            textViewItem.setText(totalPedido);
            textViewItem.setTag(pedido.get_id());

            textViewItem = (TextView) convertView.findViewById(R.id.textViewDtPedido);
            textViewItem.setText(dtEmi);
            textViewItem.setTag(pedido.get_id());
        } catch (Exception e) {

        }

        return convertView;
    }
}
