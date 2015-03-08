package br.com.gwaya.jopy.model;

import java.util.List;

public class PedidoCompra {

    private String _id;
    private String idSistema;
    private String aprovadores;
    private int enviado;
    private String statusPedido;
    private String nomeForn;
    private String cpfCnpjForn;
    private String condPagto;
    private String codForn;
    private String dtEmi;
    private String dtNeces;
    private String dtRej;
    private String centroCusto;
    private String idSolicitante;
    private String solicitante;
    private String motivo;
    private String motivoRejeicao;
    private float totalPedido;
    private String obs;
    private String dtMod;
    private List<PedidoCompraItem> itens;

    public PedidoCompra() {
        this.enviado = 1;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getIdSistema() {
        return idSistema;
    }

    public void setIdSistema(String idSistema) {
        this.idSistema = idSistema;
    }

    public String getAprovadores() {
        return aprovadores;
    }

    public void setAprovadores(String aprovadores) {
        this.aprovadores = aprovadores;
    }

    public int getEnviado() {
        return enviado;
    }

    public void setEnviado(int enviado) {
        this.enviado = enviado;
    }

    public String getStatusPedido() {
        return statusPedido;
    }

    public void setStatusPedido(String statusPedido) {
        this.statusPedido = statusPedido;
    }

    public String getNomeForn() {
        return nomeForn;
    }

    public void setNomeForn(String nomeForn) {
        this.nomeForn = nomeForn;
    }

    public String getCpfCnpjForn() {
        return cpfCnpjForn;
    }

    public void setCpfCnpjForn(String cpfCnpjForn) {
        this.cpfCnpjForn = cpfCnpjForn;
    }

    public String getCondPagto() {
        return condPagto;
    }

    public void setCondPagto(String condPagto) {
        this.condPagto = condPagto;
    }

    public String getCodForn() {
        return codForn;
    }

    public void setCodForn(String codForn) {
        this.codForn = codForn;
    }

    public String getDtEmi() {
        return dtEmi;
    }

    public void setDtEmi(String dtEmi) {
        this.dtEmi = dtEmi;
    }

    public String getDtNeces() {
        return dtNeces;
    }

    public void setDtNeces(String dtNeces) {
        this.dtNeces = dtNeces;
    }

    public String getDtRej() {
        return dtRej;
    }

    public void setDtRej(String dtRej) {
        this.dtRej = dtRej;
    }

    public String getCentroCusto() {
        return centroCusto;
    }

    public void setCentroCusto(String centroCusto) {
        this.centroCusto = centroCusto;
    }

    public String getIdSolicitante() {
        return idSolicitante;
    }

    public void setIdSolicitante(String idSolicitante) {
        this.idSolicitante = idSolicitante;
    }

    public String getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(String solicitante) {
        this.solicitante = solicitante;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getMotivoRejeicao() {
        return motivoRejeicao;
    }

    public void setMotivoRejeicao(String motivoRejeicao) {
        this.motivoRejeicao = motivoRejeicao;
    }

    public float getTotalPedido() {
        return totalPedido;
    }

    public void setTotalPedido(float totalPedido) {
        this.totalPedido = totalPedido;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public String getDtMod() {
        return dtMod;
    }

    public void setDtMod(String dtMod) {
        this.dtMod = dtMod;
    }

    public List<PedidoCompraItem> getItens() {
        return itens;
    }

    public void setItens(List<PedidoCompraItem> itens) {
        this.itens = itens;
    }
}
