package br.com.gwaya.jopy.adapter;

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

import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.model.PedidoCompra;


public class AdapterHistorico extends ArrayAdapter<PedidoCompra> {

    private final Context mContext;
    private final int layoutResourceId;
    private List<PedidoCompra> data = null;

    public AdapterHistorico(Context mContext, List<PedidoCompra> data) {
        super(mContext, R.layout.rowitem_historico, data);

        this.layoutResourceId = R.layout.rowitem_historico;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
        }

        PedidoCompra pedido = data.get(position);

        final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        final String ISOFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        Date date;
        try {
            FrameLayout frameLayout = (FrameLayout) convertView.findViewById(R.id.frmStatus);
            int resourceColor = R.color.emitido;
            if (pedido.getStatusPedido().equals("aprovado")) {
                resourceColor = R.color.aprovado_forte;
            } else if (pedido.getStatusPedido().equals("rejeitado")) {
                resourceColor = R.color.rejeitado_forte;
            } else {
                resourceColor = R.color.emitido;
            }

            //convertView.setBackgroundResource(resourceColor);
            frameLayout.setBackgroundResource(resourceColor);

            date = (new SimpleDateFormat(ISOFormat)).parse(pedido.getDtEmi());

            String dtEmi = format.format(date);
            String totalPedido = String.format("%.2f", pedido.getTotalPedido());

            totalPedido = NumberFormat.getCurrencyInstance().format(pedido.getTotalPedido());

            // get the TextView and then set the text (item name) and tag (item ID) values
            TextView textViewItem = (TextView) convertView.findViewById(R.id.txtHNomeFor);
            textViewItem.setText(pedido.getNomeForn());
            textViewItem.setTag(pedido.get_id());

            textViewItem = (TextView) convertView.findViewById(R.id.txtHTotal);
            textViewItem.setText(totalPedido);
            textViewItem.setTag(pedido.get_id());

            textViewItem = (TextView) convertView.findViewById(R.id.txtDtEmi);
            textViewItem.setText(dtEmi);
            textViewItem.setTag(pedido.get_id());

        } catch (Exception ignored) {

        }

        return convertView;
    }
}
