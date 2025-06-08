package org.MobiliTECH.dao;

import java.sql.SQLException;

public interface ReturnableDAO <T>{
    T insert(T entidade) throws SQLException;
}
