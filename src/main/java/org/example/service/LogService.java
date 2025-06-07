package org.example.service;

import org.example.dao.LogDAO;
import org.example.model.Log;

public class LogService {

    public static void registrarInfo(String informacao, String descricao) {
        Log log = new Log("INFO", informacao, descricao);
        new LogDAO().insert(log);
    }

    public static void registrarErro(String informacao, String descricao) {
        Log log = new Log("ERROR", informacao, descricao);
        new LogDAO().insert(log);
    }
}
