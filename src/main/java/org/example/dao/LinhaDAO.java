package org.example.dao;

import org.example.model.Linha;
import java.sql.*;

public class LinhaDAO {
    private Connection conn;

    public LinhaDAO(Connection conn) { this.conn = conn; }

    public Linha findByNomeEmpresaGrupo(String nome, int fkEmpresa, int fkGrupo) throws SQLException {
        String sql = "SELECT * FROM linha WHERE nome = ? AND fkEmpresa = ? AND fkGrupo = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nome);
            ps.setInt(2, fkEmpresa);
            ps.setInt(3, fkGrupo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Linha l = new Linha();
                    l.setIdLinha(rs.getInt("idLinha"));
                    l.setNome(rs.getString("nome"));
                    l.setFkEmpresa(rs.getInt("fkEmpresa"));
                    l.setFkGrupo(rs.getInt("fkGrupo"));
                    return l;
                }
            }
        }
        return null;
    }

    public Linha insert(Linha linha) throws SQLException {
        String sql = "INSERT INTO linha (nome, fkEmpresa, fkGrupo) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, linha.getNome());
            ps.setInt(2, linha.getFkEmpresa());
            ps.setInt(3, linha.getFkGrupo());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    linha.setIdLinha(rs.getInt(1));
                    return linha;
                }
            }
        }
        throw new SQLException("Falha ao inserir linha");
    }
}
