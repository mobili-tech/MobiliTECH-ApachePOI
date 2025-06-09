package org.MobiliTECH.model;
import java.time.LocalDateTime;

public class NotificacaoSlack {
        private String descricao;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public NotificacaoSlack(String descricao) {
        this.descricao = descricao;
    }
}
