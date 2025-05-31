package org.example.service;

import org.example.dao.EmpresaDAO;
import org.example.dao.GrupoDAO;
import org.example.dao.LinhaDAO;
import org.example.dao.RegistroDAO;
import org.example.model.Empresa;
import org.example.model.Grupo;
import org.example.model.Linha;
import org.example.model.Registro;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class TransporteService {

    private Connection conn;
    private EmpresaDAO empresaDAO;
    private GrupoDAO grupoDAO;
    private LinhaDAO linhaDAO;
    private RegistroDAO registroDAO;

    private Map<String, Empresa> empresaCache = new HashMap<>();
    private Map<String, Grupo> grupoCache = new HashMap<>();
    private Map<String, Linha> linhaCache = new HashMap<>();

    public TransporteService() throws SQLException {
        String jdbcUrl = System.getenv("DB_HOST");
        String usuario = System.getenv("DB_USER");
        String senha = System.getenv("DB_PSWD");

        this.conn = DriverManager.getConnection(jdbcUrl, usuario, senha);
        this.conn.setAutoCommit(false);

        empresaDAO = new EmpresaDAO(conn);
        grupoDAO = new GrupoDAO(conn);
        linhaDAO = new LinhaDAO(conn);
        registroDAO = new RegistroDAO(conn);
    }

    public void processarTransporte() throws SQLException {
        String sql = "SELECT * FROM transporte";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String empresaNome = rs.getString("empresa");
                String grupoTipo = rs.getString("grupo");
                String linhaNome = rs.getString("linha");
                Date data = rs.getDate("data");
                int passageirosTotal = rs.getInt("passageiros_total");

                Empresa empresa = empresaCache.get(empresaNome);
                if (empresa == null) {
                    empresa = empresaDAO.findByRazaoSocial(empresaNome);
                    if (empresa == null) {
                        empresa = new Empresa();
                        empresa.setRazaoSocial(empresaNome);
                        empresaDAO.insert(empresa);
                    }
                    empresaCache.put(empresaNome, empresa);
                }

                Grupo grupo = grupoCache.get(grupoTipo);
                if (grupo == null) {
                    grupo = grupoDAO.findByTipo(grupoTipo);
                    if (grupo == null) {
                        grupo = new Grupo();
                        grupo.setTipo(grupoTipo);
                        grupoDAO.insert(grupo);
                    }
                    grupoCache.put(grupoTipo, grupo);
                }

                String linhaKey = linhaNome + "_" + empresa.getIdEmpresa() + "_" + grupo.getIdGrupo();
                Linha linha = linhaCache.get(linhaKey);
                if (linha == null) {
                    linha = linhaDAO.findByNomeEmpresaGrupo(linhaNome, empresa.getIdEmpresa(), grupo.getIdGrupo());
                    if (linha == null) {
                        linha = new Linha();
                        linha.setNome(linhaNome);
                        linha.setFkEmpresa(empresa.getIdEmpresa());
                        linha.setFkGrupo(grupo.getIdGrupo());
                        linhaDAO.insert(linha);
                    }
                    linhaCache.put(linhaKey, linha);
                }

                Registro registro = new Registro();
                registro.setFkEmpresa(empresa.getIdEmpresa());
                registro.setFkLinha(linha.getIdLinha());
                registro.setDtRegistro(data);
                registro.setQtdPassageiros(passageirosTotal);

                registroDAO.insert(registro);
            }

            conn.commit();
            LogService.registrarInfo("Movimentacao de Registros","Registros movimentados com sucesso");
        } catch (SQLException e) {
            conn.rollback();
            LogService.registrarErro("Erro na movimentacao de Registros","Erro ao movimentar registros");
            throw e;
        } finally {
            conn.close();
        }
    }
}
