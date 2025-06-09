package org.MobiliTECH;

import org.MobiliTECH.service.ImportadorS3;
import org.MobiliTECH.service.NotificacaoSlackService;
import org.MobiliTECH.service.ProcessadorPlanilha;
import org.MobiliTECH.service.TransporteService;

import java.util.List;

public class ImportadorExcel {
    public static void main(String[] args) {
        ImportadorS3 importadorS3 = new ImportadorS3();
        NotificacaoSlackService notificacaoSlackService = new NotificacaoSlackService();
        List<String> arquivos = importadorS3.listarArquivos();

        if (arquivos.isEmpty()) {
            notificacaoSlackService.enviarNotificacoes("Nenhum arquivo novo encontrado para o processamento.");
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
        notificacaoSlackService.enviarNotificacoes("✅ Processamento de arquivos concluído.");
        System.out.println("✅ Processamento concluído.");
    }
}
