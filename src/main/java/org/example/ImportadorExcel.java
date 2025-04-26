package org.example;

import org.example.service.ImportadorS3;
import org.example.service.ProcessadorPlanilha;

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

        System.out.println("✅ Processamento concluído.");
    }
}
