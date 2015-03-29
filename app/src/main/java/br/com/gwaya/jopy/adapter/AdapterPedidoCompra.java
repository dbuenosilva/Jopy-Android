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
import br.com.gwaya.jopy.enums.StatusPedido;
import br.com.gwaya.jopy.model.PedidoCompra;


public class AdapterPedidoCompra extends ArrayAdapter<PedidoCompra> {

    private final Context mContext;
    private final int layoutResourceId;
    private View lnTipo;
    private View viewMarcadorLateral;
    private View viewMarcadorRodape;
    private List<PedidoCompra> data = null;
    private StatusPedido statusPedido;

    public AdapterPedidoCompra(Context mContext, List<PedidoCompra> data, StatusPedido statusPedido) {
        super(mContext, R.layout.adapter_pedidocompra, data);

        this.layoutResourceId = R.layout.adapter_pedidocompra;
        this.mContext = mContext;
        this.data = data;
        this.statusPedido = statusPedido;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            // inflate the layout
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
        }

        PedidoCompra pedido = data.get(position);


        lnTipo = convertView.findViewById(R.id.lnTipo);
        viewMarcadorLateral = convertView.findViewById(R.id.viewMarcadorLateral);
        viewMarcadorRodape = convertView.findViewById(R.id.viewMarcadorRodape);

        if (pedido.getStatusPedido().equals("emitido")) {
            lnTipo.setBackgroundResource(R.color.emitido2);
        } else if (pedido.getStatusPedido().equals("aprovado")) {
            lnTipo.setBackgroundResource(R.color.aprovado_fraco);
            viewMarcadorLateral.setBackgroundResource(R.color.aprovado_forte);
            viewMarcadorRodape.setBackgroundResource(R.color.aprovado_forte);
        } else {
            lnTipo.setBackgroundResource(R.color.rejeitado_fraco);
            viewMarcadorLateral.setBackgroundResource(R.color.rejeitado_forte);
            viewMarcadorRodape.setBackgroundResource(R.color.rejeitado_forte);
        }

        String totalPedido = String.format("%.2f", pedido.getTotalPedido());
        totalPedido = NumberFormat.getCurrencyInstance().format(pedido.getTotalPedido());

        TextView textViewItem = (TextView) convertView.findViewById(R.id.textViewNomeForn);
        textViewItem.setText(pedido.getNomeForn());
        textViewItem.setTag(pedido.get_id());

        textViewItem = (TextView) convertView.findViewById(R.id.textViewTotalPedido);
        textViewItem.setText(totalPedido);
        textViewItem.setTag(pedido.get_id());

        textViewItem = (TextView) convertView.findViewById(R.id.textViewDtPedido);
        textViewItem.setText(getDataEmString(pedido));
        textViewItem.setTag(pedido.get_id());


        return convertView;
    }

    public String getDataEmString(PedidoCompra pedido) {
        String data = "";

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String ISOFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

        try {

            if (StatusPedido.APROVADO == statusPedido) {
                data = pedido.getDtAprov();
            } else if (StatusPedido.REJEITADO == statusPedido) {
                data = pedido.getDtRej();
            } else {
                data = pedido.getDtNeces();
            }

            Date date = (new SimpleDateFormat(ISOFormat)).parse(data);
            data = format.format(date);

        } catch (Exception ignored) {

        }
        return data;
    }
}