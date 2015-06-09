package br.com.gwaya.jopy.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.domain.BaseRecyclerAdapter;
import br.com.gwaya.jopy.model.Horario;

/**
 * Created by pedro.sousa on 09/06/15.
 */
public class AdapterHorario extends BaseRecyclerAdapter<Horario, AdapterHorario.HorarioViewHolder> {

    public AdapterHorario(Context context, List<Horario> list) {
        super(context, list);
    }

    @Override
    public HorarioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HorarioViewHolder(layoutInflater.inflate(R.layout.view_holder_horario, parent, false));
    }

    @Override
    public void onBindViewHolder(HorarioViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.bindData(lista.get(position));
    }

    public static class HorarioViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewHorario;

        public HorarioViewHolder(View view) {
            super(view);
            textViewHorario = (TextView) view.findViewById(R.id.textViewHorario);
        }

        public void bindData(Horario horario) {
            textViewHorario.setText(horario.getHorario());
        }

    }

}
