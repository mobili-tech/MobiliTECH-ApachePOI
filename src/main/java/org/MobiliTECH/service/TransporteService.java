package org.MobiliTECH.service;

import org.MobiliTECH.dao.EmpresaDAO;
import org.MobiliTECH.dao.GrupoDAO;
import org.MobiliTECH.dao.LinhaDAO;
import org.MobiliTECH.dao.RegistroDAO;
import org.MobiliTECH.model.Empresa;
import org.MobiliTECH.model.Grupo;
import org.MobiliTECH.model.Linha;
import org.MobiliTECH.model.Registro;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class TransporteService {

    private final Connection conn;
    private final EmpresaDAO empresaDAO;
    private final GrupoDAO grupoDAO;
    private final LinhaDAO linhaDAO;
    private final RegistroDAO registroDAO;

    private final Map<String, Empresa> empresaCache = new HashMap<>();
    private final Map<String, Grupo> grupoCache = new HashMap<>();
    private final Map<String, Linha> linhaCache = new HashMap<>();

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
                int qtdViagensIda = rs.getInt("partidas_ponto_inicial");
                int qtdViagensVolta = rs.getInt("partidas_ponto_final");


                Empresa empresa = empresaCache.get(empresaNome);
                if (empresa == null) {
                    empresa = empresaDAO.findByNomeFantasia(empresaNome);
                    if (empresa == null) {
                        empresa = new Empresa();
                        empresa.setNomeFantasia(empresaNome);
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
                        linha.setQtdViagensIda(qtdViagensIda);
                        linha.setQtdViagensVolta(qtdViagensVolta);
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
            System.out.println("✅ Migração concluída com sucesso.");
            truncateTransporteTable();

        } catch (SQLException e) {
            conn.rollback();
            LogService.registrarErro("Erro na movimentacao de Registros","Erro ao movimentar registros");
            throw e;
        } finally {
            conn.close();
        }
    }

    private void truncateTransporteTable() {
        String truncateSql = "TRUNCATE TABLE transporte";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(truncateSql);
            LogService.registrarInfo("TRUNCATE Table", "Tabela 'transporte' truncada com sucesso.");
            System.out.println("✅ Tabela 'transporte' truncada com sucesso.");
        } catch (SQLException e) {
            LogService.registrarErro("Erro ao truncar tabela", "Erro ao executar TRUNCATE na tabela 'transporte': " + e.getMessage());
        }
    }
}
