package br.com.gwaya.jopy.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.model.Horario;

/**
 * Created by pedro.sousa on 22/06/15.
 */
public class HorarioViewHolder extends RecyclerView.ViewHolder {

    private Button button;

    public HorarioViewHolder(View view) {
        super(view);
        button = (Button) view.findViewById(R.id.button);
    }

    public void bindData(Horario horario) {
        if (horario != null) {
            button.setText(horario.getHorario());
            button.setClickable(false);
        } else {
            button.setText("Informar");
            button.setClickable(true);
        }

    }

    public Button getButton() {
        return button;
    }
}
