package br.com.gwaya.jopy.interfaces;

import android.content.Context;

/**
 * Created by pedrofsn on 08/03/2015.
 */
public interface IDownloadPedidos {

    public void showFalhaAoBaixar();

    public void showSemNovosProdutos();

    public void logoff(Context context, Integer codigoErro);
}
