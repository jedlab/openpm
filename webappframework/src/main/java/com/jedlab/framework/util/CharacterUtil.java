package com.jedlab.framework.util;

/**
 * @author Omid Pourhadi
 *
 */
public class CharacterUtil
{
    /**
     *@param Assimilate all Persian special character.
     * @see this method sync with preparestring() postgresql function, if you need change this function you have to change preparestring() in postgresql
     * @author Ali Taghaddosy Pour : a.taghadosi AT gmail DOT com
     * 
     */
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
