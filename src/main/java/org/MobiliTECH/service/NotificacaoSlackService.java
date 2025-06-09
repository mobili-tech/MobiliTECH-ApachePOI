package org.MobiliTECH.service;

import org.MobiliTECH.dao.NotificacaoSlackDAO;
import org.MobiliTECH.dto.NotificacaoSlackDTO;
import org.MobiliTECH.model.NotificacaoSlack;
import org.MobiliTECH.util.NotificacaoSlackUtil;

public class NotificacaoSlackService {
    private final NotificacaoSlackDAO notificacaoSlackDAO;
    private final NotificacaoSlackUtil notificacaoSlackUtil;

    public NotificacaoSlackService() {
        this.notificacaoSlackUtil = new NotificacaoSlackUtil();
        this.notificacaoSlackDAO = new NotificacaoSlackDAO();
    }

    public void enviarNotificacoes(String conteudo) {
        NotificacaoSlackDTO dto = new NotificacaoSlackDTO(conteudo);

        notificacaoSlackUtil.enviarMensagem(dto);

        NotificacaoSlack notificacaoSlack = new NotificacaoSlack(conteudo);

        this.notificacaoSlackDAO.insert(notificacaoSlack);
    }
}
