package org.MobiliTECH.dao;

import org.MobiliTECH.model.Empresa;

import java.sql.*;

public class EmpresaDAO implements DAO<Empresa>{
    private final Connection conn;

    public EmpresaDAO(Connection conn) {
        this.conn = conn;
    }

    public Empresa findByNomeFantasia(String nomeFantasia) throws SQLException {
        String sql = "SELECT * FROM empresa WHERE nomeFantasia = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nomeFantasia);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Empresa e = new Empresa();
                    e.setIdEmpresa(rs.getInt("idEmpresa"));
                    e.setNomeFantasia(rs.getString("nomeFantasia"));
                    return e;
                }
            }
        }
        return null;
    }

    @Override
    public void insert(Empresa empresa) {
        String sql = "INSERT INTO empresa (nomeFantasia) VALUES (?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, empresa.getNomeFantasia());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    empresa.setIdEmpresa(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir empresa", e);
        }
    }
}
