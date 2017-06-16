package com.jedlab.framework.db.worker;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.hibernate.jdbc.Work;

/**
 * @author Omid Pourhadi
 *
 */
public class DbScalarWork<T> implements Work
{

    private String sql;
    private T result;
    private Object[] params;

    public DbScalarWork(String sql, Object[] params)
    {
        this.sql = sql;
        this.params = params;
    }

    @Override
    public void execute(Connection connection) throws SQLException
    {
        QueryRunner qr = new QueryRunner();
        ScalarHandler<T> handler = new ScalarHandler<T>();
        result = qr.query(connection, sql, params, handler);
    }

    public T getResult()
    {
        return result;
    }

}
