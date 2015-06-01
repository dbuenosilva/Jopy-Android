package br.com.gwaya.jopy.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.controller.ControllerPermissao;
import br.com.gwaya.jopy.enums.Acesso;
import br.com.gwaya.jopy.model.Permissao;

/**
 * Created by pedrofsn on 23/05/15.
 */
public class ActivityMenu extends Activity implements View.OnClickListener {

    private boolean login;
    private List<Permissao> listaPermissoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Bundle extras = getIntent().getExtras();
        login = false;

        if (extras != null) {
            login = extras.getBoolean("login");
        }

        ControllerPermissao controllerPermissao = new ControllerPermissao();
        listaPermissoes = controllerPermissao.readAll();

        findViewById(R.id.viewCompras).setOnClickListener(this);
        findViewById(R.id.viewVendas).setOnClickListener(this);
        findViewById(R.id.viewOrcamentos).setOnClickListener(this);
        findViewById(R.id.viewContasPagar).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.viewCompras:
//                if (validarPermissao(Acesso.COMPRAS)) {
                    Intent intent = new Intent(this, ActivityMain.class);
                    intent.putExtra("login", login);
                    startActivity(intent);
//                }
                break;
            case R.id.viewVendas:
                if (validarPermissao(Acesso.VENDAS)) {

                }
                break;
            case R.id.viewOrcamentos:
                if (validarPermissao(Acesso.ORCAMENTO)) {

                }
                break;
            case R.id.viewContasPagar:
                if (validarPermissao(Acesso.FINANCEIRO)) {

                }
                break;
        }
    }

    private boolean validarPermissao(Acesso acesso) {
        for (Permissao permissao : listaPermissoes) {
            return acesso.getValor() == permissao.getAcesso();
        }
        Toast.makeText(this, getString(R.string.solucao_em_desenvolvimento), Toast.LENGTH_SHORT).show();
        return false;
    }
}
