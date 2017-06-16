package com.omidp.oframework.test;

import junit.framework.Assert;

import org.junit.Test;

import com.jedlab.framework.db.query.XmlParser;

/**
 * @author omidp
 *
 */
public class NativeQueryTestCase
{

    @Test
    public void testNativeQueries()
    {
        String authorBooksQuery = XmlParser.findNativeQuery("Author", "findBooks");
        Assert.assertNotNull(authorBooksQuery);
    }
    
}
