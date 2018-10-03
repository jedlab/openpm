package com.jedlab.framework.spring.rest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jedlab.framework.util.RegexUtil;

/**
 * @author : Omid Pourhadi omidpourhadi [AT] gmail [DOT] com
 * @version 0.2
 */
public class QueryWhereParser
{

    private static Logger log = LoggerFactory.getLogger(QueryWhereParser.class);

    public static final QueryWhereParser EMPTY = new QueryWhereParser();

    public static final String AND = "AND";
    public static final String OR = "OR";

    private String match = AND;

    private List<FilterProperty> filterProperties = new ArrayList<>();

    public static final String OPERATOR_EXPRESIION = "^\\$[a-z]{2}$";
    public static final String OPERATOR_EXPRESIION_3 = "^\\$[a-z]{3}$";

    public static final String GT = "$gt"; // great than
    public static final String GTE = "$gte"; // greate than equal
    public static final String LT = "$lt"; // less than
    public static final String LTE = "$lte"; // less than equal
    public static final String LK = "$lk"; // like
    public static final String EQ = "$eq"; // equal
    public static final String NEQ = "$neq"; // not equal
    public static final String IN = "$in"; //
    public static final String BW = "$bw";// begins with
    public static final String EW = "$ew"; // ends with
    public static final String NE = "$ne"; // null
    public static final String NN = "$nn"; // not null

    public QueryWhereParser()
    {
    }

    public QueryWhereParser(String content)
    {
        buildParameters(content);
    }

    public void buildParameters(String content)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(content);
            parseJSON(jsonObject, 0);
        }
        catch (Exception e)
        {
            log.error("could not parse query", e);
        }

    }

    private void parseFilter(String key, JSONArray jsonArr, int index) throws JSONException
    {
        for (int i = 0; i < jsonArr.length(); i++)
        {
            Object object = jsonArr.get(i);
            if (object instanceof JSONObject)
            {
                JSONObject obj = (JSONObject) object;
                parseFilter(key, obj, i);
            }
        }
    }

    private void parseFilter(String key, JSONObject jsonObject, int index) throws JSONException
    {
        FilterProperty fp = new FilterProperty();
        // jsonobject
        Object op = jsonObject.keys().next();
        fp.setOperator(String.valueOf(op));
        Object objectValue = jsonObject.get(String.valueOf(op));
        fp.setValue(objectValue);
        fp.setPropertyName(key);
        filterProperties.add(fp);
    }

    private void parseJSON(JSONObject jsonObject, int index)
    {
        log.debug("parsing " + jsonObject.toString() + " index : " + index);
        try
        {
            Iterator<?> keys = jsonObject.keys();
            while (keys.hasNext())
            {
                String key = (String) keys.next();
                Object value = jsonObject.get(key);
                if (value instanceof JSONObject)
                {
                    parseFilter(key, (JSONObject) value, index++);
                }
                else if (value instanceof JSONArray)
                {
                    parseFilter(key, (JSONArray) value, index++);
                }
            }
        }
        catch (Exception e)
        {
            log.error("could not parse query", e);
        }
    }

    public String getMatch()
    {
        return match;
    }

    public void setMatch(String match)
    {
        this.match = match;
    }

    public static class FilterProperty
    {
        String operator;
        String propertyName;
        Object value;

        public FilterProperty()
        {
        }

        public String getOperator()
        {
            return operator;
        }

        public void setOperator(String operator)
        {
            this.operator = operator;
        }

        public String getPropertyName()
        {
            return propertyName;
        }

        public void setPropertyName(String propertyName)
        {
            this.propertyName = propertyName;
        }

        public Object getValue()
        {
            return value;
        }

        public void setValue(Object value)
        {
            this.value = value;
        }

    }

    public List<FilterProperty> getFilterProperties()
    {
        return filterProperties;
    }

}