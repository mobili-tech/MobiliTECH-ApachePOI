package org.example;

import org.example.service.ImportadorS3;
import org.example.service.ProcessadorPlanilha;
import org.example.service.TransporteService;

import java.util.List;

public class ImportadorExcel {
    public static void main(String[] args) {
        ImportadorS3 importadorS3 = new ImportadorS3();
        List<String> arquivos = importadorS3.listarArquivos();

        if (arquivos.isEmpty()) {
            System.out.println("Nenhum arquivo novo encontrado.");
            return;
        }

        ProcessadorPlanilha processador = new ProcessadorPlanilha();

        for (String arquivo : arquivos) {
            processador.processar(arquivo);
        }

        try{
            TransporteService service = new TransporteService();
            service.processarTransporte();
            System.out.println("Migração concluída com sucesso.");
        } catch (Exception e) {
        }

        System.out.println("✅ Processamento concluído.");
    }
}
