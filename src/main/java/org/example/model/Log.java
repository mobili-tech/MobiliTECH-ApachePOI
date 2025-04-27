package org.example.model;

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
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    public String getInformacao() {
        return informacao;
    }
    public void setInformacao(String informacao) {
        this.informacao = informacao;
    }
    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
