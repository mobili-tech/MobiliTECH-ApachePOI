package org.example.dao;

import org.example.model.RegistroTransporte;
import org.example.service.LogService;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransporteDAO implements DAO<List<RegistroTransporte>> {

    private final String jdbcUrl = System.getenv("DB_HOST");
    private final String usuario = System.getenv("DB_USER");
    private final String senha = System.getenv("DB_PSWD");

    @Override
    public void inserir(List<RegistroTransporte> registros) {
        String sql = """
            INSERT INTO transporte (
                data, grupo, lote, empresa, linha, passageiros_total,
                partidas_ponto_inicial, partidas_ponto_final
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
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
                stmt.setInt(6, r.passageirosTotal);
                stmt.setInt(7, r.partidasPontoInicial);
                stmt.setInt(8, r.partidasPontoFinal);

                stmt.addBatch();
            }

            stmt.executeBatch();
            conn.commit();
            LogService.registrarInfo("✅Registros de transporte movidos com sucesso!", "");

        } catch (Exception e) {
           LogService.registrarErro("❌ Erro ao mover registros no banco","Erro ao salvar registros no banco: " + e.getMessage());
        }
    }
}
