package org.example.service;

import org.example.dao.LogDAO;
import org.example.model.Log;

public class LogService {

    private LogDAO logDAO;

    public LogService() {
        logDAO = new LogDAO();
    }

    public void registrarInfo(String informacao, String descricao) {
        Log log = new Log("INFO", informacao, descricao);
        logDAO.inserirLog(log);
    }

    public void registrarErro(String informacao, String descricao) {
        Log log = new Log("ERROR", informacao, descricao);
        logDAO.inserirLog(log);
    }
}
