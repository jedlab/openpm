package com.jedlab.framework.report;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.beanutils.PropertyUtils;

import com.jedlab.framework.report.JasperReportManager.Paging;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRRewindableDataSource;

/**
 * @author OmidPourhadi [AT] gmail [DOT] com
 *
 */
public class JasperPagingDatasource implements JRRewindableDataSource
{

    private static final int MAX_RESULT = 1000;

    private long totalCount;

    private Object currentBean;

    private JasperPaginationHandler paginationHandler;

    private int firstResult;

    private Collection<?> data;

    private Iterator<?> iterator;

    private int currentIndex;
    
    private int rowCounter;

    public JasperPagingDatasource(JasperPaginationHandler paginationHandler)
    {
        this.paginationHandler = paginationHandler;
        this.totalCount = paginationHandler.getResultCount();
    }

    @Override
    public boolean next() throws JRException
    {
        if ((this.totalCount == 0) || (this.totalCount >= 0 && this.currentIndex >= this.totalCount))
        {
            return false;
        }
        if (this.data == null || this.rowCounter >= MAX_RESULT)
        {
            Paging p = new Paging(this.firstResult, MAX_RESULT).getPage();
            this.data = paginationHandler.getResultList(p.getFirstResult(), p.getMaxResult());
            if (this.data != null)
            {
                this.iterator = this.data.iterator();
            }
            // reset
            this.firstResult++;
            this.currentIndex = 0;
        }
        if (this.iterator != null)
        {
            boolean hasNext = this.iterator.hasNext();
            if (hasNext)
            {
                this.currentBean = this.iterator.next();
                this.currentIndex++;
                this.rowCounter++;
                return true;
            }
        }
        //
        return false;
    }

    @Override
    public Object getFieldValue(JRField jrField) throws JRException
    {
        String reportFieldName = jrField.getName();
        Object bean = this.currentBean;
        if (bean != null)
        {
            try
            {
                return PropertyUtils.getProperty(bean, reportFieldName);
            }
            catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
            {
                throw new JRException(e);
            }
            // TODO : use reportfield annotation later
            // Field[] fields = bean.getClass().getDeclaredFields();
            // for (Field field : fields)
            // {
            // ReportField reportField =
            // field.getDeclaredAnnotation(ReportField.class);
            // if(reportField != null)
            // {
            // String rptField = reportField.value();
            // if(reportFieldName.equals(rptField))
            // {
            //
            // }
            // }
            // }
        }
        return null;
    }

    @Override
    public void moveFirst()
    {
        if (this.data != null)
        {
            this.iterator = this.data.iterator();
        }
    }
    
}
