package br.com.gwaya.jopy.activity.abstracoes;

import android.support.v7.app.ActionBarActivity;

/**
 * Created by pedro on 18/03/15.
 */
public abstract class Aba extends ActionBarActivity {

    public abstract String getNomeAba();

    public abstract String getTituloTela();

    public abstract int getIconTabID();

}