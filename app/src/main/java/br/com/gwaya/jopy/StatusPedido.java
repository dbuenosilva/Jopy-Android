package br.com.gwaya.jopy;

/**
 * Created by pedro on 13/03/15.
 */
public enum StatusPedido {

    EMITIDO(0), APROVADO(1), REJEITADO(2);

    private int valor = 0;

    StatusPedido(int valor) {
        this.valor = valor;
    }

    public static String getText(int valor) {
        if (EMITIDO.getValor() == valor) {
            return "emitido";
        } else if (APROVADO.getValor() == valor) {
            return "aprovado";
        } else {
            return "rejeitado";
        }
    }

    public static StatusPedido getFromInt(int valor) {
        if (EMITIDO.getValor() == valor) {
            return StatusPedido.EMITIDO;
        } else if (APROVADO.getValor() == valor) {
            return StatusPedido.APROVADO;
        } else {
            return StatusPedido.REJEITADO;
        }
    }

    public static StatusPedido getFromText(String valor) {
        if (APROVADO.getTexto().equals(valor)) {
            return StatusPedido.APROVADO;
        } else if (REJEITADO.getTexto().equals(valor)) {
            return StatusPedido.REJEITADO;
        } else {
            return StatusPedido.EMITIDO;
        }
    }

    public int getValor() {
        return valor;
    }

    public String getTexto() {
        return getText(valor);
    }

}
