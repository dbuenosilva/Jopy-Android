package br.com.gwaya.jopy.activity;

import android.support.v7.app.ActionBarActivity;

/**
 * Created by pedro on 18/03/15.
 */
public abstract class Aba extends ActionBarActivity {

    public abstract String getTheTitle();

    public abstract int getIconTabID();

    public abstract int getNumeroAba();

}