package br.com.gwaya.jopy.activity.abas;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import java.util.List;

import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.activity.ActivityLogin;
import br.com.gwaya.jopy.activity.abstracoes.Aba;
import br.com.gwaya.jopy.dao.AcessoDAO;
import br.com.gwaya.jopy.dao.PedidoCompraDAO;
import br.com.gwaya.jopy.model.Acesso;
import br.com.gwaya.jopy.tasks.LogoutAsyncTask;


/**
 * Created by marcelorosa on 11/01/15.
 */
public class ActivitySobre extends Aba {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_opcoes);

        try {
            ((WebView) findViewById(R.id.webView)).loadUrl("file:///android_asset/index.html");
        } catch (Exception e) {
            e.printStackTrace();
        }

        findViewById(R.id.layoutLogoff).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    // Diego - 20/30 - informa para API com intuito de parar as push notifications para o device
                    List<Acesso> lstAcesso = new AcessoDAO().getAllAcesso();

                    if (lstAcesso.size() > 0) {

                        Acesso acesso = lstAcesso.get(0);

                        LogoutAsyncTask logout = new LogoutAsyncTask(ActivitySobre.this.getApplicationContext(), acesso);
                        logout.execute();

                    }

                    new AcessoDAO().deleteAcesso();
                    new PedidoCompraDAO().deleteAll();

                    Intent intent = new Intent(ActivitySobre.this, ActivityLogin.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    ActivitySobre.this.startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //CUSTOM VIEW ACTIONBAR
        ActionBar mActionBar;
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.actionbar_main, null);
        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_main);
        mTitleTextView.setText(getNomeAba());

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);
    }

    @Override
    public int getIconTabID() {
        return R.drawable.tab_opcoes;
    }

    @Override
    public String getNomeAba() {
        return "Sobre";
    }

}
