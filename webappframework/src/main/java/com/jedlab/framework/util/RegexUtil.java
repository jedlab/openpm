package com.jedlab.framework.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil
{

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
