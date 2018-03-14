package com.omidp.oframework.test.model;

import java.io.IOException;

import org.junit.Test;

import com.jedlab.framework.spring.rest.QueryWhereParser;

public class JsonQueryBuilderTest
{

    @Test
    public void testParseSingleJson() throws IOException
    {
        String json = "{\"parent.id\":{\"$eq\":2}}";
        QueryWhereParser q = new QueryWhereParser(json);
        q.getFilterProperties().forEach(i->{
            System.out.println(i.getValue());
        });
    }

    @Test
    public void testParseMultipleJson() throws IOException
    {
        String json = "{\"parent.id\":{\"$eq\":1}, \"name\":{\"$lk\": \"Home\"}} ";
        QueryWhereParser q = new QueryWhereParser(json);
    }

    @Test
    public void testParseSingleObject() throws IOException
    {
        String json = "{\"parent.id\":{\"$eq\":1}} ";
        QueryWhereParser q = new QueryWhereParser(json);
    }

    @Test
    public void testParseJson() throws IOException
    {
        String json = "{\"id\":{\"$eq\":1}, \"name\":{\"$lk\": \"Home\"}} ";
        QueryWhereParser q = new QueryWhereParser(json);
    }

    @Test
    public void testMultiPropertiesJson() throws IOException
    {
        String json = "{\"id\":{\"$gt\":1}, \"id\":{\"$lt\": 10}} ";
        QueryWhereParser q = new QueryWhereParser(json);
    }

}
