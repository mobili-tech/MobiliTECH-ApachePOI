package org.example.dao;

import org.example.model.RegistroTransporte;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;

public class TransporteDAO {

    private final String jdbcUrl = System.getenv("DB_HOST");
    private final String usuario = System.getenv("DB_USER");
    private final String senha = System.getenv("DB_PSWD");

    public void salvarRegistros(List<RegistroTransporte> registros) {
        String sql = """
            INSERT INTO transporte (
                data, grupo, lote, empresa, linha,
                passageiros_dinheiro, passageiros_comum_vt, passageiros_comum_m,
                passageiros_estudante, passageiros_estudante_mensal, passageiros_vt_mensal,
                passageiros_pagantes, passageiros_integracao, passageiros_gratuidade, passageiros_total,
                partidas_ponto_inicial, partidas_ponto_final
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DriverManager.getConnection(jdbcUrl, usuario, senha);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            for (RegistroTransporte r : registros) {
                stmt.setDate(1, Date.valueOf(r.data));
                stmt.setString(2, r.grupo);
                stmt.setString(3, r.lote);
                stmt.setString(4, r.empresa);
                stmt.setString(5, r.linha);
                stmt.setInt(6, r.passageirosDinheiro);
                stmt.setInt(7, r.passageirosComumVT);
                stmt.setInt(8, r.passageirosComumM);
                stmt.setInt(9, r.passageirosEstudante);
                stmt.setInt(10, r.passageirosEstudanteMensal);
                stmt.setInt(11, r.passageirosVTMensal);
                stmt.setInt(12, r.passageirosPagantes);
                stmt.setInt(13, r.passageirosIntegracao);
                stmt.setInt(14, r.passageirosGratuidade);
                stmt.setInt(15, r.passageirosTotal);
                stmt.setInt(16, r.partidasPontoInicial);
                stmt.setInt(17, r.partidasPontoFinal);
                stmt.addBatch();
            }

            stmt.executeBatch();
            conn.commit();

        } catch (Exception e) {
            System.err.println("❌ Erro ao salvar registros no banco:");
            e.printStackTrace();
        }
    }
}

