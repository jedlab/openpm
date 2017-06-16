package com.jedlab.framework.db.worker;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.hibernate.jdbc.Work;

/**
 * @author Omid Pourhadi omidpourhadi [AT] fanpardaz [DOT] com
 * 
 */
public class DbObjectWork<T> implements Work
{

    private String sql;
    private Object[] params;
    private Class<T> type;
    private T instance;

    public DbObjectWork(String sql, Object[] params, Class<T> type)
    {
        this.sql = sql;
        this.params = params;
        this.type = type;
    }

    @Override
    public void execute(Connection connection) throws SQLException
    {
        QueryRunner qr = new QueryRunner();
        ResultSetHandler<T> handler = new BeanHandler<T>(type);
        instance = qr.query(connection, sql, handler, params);
    }

    public T getInstance()
    {
        return instance;
    }

}
