package br.com.gwaya.jopy.model;

/**
 * Created by pedrofsn on 27/05/2015.
 */
public class Permissao {

    /*
     * ACESSOS
     * 0 - Compras
     * 1 - Vendas
     * 2 - Orçamento
     * 3 - Financeiro
     * 4 - Ponto Eletrônico
     *
     * NÍVEIS
     * 0 - LER
     * 1 - ESCREVER
     * 2 - EDITAR
     * 3 - DELETAR
     */

    public static final String TABELA = "PERMISSOES";

    public static final String NIVEL = "NIVEL";
    public static final String ACESSO = "ACESSO";
    public static final String ID = "_id";

    private transient int id;
    private int nivel;
    private int acesso;

    public Permissao(int acesso, int nivel) {
        this.acesso = acesso;
        this.nivel = nivel;
    }

    public Permissao() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
