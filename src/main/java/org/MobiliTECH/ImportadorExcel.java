package org.MobiliTECH;

import org.MobiliTECH.service.ImportadorS3;
import org.MobiliTECH.service.ProcessadorPlanilha;
import org.MobiliTECH.service.TransporteService;

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
        } catch (Exception ignored) {
        }

        System.out.println("✅ Processamento concluído.");
    }
}
