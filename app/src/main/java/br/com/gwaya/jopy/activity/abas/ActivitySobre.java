package br.com.gwaya.jopy.activity.abas;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import java.util.List;

import br.com.gwaya.jopy.App;
import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.activity.ActivityLogin;
import br.com.gwaya.jopy.activity.abstracoes.Aba;
import br.com.gwaya.jopy.communication.PedidoCompraService;
import br.com.gwaya.jopy.dao.DadosAcessoDAO;
import br.com.gwaya.jopy.dao.PedidoCompraDAO;
import br.com.gwaya.jopy.interfaces.ILogout;
import br.com.gwaya.jopy.model.DadosAcesso;
import br.com.gwaya.jopy.tasks.LogoutAsyncTask;


/**
 * Created by pedrofsn on 31/03/15.
 */
public class ActivitySobre extends Aba implements ILogout {

    public static final int ID = 3;

    private LogoutAsyncTask asyncTaskLogout;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sobre);

        configActionBar();

        try {
            ((WebView) findViewById(R.id.webView)).loadUrl("file:///android_asset/index.html");
        } catch (Exception e) {
            e.printStackTrace();
        }

        findViewById(R.id.relativeLayoutLogoff).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Diego - 20/30 - informa para API com intuito de parar as push notifications para o device
                List<DadosAcesso> lstDadosAcesso = new DadosAcessoDAO().getAllDadosAcesso();

                if (lstDadosAcesso.size() > 0) {

                    DadosAcesso dadosAcesso = lstDadosAcesso.get(0);

                    asyncTaskLogout = null;
                    asyncTaskLogout = new LogoutAsyncTask(ActivitySobre.this, dadosAcesso);
                    asyncTaskLogout.execute();
                }
            }
        });
    }

    private void configActionBar() {
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

    @Override
    public String getTituloTela() {
        return getNomeAba();
    }

    @Override
    public void logout(boolean resultado) {
        new DadosAcessoDAO().deleteDadosAcesso();
        new PedidoCompraDAO().deleteAll();

        App.ABA_ATUAL = ActivityPendentes.ID;

        stopService(new Intent(this, PedidoCompraService.class));

        Intent intent = new Intent(this, ActivityLogin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        finish();
    }
}
