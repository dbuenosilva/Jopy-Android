package br.com.gwaya.jopy;

import java.util.List;

public class PedidoCompra {

    public String _id;
    public String idSistema;
    public String aprovadores;
    public int enviado;
    public String statusPedido;
    public String nomeForn;
    public String cpfCnpjForn;
    public String condPagto;
    public String codForn;
    public String dtEmi;
    public String dtNeces;
    public String dtRej;
    public String centroCusto;
    public String idSolicitante;
    public String solicitante;
    public String motivo;
    public String motivoRejeicao;
    public float totalPedido;
    public String obs;
    public String dtMod;
    public List<PedidoCompraItem> itens;
    public PedidoCompra() {
        this.enviado = 1;
    }
}
