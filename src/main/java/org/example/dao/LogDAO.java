package org.example.dao;

import org.example.model.Log;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class LogDAO implements DAO<Log> {

    private static final String jdbcUrl = System.getenv("DB_HOST");
    private static final String usuario = System.getenv("DB_USER");
    private static final String senha = System.getenv("DB_PSWD");

    @Override
    public void inserir(Log log) {
        String sql = "INSERT INTO log (tipo, informacao, descricao) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(jdbcUrl, usuario, senha);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, log.getTipo());
            stmt.setString(2, log.getInformacao());
            stmt.setString(3, log.getDescricao());

            stmt.executeUpdate();
            System.out.println("✅ Log inserido com sucesso!");

        } catch (Exception e) {
            System.err.println("❌ Erro ao inserir log: " + e.getMessage());
        }
    }
}
