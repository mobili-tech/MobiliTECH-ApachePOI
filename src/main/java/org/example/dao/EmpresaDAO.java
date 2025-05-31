package org.example.dao;

import org.example.model.Empresa;
import java.sql.*;

public class EmpresaDAO {
    private Connection conn;

    public EmpresaDAO(Connection conn) { this.conn = conn; }

    public Empresa findByRazaoSocial(String razaoSocial) throws SQLException {
        String sql = "SELECT * FROM empresa WHERE razaoSocial = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, razaoSocial);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Empresa e = new Empresa();
                    e.setIdEmpresa(rs.getInt("idEmpresa"));
                    e.setRazaoSocial(rs.getString("razaoSocial"));
                    return e;
                }
            }
        }
        return null;
    }

    public Empresa insert(Empresa empresa) throws SQLException {
        String sql = "INSERT INTO empresa (razaoSocial) VALUES (?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, empresa.getRazaoSocial());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    empresa.setIdEmpresa(rs.getInt(1));
                    return empresa;
                }
            }
        }
        throw new SQLException("Falha ao inserir empresa");
    }
}
