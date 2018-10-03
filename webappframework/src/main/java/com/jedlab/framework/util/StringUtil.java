package com.jedlab.framework.util;


public class StringUtil
{

    public static boolean isNotEmpty(String input)
    {
        return (input != null && input.trim().length() > 0);
    }

    public static boolean isEmpty(String input)
    {
        if (input == null)
        {
            return true;
        }
        return input.trim().length() == 0;
    }

    public static String cut(String input, int length)
    {
        if (isEmpty(input))
        {
            return null;
        }
        if (input != null && input.length() > length)
        {
            return input.substring(0, length).concat(" ...");
        }
        return input;
    }

    public static String stringCutter(String input, int howmany)
    {
        return ((input != null && input.length() > howmany) ? input.substring(0, howmany) : input);
    }

    public static int countDuplicateWordsOccurence(String input1, String input2)
    {
        int occurence = 0;
        for (int i = 0; i < input1.length(); i++)
        {
            char at1 = input1.charAt(i);
            for (int j = 0; j < input2.length(); j++)
            {
                char at2 = input2.charAt(j);
                if (at1 == at2)
                {
                    if (i == j)
                        occurence++;
                }
            }
        }
        return occurence;
    }

    public static boolean isNotNull(Object input)
    {
        String val = input == null ? null : String.valueOf(input);
        return isNotEmpty(val);
    }
  
    public static String stringSpaceRemover(String str)
    {
        // Remove all space from str
        str = str.replace(" ", "");
        str = str.replace("آ", "ا");
        return CharacterUtil.handleSpecialPersianChars(str);
    }
      
    public static Boolean stringContains(String sourceStr, String targetStr)
    {
        if(sourceStr == null || targetStr == null)
            return false;
        if(isEmpty(targetStr) && isNotEmpty(sourceStr))
            return false;
        if(isEmpty(sourceStr) && isNotEmpty(targetStr))
            return false;
        if(stringSpaceRemover(sourceStr.toLowerCase()).indexOf(stringSpaceRemover(targetStr.toLowerCase())) > -1)
            return true;
        if(stringSpaceRemover(targetStr.toLowerCase()).indexOf(stringSpaceRemover(sourceStr.toLowerCase())) > -1)
            return true;
        return false;
    }
    
    public static String removeNewLine(String input)
    {
        if(isEmpty(input))
            return input;
        return input.replaceAll("\\n", "");
    }
    
}
