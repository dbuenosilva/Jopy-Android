package br.com.gwaya.jopy.interfaces;

import br.com.gwaya.jopy.model.DadosAcesso;

/**
 * Created by pedrofsn on 03/04/15.
 */
public interface ILoginAsyncTask {

    public void onLogon(Integer statusCode, DadosAcesso dadosAcessoLogin);

    public void onLogonFail();
}
