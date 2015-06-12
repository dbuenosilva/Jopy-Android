package br.com.gwaya.jopy.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.adapter.AdapterConfiguracaoOrdemMenu;

public class ActivityConfigurarOrdemTelaPrincipal extends ActionBarActivity {

    public static final String SHARED_PREFERENCES = "ORDEM_ABAS";
    private AdapterConfiguracaoOrdemMenu adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configurar_ordem_tela_principal);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.configurar_tela_principal));
            getSupportActionBar().setSubtitle(getString(R.string.altere_a_ordem_de_exibicao_das_telas));
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        adapter = new AdapterConfiguracaoOrdemMenu(recyclerView, getListaAbas(abas, ordemAbasPrincipais));

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private List<Integer> getListaAbas(List<Integer> abas, OrdemAbasPrincipais ordemAbasPrincipais) {

        if (ordemAbasPrincipais == null) {
            List<Integer> lista = new ArrayList<>();
            lista.add(0);
            lista.add(1);
            lista.add(2);
            ordemAbasPrincipais = new OrdemAbasPrincipais(lista);
        }

        for (int i : ordemAbasPrincipais.getAbas()) {
            switch (i) {
                case 1:
                    abas.add(AbasPrincipais.CEP.getValor());
                    break;
                break;
                default:
                    abas.add(AbasPrincipais.ALFABETICA.getValor());
                    break;
            }
        }

        return abas;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_configurar_ordem_tela_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ok:
                OrdemAbasPrincipais ordemAbasPrincipais = new OrdemAbasPrincipais(adapter.getData());
                String json = new Gson().toJson(ordemAbasPrincipais, OrdemAbasPrincipais.class);
                BLLConfiguracoes.salvarString(SHARED_PREFERENCES, "json", json);
                Toast.makeText(this, getString(R.string.suas_preferencias_foram_salvas_com_sucesso), Toast.LENGTH_SHORT).show();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
