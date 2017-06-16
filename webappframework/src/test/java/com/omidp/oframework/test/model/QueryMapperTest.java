package com.omidp.oframework.test.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.jedlab.framework.db.QueryMapper;

public class QueryMapperTest
{

    List<Map<String, Object>> result;

    @Before
    public void setUp()
    {
        result = new ArrayList<Map<String, Object>>();
        Map<String, Object> item = new HashMap<String, Object>();
        item.put("user_name", "vata2999");
        item.put("name", "Omid");
        result.add(item);

    }

    @Test
    public void testToList()
    {
        List<User> userList = QueryMapper.toList(result, User.class);
        Assert.assertEquals(userList.size(), 1);
        System.out.println(userList.iterator().next().getFullName());
    }

    @Test
    public void testToObject()
    {
        User user = QueryMapper.toObject(result, User.class);
        Assert.assertNotNull(user);
    }

}
