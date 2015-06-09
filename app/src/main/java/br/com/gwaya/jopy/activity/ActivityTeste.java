package br.com.gwaya.jopy.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.adapter.AdapterHorario;
import br.com.gwaya.jopy.model.Horario;

/**
 * Created by pedro.sousa on 09/06/15.
 */
public class ActivityTeste extends AppCompatActivity implements View.OnClickListener {

    AdapterHorario adapterHorario;
    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;
    private List<Horario> listHorario = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teste);
        initToolbar();
        initViews();
        initCode();
    }

    private void initCode() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        floatingActionButton.setOnClickListener(this);

        listHorario.add(new Horario());
        listHorario.add(new Horario());
        listHorario.add(new Horario());
        listHorario.add(new Horario());
        listHorario.add(new Horario());
        listHorario.add(new Horario());
        listHorario.add(new Horario());
        listHorario.add(new Horario());
        listHorario.add(new Horario());
        listHorario.add(new Horario());

        adapterHorario = new AdapterHorario(this, listHorario);
        recyclerView.setAdapter(adapterHorario);
    }

    private void initViews() {
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floating_btn);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Brincando com os códigos novos da Google");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floating_btn:
                listHorario.add(new Horario(new Date().toString()));
                adapterHorario.notifyDataSetChanged();
                break;
        }
    }
}
