package com.omidp.oframework.test;

import java.io.IOException;
import java.net.URLDecoder;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.Test;

import com.jedlab.framework.spring.rest.QueryWhereParser;

public class JsonQueryBuilderTest
{

    @Test
    public void testParseSingleJson() throws IOException
    {
        String filter = "{\"parent.id\":{\"$eq\":2}}";
        QueryWhereParser qb = filter != null ? new QueryWhereParser(URLDecoder.decode(filter, "UTF-8")) : QueryWhereParser.EMPTY;
        System.out.println(qb.getFilterProperties().size());
    }
    
    @Test
    public void testSingleJson() throws IOException, JSONException
    {
        String filter = "{\"id\":[{\"$gte\":1},{\"$lte\": 10}]} ";
//        JSONObject js = new JSONObject(filter);
//        String filter = "{\"trxReceiveDate\":{\"$gte\":1542745800},\"trxReceiveDate\":{\"$lte\":1537648200}}";
        QueryWhereParser qb = filter != null ? new QueryWhereParser(URLDecoder.decode(filter, "UTF-8")) : QueryWhereParser.EMPTY;
        System.out.println(qb.getFilterProperties().size());
//        qb.getFilterProperties().forEach(item->{
//            System.out.println(item.getPropertyName());
//            System.out.println(item.getOperator());
//            System.out.println(item.getValue());
//        });
    }
    
    @Test
    public void testParseMultipleJson() throws IOException
    {
        String filter = "{\"parent.id\":{\"$eq\":1}, \"name\":{\"$lk\": \"Home\"}} ";
        QueryWhereParser qb = filter != null ? new QueryWhereParser(URLDecoder.decode(filter, "UTF-8")) : QueryWhereParser.EMPTY;
        System.out.println(qb.getFilterProperties().size());
    }

}
