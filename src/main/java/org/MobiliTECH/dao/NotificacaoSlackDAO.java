package org.MobiliTECH.dao;

import org.MobiliTECH.model.NotificacaoSlack;

import java.sql.*;

public class NotificacaoSlackDAO implements DAO<NotificacaoSlack>{
    private final String jdbcUrl = System.getenv("DB_HOST");
    private final String usuario = System.getenv("DB_USER");
    private final String senha = System.getenv("DB_PSWD");

    @Override
    public void insert(NotificacaoSlack notificacaoSlack) {
        String sql = "INSERT INTO notificacoesSlack (descricao) VALUES (?)";
        try (Connection conn = DriverManager.getConnection(jdbcUrl, usuario, senha);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, notificacaoSlack.getDescricao());
                stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
