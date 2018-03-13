package com.jedlab.framework.report;

import java.io.File;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

/**
 * @author omidp
 *
 */
public class JasperReportManager
{

    private static final String REPORT_PATH = "/report" + File.separator;

    public static File getReportFile(String fileName) throws URISyntaxException
    {
        String path = REPORT_PATH + fileName;
        URL rsrc = Thread.currentThread().getContextClassLoader().getResource(path);
        if (rsrc == null) rsrc = JasperReportManager.class.getClassLoader().getResource(path);
        if (rsrc == null) rsrc = JasperReportManager.class.getResource(path);
        if (rsrc == null) throw new IllegalArgumentException("file not found " + path);
        return Paths.get(rsrc.toURI()).toFile();
    }

    public static void exportPdf(File file, OutputStream os, JasperPaginationHandler paginationHandler) throws JRException
    {
        exportPdf(file, os, paginationHandler, new HashMap<String, Object>());
    }

    public static void exportPdf(File file, OutputStream os, JasperPaginationHandler paginationHandler, Map<String, Object> parameters)
            throws JRException
    {
        try
        {
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(file);
            JRPropertiesUtil props = JRPropertiesUtil.getInstance(DefaultJasperReportsContext.getInstance());
            if (parameters == null) parameters = new HashMap<>();
            parameters.put("REPORT_LOCALE", new Locale("fa", "IR"));
            parameters.put("REPORT_RESOURCE_BUNDLE", ResourceBundle.getBundle("i18n/messages", new Locale("fa", "IR")));
            JasperReportsUtils.renderAsPdf(jasperReport, parameters, new JasperPagingDatasource(paginationHandler), os);
        }
        finally
        {

        }
    }

    public static void exportExcel(File file, OutputStream os, JasperPaginationHandler paginationHandler) throws JRException
    {
        try
        {
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(file);
            JRPropertiesUtil props = JRPropertiesUtil.getInstance(DefaultJasperReportsContext.getInstance());
            JasperReportsUtils.renderAsXls(jasperReport, new HashMap<String, Object>(), new JasperPagingDatasource(paginationHandler), os);
        }
        finally
        {

        }
    }

   
    public static class Paging
    {
        private int firstResult;
        private int maxResult;

        public Paging(int firstResult, int maxResult)
        {
            this.firstResult = firstResult;
            this.maxResult = maxResult;
        }

        public Paging getPage()
        {
            int offset = firstResult * maxResult;             
            int limit =  maxResult;
            return new Paging(offset, limit);
        }

        public int getFirstResult()
        {
            return firstResult;
        }

        public int getMaxResult()
        {
            return maxResult;
        }

    }



}
