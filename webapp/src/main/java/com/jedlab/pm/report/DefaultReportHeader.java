package com.jedlab.pm.report;

import java.util.Date;

import com.jedlab.framework.report.ReportHeader;
import com.jedlab.framework.spring.security.AuthenticationUtil;
import com.jedlab.framework.util.PersianDateConverter;

/**
 * @author omidp
 *
 */
public class DefaultReportHeader implements ReportHeader
{

    private String title;

    public DefaultReportHeader(String title)
    {
        this.title = title;
    }

    @Override
    public String logoPath()
    {
        return null;
    }

    @Override
    public String title()
    {
        return title;
    }

    @Override
    public String date()
    {
        return PersianDateConverter.getInstance().GregorianToSolar(new Date(), true);
    }

    @Override
    public String username()
    {
        return AuthenticationUtil.getUsername();
    }

    @Override
    public String persianDate()
    {
        return PersianDateConverter.getInstance().GregorianToSolar(new Date(), true);
    }

}
