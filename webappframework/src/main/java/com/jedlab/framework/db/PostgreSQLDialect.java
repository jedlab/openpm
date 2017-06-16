package com.jedlab.framework.db;

import java.sql.Types;

import org.hibernate.Hibernate;
import org.hibernate.dialect.function.StandardSQLFunction;

/**
 * @author Omid Pourhadi
 *
 */
public class PostgreSQLDialect extends org.hibernate.dialect.PostgreSQLDialect
{

    public PostgreSQLDialect()
    {
        super();
        registerFunction("replace", new StandardSQLFunction("replace", Hibernate.STRING));
        registerHibernateType(Types.ARRAY, "array"); 
        registerFunction( "string_agg", new StandardSQLFunction("string_agg", Hibernate.STRING) );
        registerFunction( "generate_series", new StandardSQLFunction("generate_series") );
        registerFunction( "preparestring", new StandardSQLFunction("preparestring", Hibernate.STRING) );
    }
    
    @Override
    public boolean supportsTupleDistinctCounts()
    {
        return true;
    }

}
