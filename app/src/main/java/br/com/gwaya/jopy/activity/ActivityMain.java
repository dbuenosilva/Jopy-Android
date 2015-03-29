package br.com.gwaya.jopy.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;

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

public class ActivityMain extends TabActivity {

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

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
/*
                for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {

                    ((TextView) tabHost.getTabWidget().getChildAt(i).findViewById(R.id.title))
                            .setTextColor(Color.parseColor("#FFFFFF")); //unselected

                }

                View indicator = tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab());
                TextView title = (TextView) indicator.findViewById(R.id.title);
*/
                switch (tabId) {
                    case "Pedidos Pendentes":
                        //                      title.setTextColor(getResources().getColor(R.color.emitido));
                        App.ABA_ATUAL = ActivityPendentes.ID;
                        break;
                    case "Pedidos Aprovados":
                        //                    title.setTextColor(getResources().getColor(R.color.aprovado_forte));
                        App.ABA_ATUAL = ActivityAprovados.ID;
                        break;
                    case "Pedidos Rejeitados":
                        //              title.setTextColor(getResources().getColor(R.color.rejeitado));
                        App.ABA_ATUAL = ActivityRejeitados.ID;
                        break;
                    case "Sobre":
                        //              title.setTextColor(getResources().getColor(R.color.emitido));
                        App.ABA_ATUAL = ActivitySobre.ID;
                        break;
                }

            }
        });
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

                //TextView title = (TextView) tabIndicator.findViewById(R.id.title);
                ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);

                //title.setText(aba.getNomeAba().replace("Pedidos ", ""));
                icon.setImageResource(aba.getIconTabID());
/*
                if ("Pedidos Pendentes".equals(aba.getNomeAba())) {
                    title.setTextColor(getResources().getColor(R.color.emitido));
                } else {
                    title.setTextColor(Color.parseColor("#FFFFFF"));
                }
*/
                TabHost.TabSpec spec = tabHost.newTabSpec(aba.getNomeAba());
                spec.setIndicator(tabIndicator);
                spec.setContent(intent);

                tabHost.addTab(spec);
            }
        }
    }
}
