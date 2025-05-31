package org.example.model;

import java.sql.Date;

public class Registro {
    private int idRegistro;
    private int fkLinha;
    private int fkEmpresa;
    private Date dtRegistro;
    private int qtdPassageiros;

    public int getIdRegistro() { return idRegistro; }
    public void setIdRegistro(int idRegistro) { this.idRegistro = idRegistro; }

    public int getFkLinha() { return fkLinha; }
    public void setFkLinha(int fkLinha) { this.fkLinha = fkLinha; }

    public int getFkEmpresa() { return fkEmpresa; }
    public void setFkEmpresa(int fkEmpresa) { this.fkEmpresa = fkEmpresa; }

    public Date getDtRegistro() { return dtRegistro; }
    public void setDtRegistro(Date dtRegistro) { this.dtRegistro = dtRegistro; }

    public int getQtdPassageiros() { return qtdPassageiros; }
    public void setQtdPassageiros(int qtdPassageiros) { this.qtdPassageiros = qtdPassageiros; }
}
