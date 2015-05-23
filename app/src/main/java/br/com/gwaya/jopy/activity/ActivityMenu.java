package br.com.gwaya.jopy.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import br.com.gwaya.jopy.R;

/**
 * Created by pedrofsn on 23/05/15.
 */
public class ActivityMenu extends Activity implements View.OnClickListener {

    private boolean login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Bundle extras = getIntent().getExtras();
        login = extras.getBoolean("login", true);

        findViewById(R.id.viewCompras).setOnClickListener(this);
        findViewById(R.id.viewOrcamentos).setOnClickListener(this);
        findViewById(R.id.viewVendas).setOnClickListener(this);
        findViewById(R.id.viewContasPagar).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.viewCompras:
                Intent intent = new Intent(this, ActivityMain.class);
                intent.putExtra("login", login);
                startActivity(intent);
                break;
            case R.id.viewOrcamentos:
            case R.id.viewVendas:
            case R.id.viewContasPagar:
                Toast.makeText(this, getString(R.string.funcionalidade_em_desenvolvimento), Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
