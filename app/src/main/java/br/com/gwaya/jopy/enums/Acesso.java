package br.com.gwaya.jopy.enums;

/**
 * Created by pedrofsn on 28/05/2015.
 */
public enum Acesso {

    COMPRAS(0), VENDAS(1), ORCAMENTO(2), FINANCEIRO(3), PONTO_ELETRONICO(4);

    private int valor = 0;

    Acesso(int valor) {
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }
}
