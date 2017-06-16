package com.jedlab.framework.db.worker;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.hibernate.jdbc.Work;

/**
 * @author Omid Pourhadi omidpourhadi [AT] fanpardaz [DOT] com
 * 
 */
public class DbWork implements Work
{

    private String sql;
    private Object[] params;
    private List<Map<String, Object>> result;

    public DbWork(String sql, Object[] params)
    {
        this.sql = sql;
        this.params = params;
    }

    @Override
    public void execute(Connection connection) throws SQLException
    {
        QueryRunner qr = new QueryRunner();
        ResultSetHandler<List<Map<String, Object>>> handler = new MapListHandler();
        result = qr.query(connection, sql, handler, params);
    }

    public List<Map<String, Object>> getResult()
    {
        return result;
    }

    
    

}
