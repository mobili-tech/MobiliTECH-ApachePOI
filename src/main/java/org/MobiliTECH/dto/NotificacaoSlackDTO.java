package org.MobiliTECH.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NotificacaoSlackDTO {
    private final String texto;

    public NotificacaoSlackDTO(String text) {
        this.texto = text;
    }

    @JsonProperty("text")
    public String getMessage() {
        return texto;
    }
}
