package com.jedlab.framework.db.worker;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jedlab.framework.reflections.ReflectionUtil;
import com.jedlab.framework.util.CollectionUtil;

/**
 * @author omidp
 *
 */
public class DBUtil
{

    private final static Logger LOGGER = LoggerFactory.getLogger(DBUtil.class);

    Session session;

    public DBUtil(Session session)
    {
        if (session == null)
            throw new IllegalArgumentException("session can not be null");
        this.session = session;
    }

    private Logger getLog()
    {
        return LOGGER;
    }

    private Session getSession()
    {
        return this.session;
    }
    
    public boolean isDatabaseOracle()
    {
        boolean isOracle = false;
        SessionFactory sessionFactory = getSession().getSessionFactory();
        try
        {
            Object dialect = PropertyUtils.getProperty(sessionFactory, "dialect");
            if (dialect.toString().contains("Oracle"))
            {
                isOracle = true;
            }
        }
        catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
        {
            // DO NOTHING
        }
        return isOracle;
    }
    
    public boolean isDatabaseMySQL()
    {
        boolean isMysql = false;
        SessionFactory sessionFactory = getSession().getSessionFactory();
        try
        {
            Object dialect = PropertyUtils.getProperty(sessionFactory, "dialect");
            if (dialect.toString().contains("MySQL"))
            {
                isMysql = true;
            }
        }
        catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
        {
            // DO NOTHING
        }
        return isMysql;
    }

    public <T> T executeScalar(String sql, Class<T> type, Object... params)
    {
        DbScalarWork<T> sw = new DbScalarWork<T>(sql, params);
        getSession().doWork(sw);
        getLog().info("executing : {0} ", sql);
        return sw.getResult();
    }

    public List<Map<String, Object>> executeQuery(String sql, Object... params)
    {
        DbWork dbWork = new DbWork(sql, params);
        getSession().doWork(dbWork);
        getLog().info("executing : {0} ", sql);
        return dbWork.getResult();
    }

    public <T> T executeQuery(Class<T> clz, String sql, Object... params)
    {
        DbObjectWork<T> dbWork = new DbObjectWork<T>(sql, params, clz);
        getSession().doWork(dbWork);
        getLog().info("executing : {0} ", sql);
        return dbWork.getInstance();
    }

    public <E> List<E> executeQueryList(Class<E> clz, String sql, Object... params)
    {
        DbListWork<E> dbWork = new DbListWork<E>(sql, params, clz);
        getSession().doWork(dbWork);
        getLog().info("executing : {0} ", sql);
        return dbWork.getInstance();
    }

    public <E> List<E> executeQueryListSingleColumnPrimitive(Class<E> clz, String sql, Object... params)
    {
        List<E> arr = new ArrayList<E>();
        List<Map<String, Object>> query = executeQuery(sql, params);
        if (CollectionUtil.isEmpty(query))
            return arr;
        String key = query.iterator().next().keySet().iterator().next();
        for (Map<String, Object> map : query)
        {
            Object val = map.get(key);
            if (val != null)
                arr.add(ReflectionUtil.cast(val, clz));
        }
        return arr;
    }

    public void executeUpdate(String sql, final Object... params)
    {
        getLog().info("executing : {0} ", sql);
        DbWorker w = new DbWorker(sql) {

            @Override
            public void exec(Connection con, String q) throws SQLException
            {
                QueryRunner qr = new QueryRunner();
                qr.update(con, q, params);
            }
        };
        getSession().doWork(w);
    }

}
