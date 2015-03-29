package br.com.gwaya.jopy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.enums.StatusPedido;
import br.com.gwaya.jopy.model.PedidoCompra;


public class AdapterPedidoCompra extends BaseAdapter {

    private final Context context;
    private List<PedidoCompra> lista;
    private StatusPedido statusPedido;

    public AdapterPedidoCompra(Context context, List<PedidoCompra> lista, StatusPedido statusPedido) {
        this.context = context;
        this.lista = lista;
        this.statusPedido = statusPedido;
    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public List<PedidoCompra> getItem(int i) {
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
            convertView = inflater.inflate(R.layout.adapter_pedidocompra, parent, false);

            viewHolder.textViewNomeForn = (TextView) convertView.findViewById(R.id.textViewNomeForn);
            viewHolder.textViewTotalPedido = (TextView) convertView.findViewById(R.id.textViewTotalPedido);
            viewHolder.textViewDtPedido = (TextView) convertView.findViewById(R.id.textViewDtPedido);
            viewHolder.lnTipo = convertView.findViewById(R.id.lnTipo);
            viewHolder.viewMarcadorLateral = convertView.findViewById(R.id.viewMarcadorLateral);
            viewHolder.viewMarcadorRodape = convertView.findViewById(R.id.viewMarcadorRodape);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        PedidoCompra pedido = lista.get(position);

        if (StatusPedido.EMITIDO.getTexto().equalsIgnoreCase(pedido.getStatusPedido())) {
            viewHolder.lnTipo.setBackgroundResource(R.color.emitido2);
        } else if (StatusPedido.APROVADO.getTexto().equalsIgnoreCase(pedido.getStatusPedido())) {
            viewHolder.lnTipo.setBackgroundResource(R.color.aprovado_fraco);
            viewHolder.viewMarcadorLateral.setBackgroundResource(R.color.aprovado_forte);
            viewHolder.viewMarcadorRodape.setBackgroundResource(R.color.aprovado_forte);
        } else {
            viewHolder.lnTipo.setBackgroundResource(R.color.rejeitado_fraco);
            viewHolder.viewMarcadorLateral.setBackgroundResource(R.color.rejeitado_forte);
            viewHolder.viewMarcadorRodape.setBackgroundResource(R.color.rejeitado_forte);
        }

        viewHolder.textViewNomeForn.setText(pedido.getNomeForn());
        viewHolder.textViewDtPedido.setText(getDataEmString(pedido));
        viewHolder.textViewTotalPedido.setText(NumberFormat.getCurrencyInstance().format(pedido.getTotalPedido()));

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

    private static class ViewHolder {
        TextView textViewNomeForn;
        TextView textViewTotalPedido;
        TextView textViewDtPedido;
        View lnTipo;
        View viewMarcadorLateral;
        View viewMarcadorRodape;
    }
}