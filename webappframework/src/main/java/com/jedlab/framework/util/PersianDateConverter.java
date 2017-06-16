package com.jedlab.framework.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author omid pourhadi Email : omidpourhadi AT gmail DOT com
 */
public class PersianDateConverter
{

    

    private final static Logger LOGGER = Logger.getLogger(PersianDateConverter.class.getName());
    
    private final static Pattern patternDate = Pattern.compile("^(\\d{4})(-|/)(\\d{2})(-|/)(\\d{2})$", Pattern.CASE_INSENSITIVE);
    private final static Pattern patternDateTime = Pattern.compile("^(\\d{4})(-|/)(\\d{2})(-|/)(\\d{2})\\s(\\d{2}):(\\d{2})$",
            Pattern.CASE_INSENSITIVE);    
    private final static Pattern patternDateTimeSec = Pattern.compile("^(\\d{4})(-|/)(\\d{2})(-|/)(\\d{2})\\s(\\d{2}):(\\d{2}):(\\d{2})$",
            Pattern.CASE_INSENSITIVE);
    private final static Pattern patternDateTimeSec2 = Pattern.compile("^(\\d{4})(-|/)(\\d{2})(-|/)(\\d{2})\\s(\\d{2}):(\\d{2}):(\\d{2})(\\.\\d+)*",
            Pattern.CASE_INSENSITIVE);

    private String[] formats = { "yyyy/MM/dd", "yyyy/MM/dd HH:mm", "yyyy/MM/dd HH:mm:ss", "EEE MMM d HH:mm:ss z yyyy",
            "EEE, d MMM yyyy HH:mm:ss", "EEE MMM d HH:mm:ss z yyyy", "yyyy-MM-dd HH:mm", "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss" };

    private static final double PERSIAN_EPOCH = 1948320.5;;

    private static final double GREGORIAN_EPOCH = 1721425.5;

    private static final PersianDateConverter INSTANCE = new PersianDateConverter();

    private PersianDateConverter()
    {
    }

    public static PersianDateConverter getInstance()
    {
        return INSTANCE;
    }

    /**
     * @param solarDateAsTimeStamp
     *            : can be yyyy/MM/dd or yyyy/MM/dd HH:mm
     * @return
     */
    public String SolarToGregorian(String solarDateAsTimeStamp)
    {
        // fail-fast
        if (solarDateAsTimeStamp == null || solarDateAsTimeStamp.isEmpty())
            return "";
        MatcherHolder match = new MatcherHolder().match(solarDateAsTimeStamp);
        //
        DateConverter pdc = new DateConverter(match.getDelimiter());
        DateHolder dh = pdc.convertJdnToGregorian(pdc.convertPersianToJdn(match.getM_currentYear(), match.getM_currentMonth(),
                match.getM_currentDay()));
        StringBuilder result = new StringBuilder(dh.toString());
        if (patternDateTime.matcher(solarDateAsTimeStamp).matches()
                || patternDateTimeSec.matcher(solarDateAsTimeStamp).matches()
                || patternDateTimeSec2.matcher(solarDateAsTimeStamp).matches())
        {
            result.append(match.toString());
        }
        return result.toString();
    }

    public Date SolarToGregorianAsDate(String solarDateAsTimeStamp)
    {
        if (solarDateAsTimeStamp == null || solarDateAsTimeStamp.isEmpty())
            return null;
        Calendar cal = Calendar.getInstance();
        MatcherHolder match = new MatcherHolder().match(solarDateAsTimeStamp);
        //
        DateConverter pdc = new DateConverter(match.getDelimiter());
        DateHolder dh = pdc.convertJdnToGregorian(pdc.convertPersianToJdn(match.getM_currentYear(), match.getM_currentMonth(),
                match.getM_currentDay()));
        cal.set(Calendar.YEAR, dh.getYear());
        cal.set(Calendar.MONTH, dh.getMonth()-1);//zero-base index
        cal.set(Calendar.DATE, dh.getDay());
        if (patternDateTime.matcher(solarDateAsTimeStamp).matches()
                || patternDateTimeSec.matcher(solarDateAsTimeStamp).matches()
                || patternDateTimeSec2.matcher(solarDateAsTimeStamp).matches())
        {
            cal.set(Calendar.HOUR_OF_DAY, match.getM_currentHour());
            cal.set(Calendar.MINUTE, match.getM_currentMin());
            cal.set(Calendar.SECOND, match.getM_currentSec());
        }
        return cal.getTime();
    }

    public String GregorianToSolar(String gregorianDateAsTimeStamp)
    {
        if (gregorianDateAsTimeStamp == null || gregorianDateAsTimeStamp.isEmpty())
            return "";
        MatcherHolder match = new MatcherHolder().match(gregorianDateAsTimeStamp);
        //
        DateConverter pdc = new DateConverter(match.getDelimiter());

        DateHolder dh = pdc.convertJdnToPersian(pdc.convertGregorianToJdn(match.getM_currentYear(), match.getM_currentMonth(),
                match.getM_currentDay()));
        StringBuilder result = new StringBuilder(dh.toString());
        if (patternDateTime.matcher(gregorianDateAsTimeStamp).matches() 
                || patternDateTimeSec.matcher(gregorianDateAsTimeStamp).matches()
                || patternDateTimeSec2.matcher(gregorianDateAsTimeStamp).matches())
        {
            result.append(match.toString());
        }
        return result.toString();
    }

    public String GregorianToSolar(Date gregorianDateAsTimeStamp, boolean includeTime)
    {
        if (gregorianDateAsTimeStamp == null)
            return "";
        SimpleDateFormat sdf = new SimpleDateFormat();
        if (includeTime)
            sdf.applyPattern("yyy/MM/dd HH:mm");
        else
            sdf.applyPattern("yyy/MM/dd");
        return GregorianToSolar(sdf.format(gregorianDateAsTimeStamp));
    }

    /**
     * @author omidp
     *         <p>
     *         separate the given date as holder
     *         </p>
     */
    private final class MatcherHolder
    {
        int m_currentYear;
        int m_currentMonth;
        int m_currentDay;
        int m_currentHour;
        int m_currentMin;
        int m_currentSec;
        String delimiter = "/";
        StringBuilder sb = new StringBuilder();

        public MatcherHolder match(String solarDateAsTimeStamp)
        {
            Matcher timeMatcher = patternDateTime.matcher(solarDateAsTimeStamp);
            Matcher timeSecMatcher = patternDateTimeSec.matcher(solarDateAsTimeStamp);
            Matcher dateMatcher = patternDate.matcher(solarDateAsTimeStamp);
            Matcher timeSecMatcher2 = patternDateTimeSec2.matcher(solarDateAsTimeStamp);
            if (timeMatcher.matches())
            {
                // yyyy/MM/dd HH:mm
                m_currentYear = Integer.parseInt(timeMatcher.group(1));
                m_currentMonth = Integer.parseInt(timeMatcher.group(3));
                m_currentDay = Integer.parseInt(timeMatcher.group(5));
                m_currentHour = Integer.parseInt(timeMatcher.group(6));
                m_currentMin = Integer.parseInt(timeMatcher.group(7));
                delimiter = timeMatcher.group(2);
            }
            else if(timeSecMatcher.matches())
            {
                // yyyy/MM/dd HH:mm
                m_currentYear = Integer.parseInt(timeSecMatcher.group(1));
                m_currentMonth = Integer.parseInt(timeSecMatcher.group(3));
                m_currentDay = Integer.parseInt(timeSecMatcher.group(5));
                m_currentHour = Integer.parseInt(timeSecMatcher.group(6));
                m_currentMin = Integer.parseInt(timeSecMatcher.group(7));
                m_currentSec = Integer.parseInt(timeSecMatcher.group(8));
                delimiter = timeSecMatcher.group(2);
            }
            else if(timeSecMatcher2.matches())
            {
                m_currentYear = Integer.parseInt(timeSecMatcher2.group(1));
                m_currentMonth = Integer.parseInt(timeSecMatcher2.group(3));
                m_currentDay = Integer.parseInt(timeSecMatcher2.group(5));
                m_currentHour = Integer.parseInt(timeSecMatcher2.group(6));
                m_currentMin = Integer.parseInt(timeSecMatcher2.group(7));
                m_currentSec = Integer.parseInt(timeSecMatcher2.group(8));
                delimiter = timeSecMatcher2.group(2);
            }
            else
            {
                if (dateMatcher.matches() == false)
                {
                    LOGGER.info(solarDateAsTimeStamp);
                    throw new IllegalArgumentException("can not match date with valid format : " + solarDateAsTimeStamp);
                }
                // yyy-MM-dd
                m_currentYear = Integer.parseInt(dateMatcher.group(1));
                m_currentMonth = Integer.parseInt(dateMatcher.group(3));
                m_currentDay = Integer.parseInt(dateMatcher.group(5));
                delimiter = dateMatcher.group(2);

            }
            return this;
        }

        public int getM_currentYear()
        {
            return m_currentYear;
        }

        public int getM_currentMonth()
        {
            return m_currentMonth;
        }

        public int getM_currentDay()
        {
            return m_currentDay;
        }

        public int getM_currentHour()
        {
            return m_currentHour;
        }

        public int getM_currentMin()
        {
            return m_currentMin;
        }
        
        public int getM_currentSec()
        {
            return m_currentSec;
        }

        public String getDelimiter()
        {
            return delimiter;
        }
        
        

        @Override
        public String toString()
        {
            sb.append(" ");
            if (getM_currentHour() < 10)
                sb.append("0");
            sb.append(getM_currentHour()).append(":");
            if (getM_currentMin() < 10)
                sb.append("0");
            sb.append(getM_currentMin());
            if(getM_currentSec() < 10 && getM_currentSec() > 0)
                sb.append("0").append(getM_currentSec());
            if(getM_currentSec() > 10)
                sb.append(getM_currentSec());
            return sb.toString();
        }

    }

    /**
     * @author omidp
     *         <p>
     *         Hold the converted date as separator
     *         </p>
     */
    private final class DateHolder
    {

        private double year, month, day;
        private StringBuilder sb;
        private String delimiter;

        public DateHolder(double year, double month, double day, String delimiter)
        {
            this.year = year;
            this.month = month;
            this.day = day;
            this.delimiter = delimiter;
            sb = new StringBuilder();
        }

        public int getYear()
        {
            return (int) year;
        }

        public int getMonth()
        {
            return (int) month;
        }

        public int getDay()
        {
            return (int) day;
        }

        @Override
        public String toString()
        {
            sb.append((int) year).append(delimiter);
            if (month < 10)
                sb.append("0");
            sb.append((int) month);
            sb.append(delimiter);
            if (day < 10)
                sb.append("0");
            sb.append((int) day);
            return sb.toString();
        }
    }

    private class DateConverter
    {
        private String delimiter;

        public DateConverter(String delimiter)
        {
            this.delimiter = delimiter;
        }

        // PERSIAN_TO_JD -- Determine Julian day from Persian date
        public double convertPersianToJdn(double year, double month, double day)
        {
            double epbase, epyear;

            epbase = year - ((year >= 0) ? 474 : 473);
            epyear = 474 + mod(epbase, 2820);

            return day + ((month <= 7) ? ((month - 1) * 31) : (((month - 1) * 30) + 6)) + Math.floor(((epyear * 682) - 110) / 2816)
                    + (epyear - 1) * 365 + Math.floor(epbase / 2820) * 1029983 + (PERSIAN_EPOCH - 1);
        }

        public DateHolder convertJdnToPersian(double jd)
        {
            double year, month, day, depoch, cycle, cyear, ycycle, aux1, aux2, yday;

            jd = Math.floor(jd) + 0.5;

            depoch = jd - convertPersianToJdn(475, 1, 1);
            cycle = Math.floor(depoch / 1029983);
            cyear = mod(depoch, 1029983);
            if (cyear == 1029982)
            {
                ycycle = 2820;
            }
            else
            {
                aux1 = Math.floor(cyear / 366);
                aux2 = mod(cyear, 366);
                ycycle = Math.floor(((2134 * aux1) + (2816 * aux2) + 2815) / 1028522) + aux1 + 1;
            }
            year = ycycle + (2820 * cycle) + 474;
            if (year <= 0)
            {
                year--;
            }
            yday = (jd - convertPersianToJdn(year, 1, 1)) + 1;
            month = (yday <= 186) ? Math.ceil(yday / 31) : Math.ceil((yday - 6) / 30);
            day = (jd - convertPersianToJdn(year, month, 1)) + 1;

            return new DateHolder(year, month, day, delimiter);
        }

        /**
         * @param jdn
         * @return
         */
        public DateHolder convertJdnToGregorian(double jd)
        {
            double wjd, depoch, quadricent, dqc, cent, dcent, quad, dquad, yindex, dyindex, year, yearday, leapadj;

            wjd = Math.floor(jd - 0.5) + 0.5;
            depoch = wjd - GREGORIAN_EPOCH;
            quadricent = Math.floor(depoch / 146097);
            dqc = mod(depoch, 146097);
            cent = Math.floor(dqc / 36524);
            dcent = mod(dqc, 36524);
            quad = Math.floor(dcent / 1461);
            dquad = mod(dcent, 1461);
            yindex = Math.floor(dquad / 365);
            year = (quadricent * 400) + (cent * 100) + (quad * 4) + yindex;
            if (!((cent == 4) || (yindex == 4)))
            {
                year++;
            }
            yearday = wjd - convertGregorianToJdn(year, 1, 1);
            leapadj = ((wjd < convertGregorianToJdn(year, 3, 1)) ? 0 : (Leap_Gregorian(year) ? 1 : 2));
            double month = Math.floor((((yearday + leapadj) * 12) + 373) / 367);
            double day = (wjd - convertGregorianToJdn(year, month, 1)) + 1;

            return new DateHolder(year, month, day, delimiter);
        }

        public double convertGregorianToJdn(double year, double month, int day)
        {
            return (GREGORIAN_EPOCH - 1) + (365 * (year - 1)) + Math.floor((year - 1) / 4) + (-Math.floor((year - 1) / 100))
                    + Math.floor((year - 1) / 400)
                    + Math.floor((((367 * month) - 362) / 12) + ((month <= 2) ? 0 : (Leap_Gregorian(year) ? -1 : -2)) + day);
        }

        // LEAP_GREGORIAN -- Is a given year in the Gregorian calendar a leap
        // year ?
        public boolean Leap_Gregorian(double year)
        {
            return ((year % 4) == 0) && (!(((year % 100) == 0) && ((year % 400) != 0)));
        }

        /**
         * @param jdn
         * @return
         */
        public DateHolder convertJdnToJulian(double jdn)
        {
            double z, a, alpha, b, c, d, e, year, month, day, td = 0;

            td += 0.5;
            z = Math.floor(td);

            a = z;
            b = a + 1524;
            c = Math.floor((b - 122.1) / 365.25);
            d = Math.floor(365.25 * c);
            e = Math.floor((b - d) / 30.6001);

            month = Math.floor((e < 14) ? (e - 1) : (e - 13));
            year = Math.floor((month > 2) ? (c - 4716) : (c - 4715));
            day = b - d - Math.floor(30.6001 * e);

            /*
             * If year is less than 1, subtract one to convert from a zero based
             * date system to the common era system in which the year -1 (1
             * B.C.E) is followed by year 1 (1 C.E.).
             */

            if (year < 1)
            {
                year--;
            }

            return new DateHolder(year, month, day, delimiter);
        }

        public double mod(double args1, double args2)
        {
            return args1 % args2;
        }

    }
    

}
