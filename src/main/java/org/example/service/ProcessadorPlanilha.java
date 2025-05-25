package org.example.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.dao.TransporteDAO;
import org.example.model.RegistroTransporte;

import java.io.InputStream;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProcessadorPlanilha {

    private final ImportadorS3 importadorS3 = new ImportadorS3();
    private final TransporteDAO dao = new TransporteDAO();
    private final LogService log = new LogService();

    public void processar(String caminhoArquivoS3) {
        try (InputStream input = importadorS3.baixarArquivo(caminhoArquivoS3);
             Workbook workbook = new XSSFWorkbook(input)) {

            Sheet sheet = workbook.getSheetAt(0);
            List<RegistroTransporte> registros = new ArrayList<>();

            for (int i = 3; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                RegistroTransporte r = new RegistroTransporte();

                // Preenchendo campos
                Date dataPlanilha = getDate(row, 0);
                if (dataPlanilha == null) continue;

                r.data = dataPlanilha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                r.grupo = getString(row, 1);
                r.lote = getString(row, 2);
                r.empresa = getString(row, 3);
                r.linha = getString(row, 4);
                r.passageirosDinheiro = getInt(row, 5);
                r.passageirosComumVT = getInt(row, 6);
                r.passageirosComumM = getInt(row, 7);
                r.passageirosEstudante = getInt(row, 8);
                r.passageirosEstudanteMensal = getInt(row, 9);
                r.passageirosVTMensal = getInt(row, 10);
                r.passageirosPagantes = getInt(row, 11);
                r.passageirosIntegracao = getInt(row, 12);
                r.passageirosGratuidade = getInt(row, 13);
                r.passageirosTotal = getInt(row, 14);
                r.partidasPontoInicial = getInt(row, 15);
                r.partidasPontoFinal = getInt(row, 16);

                registros.add(r);
            }

            dao.inserir(registros);
            importadorS3.moverParaConcluido(caminhoArquivoS3);

            log.registrarInfo("Inserção de Registros",registros.size() + " registros inseridos de " + caminhoArquivoS3);
            System.out.println("✅ " + registros.size() + " registros inseridos de " + caminhoArquivoS3);
        } catch (Exception e) {
            log.registrarErro("Erro ao processar arquivo", "Erro ao processar arquivo: " + caminhoArquivoS3);
            System.err.println("❌ Erro ao processar arquivo: " + caminhoArquivoS3);
            e.printStackTrace();
        }
    }

    // Métodos auxiliares
    private java.util.Date getDate(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex);
        if (cell != null && cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue();
        }
        return null;
    }

    private String getString(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex);
        return cell != null ? cell.toString().trim() : "";
    }

    private int getInt(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex);
        if (cell != null && cell.getCellType() == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        }
        return 0;
    }
}