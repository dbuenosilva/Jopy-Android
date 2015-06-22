package br.com.gwaya.jopy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.adapter.viewholder.HorarioViewHolder;
import br.com.gwaya.jopy.domain.BaseRecyclerAdapter;
import br.com.gwaya.jopy.model.Horario;

/**
 * Created by pedro.sousa on 09/06/15.
 */
public class AdapterHorario extends BaseRecyclerAdapter<Horario, HorarioViewHolder> {

    public AdapterHorario(Context context, List<Horario> list) {
        super(context, list);
    }

    @Override
    public HorarioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.view_holder_horario, parent, false);
        return new HorarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HorarioViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.bindData(lista.get(position));
    }

}
