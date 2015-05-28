package br.com.gwaya.jopy.enums;

/**
 * Created by pedro on 13/03/15.
 */
public enum Nivel {

    LER(0), ESCREVER(1), ATUALIZAR(2), REMOVER(3);

    private int valor = 0;

    Nivel(int valor) {
        this.valor = valor;
    }

//    public static String getText(int valor) {
//        if (ESCREVER.getValor() == valor) {
//            return "ESCREVER";
//        } else if (ATUALIZAR.getValor() == valor) {
//            return "ATUALIZAR";
//        } else if (REMOVER.getValor() == valor) {
//            return "REMOVER";
//        } else {
//            return "LER";
//        }
//    }

    public static Nivel getFromInt(int valor) {
        if (ESCREVER.getValor() == valor) {
            return Nivel.ESCREVER;
        } else if (ATUALIZAR.getValor() == valor) {
            return Nivel.ATUALIZAR;
        } else if (REMOVER.getValor() == valor) {
            return Nivel.REMOVER;
        } else {
            return Nivel.LER;
        }
    }

//    public static Nivel getFromText(String valor) {
//        if (APROVADO.getTexto().equals(valor)) {
//            return Nivel.APROVADO;
//        } else if (REJEITADO.getTexto().equals(valor)) {
//            return Nivel.REJEITADO;
//        } else {
//            return Nivel.EMITIDO;
//        }
//    }

    public int getValor() {
        return valor;
    }

//    public String getTexto() {
//        return getText(valor);
//    }

}
