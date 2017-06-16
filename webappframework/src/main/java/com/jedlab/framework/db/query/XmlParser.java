package com.jedlab.framework.db.query;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Omid Pourhadi
 * <p>use a structural xml file to store your queries, instead of writing them in the middle of your code</p>
 * 1. it doesn't need to recompile your code
 * 2. you can paste it in your sql editor and run it
 * 3. makes your code clean
 */
public class XmlParser
{

    private final static Logger logger = LoggerFactory.getLogger(XmlParser.class);

    private static String NATIVE_FILENAME = "nativeQueries.xml";
    private static String HBM_FILENAME = "hbmQueries.xml";

    public static String findNativeQuery(String modelName, String queryName)
    {
        return parseFile(NATIVE_FILENAME, modelName, queryName);
    }

    private static String parseFile(String fileName, String modelName, String queryName)
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        InputStream stream = null;
        try
        {
            DocumentBuilder db = dbf.newDocumentBuilder();
            stream = XmlParser.class.getResourceAsStream(fileName);

            if (stream == null)
            {
                stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);

            }
            if (stream == null)
            {
                stream = XmlParser.class.getClassLoader().getResourceAsStream(fileName);
            }
            Document dom = db.parse(stream);
            NodeList nl = dom.getElementsByTagName(modelName);

            if (nl != null && nl.getLength() > 0)
            {
                for (int i = 0; i < nl.getLength(); i++)
                {

                    Element el = (Element) nl.item(i);
                    String queryAttr = el.getAttribute("queryName");
                    if (queryName.equals(queryAttr))
                    {
                        return el.getTextContent().trim();
                    }

                }
            }
        }
        catch (ParserConfigurationException e)
        {
            logger.info(e.getMessage());
        }
        catch (SAXException e)
        {
            logger.info(e.getMessage());
        }
        catch (IOException e)
        {
            logger.info(e.getMessage());
        }
        finally
        {
            if (stream != null)
                try
                {
                    stream.close();
                }
                catch (IOException e)
                {
                    logger.info(e.getMessage());
                }
        }
        return null;
    }

    public static String findHbmQuery(String modelName, String queryName)
    {
        return parseFile(HBM_FILENAME, modelName, queryName);
    }
    
    public static class NodeListIter implements Iterable<Node>
    {

        private final Document document;
        private final String rootTagName;

        public NodeListIter(Document document, String rootTagName)
        {
            this.rootTagName = rootTagName;
            this.document = document;
        }

        @Override
        public Iterator<Node> iterator()
        {
            NodeList orgList = document.getElementsByTagName(rootTagName);
            List<Node> nodes = new ArrayList<Node>();
            for (int i = 0; i < orgList.getLength(); i++)
            {
                nodes.add(orgList.item(i));
            }
            return nodes.iterator();
        }

    }

}
