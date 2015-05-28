package br.com.gwaya.jopy.model;

/**
 * Created by pedrofsn on 27/05/2015.
 */
public class Permissao {

    public static final String TABELA = "PERMISSOES";

    public static final String NIVEL = "NIVEL";
    public static final String ACESSO = "ACESSO";

    private int nivel;
    private int acesso;

    public Permissao(int nivel, int acesso) {
        this.nivel = nivel;
        this.acesso = acesso;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public int getAcesso() {
        return acesso;
    }

    public void setAcesso(int acesso) {
        this.acesso = acesso;
    }
}
