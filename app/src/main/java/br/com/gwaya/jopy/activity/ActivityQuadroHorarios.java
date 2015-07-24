package br.com.gwaya.jopy.activity;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.activity.abstracoes.ActivityGeneric;
import br.com.gwaya.jopy.adapter.AdapterHorario;
import br.com.gwaya.jopy.domain.RecyclerItemClickListener;
import br.com.gwaya.jopy.model.Horario;

public class ActivityQuadroHorarios extends ActivityGeneric implements TimePickerDialog.OnTimeSetListener {

    private AdapterHorario adapter;
    private Calendar calendar;
    private int posicaoSelecionada;
    private List<Horario> lista;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quadro_horarios);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.quadro_de_horarios));
        }

        findViewById(R.id.buttonCancelar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.buttonOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exibirJSON();
            }
        });

        calendar = Calendar.getInstance();

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        // SETA CONTEÚDO
        lista = new ArrayList<>();
        lista.add(new Horario("08:00"));
        lista.add(null);
        lista.add(new Horario("13:00"));
        lista.add(null);

        adapter = new AdapterHorario(this, lista);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (lista.get(position) == null) {
                            posicaoSelecionada = position;
                            TimePickerDialog.newInstance(ActivityQuadroHorarios.this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show(getFragmentManager(), "timePicker");
                        }
                    }
                })
        );
    }

    private void exibirJSON() {
        String json = new Gson().toJson(lista.toArray(), Horario[].class);
        Toast.makeText(this, json, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        String horaEmTexo = String.valueOf(hourOfDay);
        String minutoEmTexo = String.valueOf(minute);

        if (minute == 0) {
            minutoEmTexo = "00";
        }

        if (hourOfDay == 0) {
            horaEmTexo = "00";
        }

        lista.set(posicaoSelecionada, new Horario(horaEmTexo + ":" + minutoEmTexo));
        adapter.notifyDataSetChanged();
    }
}
