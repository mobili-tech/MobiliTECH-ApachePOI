package org.MobiliTECH.model;

public class Log {
    private String tipo;
    private String informacao;
    private String descricao;

    public Log(String tipo, String informacao, String descricao) {
        this.tipo = tipo;
        this.informacao = informacao;
        this.descricao = descricao;
    }
    public String getTipo() {
        return tipo;
    }
    public String getInformacao() {
        return informacao;
    }
    public String getDescricao() {
        return descricao;
    }
}
