package com.jedlab.framework.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil
{

    public static final String USERNAME_PATTERTN = "^([a-zA-Z0-9]+[a-zA-Z0-9_\\.]*[a-zA-Z0-9]+){3,15}$";
    public static final String PASSWORD_PATTERN = "(.*[^a-zA-Z0-9].*)|(.{0,7})|([^A-Z]*)|([^0-9]*)";
    public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    public static final String IMAGEFILE_PATTERN = "([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)";
    public static final String IPADDRESS_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
    public static final String DIGIT_PATTERN = "\\d+";
    
    
    public static boolean match(String patternToCompile, String input)
    {
        Pattern pattern = Pattern.compile(patternToCompile);
        Matcher matcher = pattern.matcher(input);
        if (matcher.matches())
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public static boolean find(String input, String find)
    {
        Pattern p = Pattern.compile(find, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(input);
        return m.find();
    }

    public static int findOccurrence(String input, String find)
    {
        Pattern p = Pattern.compile(find, Pattern.CASE_INSENSITIVE);
        int cnt = 0;
        Matcher matcher = p.matcher(input);
        while (matcher.find())
        {
            cnt++;
        }
        return cnt;
    }
    

}
