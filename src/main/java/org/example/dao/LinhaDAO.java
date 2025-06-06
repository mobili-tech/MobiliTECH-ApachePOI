package org.example.dao;

import org.example.model.Linha;
import java.sql.*;

public class LinhaDAO {
    private Connection conn;

    public LinhaDAO(Connection conn) {
        this.conn = conn;
    }

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
                    l.setQtdViagensIda(rs.getInt("qtdViagensIda"));
                    l.setQtdViagensVolta(rs.getInt("qtdViagensVolta"));
                    return l;
                }
            }
        }
        return null;
    }

    public void insert(Linha linha) {
        String sql = "INSERT INTO linha (nome, fkEmpresa, fkGrupo, qtdViagensIda, qtdViagensVolta) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, linha.getNome());
            ps.setInt(2, linha.getFkEmpresa());
            ps.setInt(3, linha.getFkGrupo());
            ps.setInt(4, linha.getQtdViagensIda());
            ps.setInt(5, linha.getQtdViagensVolta());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    linha.setIdLinha(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir linha", e);
        }
    }
}
