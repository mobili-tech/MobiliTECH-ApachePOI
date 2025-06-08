package org.MobiliTECH.model;

public class Linha {
    private int idLinha;
    private String nome;
    private int fkEmpresa;
    private int fkGrupo;
    private int qtdViagensIda;
    private int qtdViagensVolta;

    public int getIdLinha() { return idLinha; }
    public void setIdLinha(int idLinha) { this.idLinha = idLinha; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getFkEmpresa() { return fkEmpresa; }
    public void setFkEmpresa(int fkEmpresa) { this.fkEmpresa = fkEmpresa; }

    public int getFkGrupo() { return fkGrupo; }
    public void setFkGrupo(int fkGrupo) { this.fkGrupo = fkGrupo; }

    public int getQtdViagensIda() { return qtdViagensIda; }
    public void setQtdViagensIda(int qtdViagensIda) { this.qtdViagensIda = qtdViagensIda; }

    public int getQtdViagensVolta() { return qtdViagensVolta; }
    public void setQtdViagensVolta(int qtdViagensVolta) { this.qtdViagensVolta = qtdViagensVolta; }
}
