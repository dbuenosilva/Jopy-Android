package br.com.gwaya.jopy.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
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
import br.com.gwaya.jopy.dao.PedidoCompraDAO;

public class ActivityMain extends TabActivity {

    private Boolean login;

    private TabHost tabHost;

    private List<Aba> listaAbas = new ArrayList<>();

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

                for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
                    ((TextView) tabHost.getTabWidget().getChildAt(i).findViewById(R.id.title))
                            .setTextColor(Color.parseColor("#FFFFFF")); //unselected
                }

                View indicator = tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab());
                TextView title = (TextView) indicator.findViewById(R.id.title);

                switch (tabId) {
                    case "Pedidos Pendentes":
                        title.setTextColor(getResources().getColor(R.color.emitido));
                        App.ABA_ATUAL = 0;
                        break;
                    case "Pedidos Aprovados":
                        title.setTextColor(getResources().getColor(R.color.aprovado));
                        App.ABA_ATUAL = 1;
                        break;
                    case "Pedidos Rejeitados":
                        title.setTextColor(getResources().getColor(R.color.rejeitado));
                        App.ABA_ATUAL = 2;
                        break;
                    case "Sobre":
                        title.setTextColor(getResources().getColor(R.color.emitido));
                        App.ABA_ATUAL = 3;
                        break;
                }
            }
        });

        tabHost.setCurrentTab(App.ABA_ATUAL);
    }

    private void popularListaDeAbas() {

        if (listaAbas != null && listaAbas.size() == 0) {
            listaAbas.add(new ActivityPendentes());
            listaAbas.add(new ActivityAprovados());
            listaAbas.add(new ActivityRejeitados());
            listaAbas.add(new ActivityOpcoes());

            for (Aba aba : listaAbas) {

                Intent intent = new Intent(this, aba.getClass());
                intent.putExtra("login", login);

                View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_indicator, getTabWidget(), false);

                TextView title = (TextView) tabIndicator.findViewById(R.id.title);
                ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);

                title.setText(aba.getTheTitle().replace("Pedidos ", ""));
                icon.setImageResource(aba.getIconTabID());

                if ("Pedidos Pendentes".equals(aba.getTheTitle())) {
                    title.setTextColor(getResources().getColor(R.color.emitido));
                } else {
                    title.setTextColor(Color.parseColor("#FFFFFF"));
                }

                TabHost.TabSpec spec = tabHost.newTabSpec(aba.getTheTitle());
                spec.setIndicator(tabIndicator);
                spec.setContent(intent);

                tabHost.addTab(spec);
            }
        }
    }
}
