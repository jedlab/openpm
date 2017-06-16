package com.jedlab.framework.db.worker;

import java.sql.Connection;
import java.sql.SQLException;

import org.hibernate.jdbc.Work;

public abstract class DbWorker implements Work
{

    private String query;

    public DbWorker(String query)
    {
        this.query = query;
    }

    @Override
    public void execute(Connection con) throws SQLException
    {
        exec(con, query);
    }
    
    public abstract void exec(Connection con, String q) throws SQLException;

}
