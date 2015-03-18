package br.com.gwaya.jopy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.dao.AcessoDAO;
import br.com.gwaya.jopy.dao.PedidoCompraDAO;

/**
 * Created by marcelorosa on 11/01/15.
 */
public class ActivityOpcoes extends Aba {

    AcessoDAO AcessoDAO;
    PedidoCompraDAO dataSource;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_opcoes);

        WebView view = (WebView) findViewById(R.id.webView);
        String text;
        text = "<html><body>";
        //text += "<h3 align=\"center\">Versão 1.0 (Beta 9)</h3>";
        text += "<div style=\"scroll:auto\">";
        text += "<p align=\"justify\">Concebra PRO é uma solução oferecida pela Triunfo Concebra a seus profissionais para otimizar o trabalho no dia a dia com o sistema de compras da empresa. Por ele é possível aprovar ou rejeitar pedidos a partir de um dispositivo mobile. A atualização dos pedidos é sincronizada em ERP possibilitando o trabalho de maneira remota.</p>";
        text += "<p align=\"justify\">Você pode analisar os pedidos mesmo quando não há conexão com a internet. Os dados analisados ficam armazenados no banco de dados do dispositivo até que seja estabelecida uma conexão com a internet enviando as informações automaticamente.</p>";
        text += "<p align=\"justify\">O aplicativo oferece o histórico de pedidos proporcionando análise de compras por fornecedor, produto e/ou centro de custo.</p>";
        //text += "<p align=\"justify\">\"</p>";
        //text += "<p align=\"justify\">\"</p>";
        text += "</div>";
        text += "</body></html>";

        try {
            //String uri = Uri.encode(text);

            //view.loadData(text, "text/html; charset=UTF-8", null);

            view.loadUrl("file:///android_asset/index.html");

            //view.loadData(text, "text/html", "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        dataSource = new PedidoCompraDAO();

        AcessoDAO = new AcessoDAO();

        RelativeLayout layoutLogoff = (RelativeLayout) findViewById(R.id.layoutLogoff);
        layoutLogoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AcessoDAO.deleteAcesso();


                    dataSource.deleteAll();


                    Intent intent = new Intent(ActivityOpcoes.this, ActivityLogin.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    ActivityOpcoes.this.startActivity(intent);

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
        mTitleTextView.setText("Sobre");

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);
    }

    @Override
    public int getIconTabID() {
        return R.drawable.tab_opcoes;
    }

    @Override
    public String getTheTitle() {
        return "Sobre";
    }

    @Override
    public int getNumeroAba() {
        return 3;
    }
}
