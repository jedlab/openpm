package com.jedlab.framework.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;


/**
 * @author OmidPourhadi [AT] gmail [DOT] com
 *
 */
public class FarsiRequestWrapper extends HttpServletRequestWrapper
{

    HttpServletRequest request;

    public FarsiRequestWrapper(HttpServletRequest request)
    {
        super(request);
        this.request = request;
    }

    @Override
    public String getCharacterEncoding()
    {
       
            return "UTF-8";
    }

    @Override
    public String getParameter(String name)
    {

        String parameter = super.getParameter(name);        
        String pchar = handleSpecialPersianChars(parameter);
        return pchar;
    }
    
    public static String handleSpecialPersianChars(String source)
    {
        if (source == null)
            return null;
        source = source.replace("ك", "ک");
        source = source.replace("ي", "ی");
        source = source.replaceAll("\\s+", " ");
        source = source.replaceAll("‬", "");
        source = source.replaceAll("‫", "");
        source = source.replaceAll("‏", "");
        return source.trim();
    }

}
