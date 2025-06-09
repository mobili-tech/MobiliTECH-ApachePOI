package org.MobiliTECH.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.MobiliTECH.dto.NotificacaoSlackDTO;
import org.MobiliTECH.service.LogService;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class NotificacaoSlackUtil {
    private final String slackWebhook = System.getenv("SLACK_WEBHOOK");
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final HttpClient client = HttpClient.newHttpClient();

    public void enviarMensagem(NotificacaoSlackDTO dto){
        try{
            String json = mapper.writeValueAsString(dto);

            HttpRequest request = HttpRequest
                    .newBuilder()
                    .uri(URI.create(slackWebhook))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> respostaEnvio = client.send(request, HttpResponse.BodyHandlers.ofString());

            LogService.registrarInfo("Envio de notificacao Slack", dto.getMessage());

        } catch (Exception e) {
            LogService.registrarErro("Erro ao enviar notificacao Slack",e.getMessage());
        }
    }

}