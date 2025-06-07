package org.example.dao;

import org.example.model.Registro;
import java.sql.*;

public class RegistroDAO implements ReturnableDAO<Registro>{
    private Connection conn;

    public RegistroDAO(Connection conn) { this.conn = conn; }

    @Override
    public Registro insert(Registro registro) throws SQLException {
        String sql = "INSERT INTO registro (fkLinha, fkEmpresa, dtRegistro, qtdPassageiros) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, registro.getFkLinha());
            ps.setInt(2, registro.getFkEmpresa());
            ps.setDate(3, registro.getDtRegistro());
            ps.setInt(4, registro.getQtdPassageiros());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    registro.setIdRegistro(rs.getInt(1));
                    return registro;
                }
            }
        }
        throw new SQLException("Falha ao inserir registro");
    }
}
