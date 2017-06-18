package com.jedlab.framework.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author omid
 * 
 */
public class CollectionUtil
{

    public static String commaSeparated(Long[] numbers)
    {
        if (numbers == null)
            return null;
        List<Long> arrList = Arrays.asList(numbers);
        return commaSeparated(arrList);
    }

    public static String commaSeparated(List<Long> numbers)
    {
        if (numbers == null)
            return null;
        String res = "";
        if (numbers != null && numbers.size() > 0)
        {
            for (int i = 0; i < numbers.size(); i++)
            {
                if (i > 0)
                    res += ",";
                res += numbers.get(i);
            }
        }
        return res;
    }
    
    public static String commaSeparatedString(List<String> items)
    {
        if (items == null)
            return null;
        String res = "";
        if (items != null && items.size() > 0)
        {
            for (int i = 0; i < items.size(); i++)
            {
                if (i > 0)
                    res += ",";
                res += "'"+items.get(i)+"'";
            }
        }
        return res;
    }

    public static List<String> skipEmpty(List<String> input)
    {
        List<String> resultList = new ArrayList<String>();
        for (String strItem : input)
        {
            if (StringUtil.isNotEmpty(strItem))
                resultList.add(strItem);
        }
        return resultList;
    }

    public static boolean isNotEmpty(Collection<?> input)
    {
        return input != null && input.size() > 0;
    }

    public static boolean isEmpty(Collection<?> input)
    {
        return isNotEmpty(input) == false;
    }

    public static List<String> convertToList(String commaSeparated, String splitter)
    {
        List<String> result = new ArrayList<String>();
        if (commaSeparated.contains(splitter))
        {
            String[] split = commaSeparated.split(splitter);
            if (split != null && split.length > 0)
            {
                for (int i = 0; i < split.length; i++)
                {
                    String val = split[i].trim().replace(" ", "");
                    if (StringUtil.isNotEmpty(val))
                        result.add(val);
                }

            }
        }
        else
        {
            if (StringUtil.isNotEmpty(commaSeparated))
            {
                try
                {
                    result.add(commaSeparated.trim().replace(" ", ""));
                }
                catch (Exception e)
                {
                }
            }
        }
        return result;
    }

    public static List<Long> convertStringToLong(String commaSeparated)
    {
        List<Long> longList = new ArrayList<Long>();
        if (commaSeparated.contains(","))
        {
            String[] split = commaSeparated.split(",");
            if (split != null && split.length > 0)
            {
                for (int i = 0; i < split.length; i++)
                {
                    String val = split[i].trim().replace(" ", "");
                    if (StringUtil.isNotEmpty(val))
                        longList.add(Long.valueOf(val));
                }

            }
        }
        else
        {
            if (StringUtil.isNotEmpty(commaSeparated))
            {
                try
                {
                    longList.add(Long.valueOf(commaSeparated.trim().replace(" ", "")));
                }
                catch (Exception e)
                {
                }
            }
        }
        return longList;
    }

    public static <T> List<T> union(List<T> list1, List<T> list2)
    {
        Set<T> set = new HashSet<T>();

        set.addAll(list1);
        set.addAll(list2);

        return new ArrayList<T>(set);
    }

    public static <T> List<T> intersection(List<T> list1, List<T> list2)
    {
        List<T> list = new ArrayList<T>();

        for (T t : list1)
        {
            if (list2.contains(t))
            {
                list.add(t);
            }
        }
        return list;
    }

    public static BigDecimal returnMinimum(List<BigDecimal> numberList)
    {
        if (CollectionUtil.isEmpty(numberList))
            throw new IllegalArgumentException("List cannot be null in returnMinimum method input parameter");
        BigDecimal minNumber = numberList.get(0);
        for (BigDecimal number : numberList)
        {
            if (minNumber.compareTo(number) > 0)
                minNumber = number;
        }
        return minNumber;
    }

    

}
