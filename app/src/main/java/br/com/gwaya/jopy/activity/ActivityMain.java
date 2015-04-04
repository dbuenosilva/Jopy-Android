package br.com.gwaya.jopy.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.gwaya.jopy.App;
import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.activity.abas.ActivityAprovados;
import br.com.gwaya.jopy.activity.abas.ActivityPendentes;
import br.com.gwaya.jopy.activity.abas.ActivityRejeitados;
import br.com.gwaya.jopy.activity.abas.ActivitySobre;
import br.com.gwaya.jopy.activity.abstracoes.Aba;
import br.com.gwaya.jopy.dao.PedidoCompraDAO;

public class ActivityMain extends TabActivity implements TabHost.OnTabChangeListener {

    private static TabHost tabHost;
    private Boolean login;
    private List<Aba> listaAbas = new ArrayList<>();

    public static void setTab(int idAba) {
        tabHost.setCurrentTab(idAba);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle extras = getIntent().getExtras();
        login = false;

        if (extras != null) {
            login = extras.getBoolean("login");
            if (login) {
                new PedidoCompraDAO().deleteAll();
            }
        }

        tabHost = getTabHost();

        popularListaDeAbas();

        tabHost.setOnTabChangedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTab(App.ABA_ATUAL);
    }

    private void popularListaDeAbas() {

        if (listaAbas != null && listaAbas.size() == 0) {
            listaAbas.add(new ActivityPendentes());
            listaAbas.add(new ActivityAprovados());
            listaAbas.add(new ActivityRejeitados());
            listaAbas.add(new ActivitySobre());

            for (Aba aba : listaAbas) {

                Intent intent = new Intent(this, aba.getClass());
                intent.putExtra("login", login);

                View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_indicator, getTabWidget(), false);

                ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
                TextView textView = (TextView) tabIndicator.findViewById(R.id.textView);

                icon.setImageResource(aba.getIconTabID());
                textView.setText(aba.getNomeAba());

                TabHost.TabSpec spec = tabHost.newTabSpec(aba.getNomeAba());
                spec.setIndicator(tabIndicator);
                spec.setContent(intent);

                tabHost.addTab(spec);
            }
        }
    }

    @Override
    public void onTabChanged(String tabId) {

        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {

            ((TextView) tabHost.getTabWidget().getChildAt(i).findViewById(R.id.textView))
                    .setTextColor(getResources().getColor(R.color.cinza_claro));

        }

        View indicator = tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab());
        TextView textView = (TextView) indicator.findViewById(R.id.textView);

        switch (tabId) {
            case "Pedidos Pendentes":
                App.ABA_ATUAL = ActivityPendentes.ID;
                break;
            case "Pedidos Aprovados":
                App.ABA_ATUAL = ActivityAprovados.ID;
                break;
            case "Pedidos Rejeitados":
                App.ABA_ATUAL = ActivityRejeitados.ID;
                break;
            case "Sobre":
                App.ABA_ATUAL = ActivitySobre.ID;
                break;
        }

        textView.setTextColor(getResources().getColor(android.R.color.white));

    }
}
