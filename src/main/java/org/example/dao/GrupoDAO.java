package org.example.dao;

import org.example.model.Grupo;
import java.sql.*;

public class GrupoDAO {
    private Connection conn;

    public GrupoDAO(Connection conn) { this.conn = conn; }

    public Grupo findByTipo(String tipo) throws SQLException {
        String sql = "SELECT * FROM grupo WHERE tipo = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tipo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Grupo g = new Grupo();
                    g.setIdGrupo(rs.getInt("idGrupo"));
                    g.setTipo(rs.getString("tipo"));
                    return g;
                }
            }
        }
        return null;
    }

    public Grupo insert(Grupo grupo) throws SQLException {
        String sql = "INSERT INTO grupo (tipo) VALUES (?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, grupo.getTipo());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    grupo.setIdGrupo(rs.getInt(1));
                    return grupo;
                }
            }
        }
        throw new SQLException("Falha ao inserir grupo");
    }
}
