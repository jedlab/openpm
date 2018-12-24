package com.jedlab.framework.report;

import java.awt.Color;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.context.MessageSource;

import com.jedlab.framework.reflections.ReflectionUtil;
import com.jedlab.framework.spring.SpringUtil;
import com.jedlab.framework.util.PersianDateConverter;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.CustomExpression;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.ColumnBuilderException;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.constants.Page;
import ar.com.fdvs.dj.domain.constants.Stretching;
import ar.com.fdvs.dj.domain.constants.VerticalAlign;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsAbstractExporter;
import net.sf.jasperreports.engine.export.JRXlsAbstractExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.type.RunDirectionEnum;

/**
 * @author OmidPourhadi [AT] gmail [DOT] com
 *
 */
public abstract class JasperDataExporter<E> implements Serializable
{

    private Class<E> entityClass;

    private Map<String, Object> reportPramaeters = new HashMap<String, Object>();

    public enum ExportType {
        XLS, PDF, INLINEPDF;
    }

    private ExportType exportType = ExportType.XLS;

    public ExportType getExportType()
    {
        return exportType;
    }

    public void setExportType(ExportType exportType)
    {
        this.exportType = exportType;
    }

    public void exportByType(String expType)
    {
        exportType = ExportType.valueOf(expType.toUpperCase());
        export();
    }

    public void addParameter(String name, Object value)
    {
        reportPramaeters.put(name, value);
    }

    public void export()
    {
        // deprecated only used for jboss VFS
        // JRProperties.setProperty(JRCompiler.COMPILER_TEMP_DIR,
        // Environment.getJasperDirectory());
        // JRProperties.setProperty(JRCompiler.COMPILER_CLASSPATH,
        // Environment.getJasperClasspath());
        try
        {
            doWork();
        }
        catch (ColumnBuilderException | ClassNotFoundException | JRException | IOException | URISyntaxException | InstantiationException
                | IllegalAccessException e)
        {
            throw new IllegalArgumentException(e);
        }
    }

    private void doWork() throws JRException, ColumnBuilderException, ClassNotFoundException, IOException, URISyntaxException,
            InstantiationException, IllegalAccessException
    {

        //
        FastReportBuilder frb = new FastReportBuilder();
        //
        initSettings(frb);
        //
        JasperPagingDatasource ds = new JasperPagingDatasource(getPaginationHandler());
        Map<String, Object> parameters = new HashMap();
        parameters.put("REPORT_LOCALE", new Locale("fa", "IR"));
        // parameters.put("REPORT_RESOURCE_BUNDLE",bundle);
        //
        if (ExportType.XLS.equals(getExportType()) == false)
        {
            createReportHeader(frb, getReportHeader(), parameters);
        }
        //
        String fname = getFileName();
        if (fname == null)
            fname = PersianDateConverter.getInstance().GregorianToSolar(new SimpleDateFormat("yyyy-MM-dd").format(new Date())) + "_report";
        Exporter e = new JasperPdfExporter(fname + ".pdf");
        if (ExportType.XLS.equals(getExportType()))
        {
            e = new JasperExcelExporter(fname + ".xls");
            frb.setIgnorePagination(true);
            parameters.put("IS_IGNORE_PAGINATION", true);
            frb.setUseFullPageWidth(false);
        }
        if (ExportType.INLINEPDF.equals(getExportType()))
        {
            e = new JasperPdfInlineExporter(fname + ".pdf");
        }
        //
        JRExporter exporter = e.getJasperExporter();
        //
        processColumns(frb);
        beforeGenerateReport(frb);
        parameters.putAll(reportPramaeters);
        DynamicReport build = frb.build();
        JasperPrint jasperPrint = DynamicJasperHelper.generateJasperPrint(build, new ClassicLayoutManager(), ds, parameters);
        
        FacesContext context = FacesContext.getCurrentInstance();        
        OutputStream os = outputstream(e, context);
        JRPropertiesUtil.getInstance(DefaultJasperReportsContext.getInstance());
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, os);
        exporter.exportReport();
        os.flush();
        os.close();
        if(context != null)
            context.responseComplete();
    }
    
    protected OutputStream outputstream(Exporter e, FacesContext context) throws IOException
    {
        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
        response.setContentType(e.getContentType());
        response.setCharacterEncoding("UTF-8");
        //
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        if (isInternetExplorer(request)) response.setHeader("Content-Disposition",
                e.getContentDisposition() + "; filename=\"" + URLEncoder.encode(e.getFileName(), "utf-8") + "\"");
        else response.setHeader("Content-Disposition",
                e.getContentDisposition() + "; filename=\"" + MimeUtility.encodeWord(e.getFileName(), "UTF-8", "Q") + "\"");
        //
        OutputStream os = response.getOutputStream();
        return os;
    }

    protected void beforeGenerateReport(FastReportBuilder frb)
    {

    }

    protected String getFileName()
    {
        return null;
    }

    private void initSettings(FastReportBuilder frb) throws URISyntaxException
    {
        frb.setDefaultStyles(ReportStyleManager.INSTACE.cellStyle(), ReportStyleManager.INSTACE.cellStyle(),
                ReportStyleManager.INSTACE.headerStyle(), ReportStyleManager.INSTACE.headerStyle());
        frb.setPrintBackgroundOnOddRows(true);
        frb.setUseFullPageWidth(true);
        frb.setOddRowBackgroundStyle(ReportStyleManager.INSTACE.oddRowBackgroundStyle());
        frb.setHeaderHeight(70);
        // frb.setPageSizeAndOrientation(Page.Page_A4_Landscape());
        frb.setReportLocale(new Locale("fa", "IR"));
        frb.setResourceBundle("messages");
        frb.setTemplateFile(getTemplateFile());
    }

    protected String getTemplateFile() 
    {
        try
        {
            if (ExportType.XLS.equals(getExportType()))
                return JasperReportManager.getReportFile("xlsReportTemplate.jrxml").getAbsolutePath();
            return JasperReportManager.getReportFile("reportTemplate.jrxml").getAbsolutePath();
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private void createReportHeader(FastReportBuilder frb, ReportHeader header, Map<String, Object> parameters)
    {
        // REPORT_PARAMETERS_MAP
        Locale locale = new Locale("fa", "IR");
        MessageSource msg = SpringUtil.getBean(MessageSource.class);
        if(header != null)
        {
            parameters.put("Report_Logo", header.logoPath());
            parameters.put("Report_Name_Label", msg.getMessage("form.reportName", null, locale));
            parameters.put("Report_Name", header.title());
            parameters.put("Report_User_Label", msg.getMessage("form.reportUsername", null, locale));
            parameters.put("Report_User", header.username());
            parameters.put("Report_Date_Label", msg.getMessage("form.reportDate", null, locale));
            parameters.put("Report_Date", header.persianDate());
        }
    }

    public static class JasperExcelExporter implements Exporter
    {

        private String fileName;
        private String contentType = "application/vnd.ms-excel";

        public JasperExcelExporter(String fileName)
        {
            this.fileName = fileName;
        }

        @Override
        public String getFileName()
        {
            return fileName;
        }

        @Override
        public JRExporter getJasperExporter()
        {
            JRXlsExporter exporter = new JRXlsExporter();
            JRPropertiesUtil propertiesUtil = exporter.getPropertiesUtil();
            propertiesUtil.setProperty(JRXlsAbstractExporter.PROPERTY_SHEET_DIRECTION, RunDirectionEnum.RTL.getName());
            propertiesUtil.setProperty(JRXlsExporter.PROPERTY_AUTO_FIT_COLUMN, "true");
            propertiesUtil.setProperty(JRXlsExporter.PROPERTY_AUTO_FIT_ROW, "true");
            exporter.setParameter(JRXlsAbstractExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
            exporter.setParameter(JRXlsAbstractExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, Boolean.TRUE);
            exporter.setParameter(JRXlsAbstractExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
            exporter.setParameter(JRXlsAbstractExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
            return exporter;
        }

        @Override
        public String getContentType()
        {
            return contentType;
        }

        @Override
        public String getContentDisposition()
        {
            return "attachment";
        }
    }

    public static class JasperPdfExporter implements Exporter
    {
        private String fileName;
        private String contentType = "application/pdf";

        public JasperPdfExporter(String fileName)
        {
            this.fileName = fileName;
        }

        public String getFileName()
        {
            return fileName;
        }

        public String getContentType()
        {
            return contentType;
        }

        @Override
        public JRExporter getJasperExporter()
        {
            JRPdfExporter exporter = new JRPdfExporter();
            return exporter;
        }

        @Override
        public String getContentDisposition()
        {
            return "attachment";
        }

    }

    public static class JasperPdfInlineExporter extends JasperPdfExporter
    {

        public JasperPdfInlineExporter(String fileName)
        {
            super(fileName);
        }

        @Override
        public String getContentDisposition()
        {
            return "inline";
        }

    }

    public interface Exporter
    {
        public String getFileName();

        public JRExporter getJasperExporter();

        public String getContentType();

        public String getContentDisposition();
    }

    public interface GenerateReportInterceptor
    {
        public void onRunReport(ColumnBuilder columnBuilder, FastReportBuilder frb, ReportItem ri);
    }

    private void processColumns(FastReportBuilder frb)
            throws ColumnBuilderException, ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        if (entityClass == null) 
            entityClass = getEntityClass();
        List<Field> fields = ReflectionUtil.getFields(entityClass);
        List<ReportItem> items = new ArrayList<>();
        for (Field field : fields)
        {
            ReportField rptField = field.getAnnotation(ReportField.class);
            if (rptField != null)
            {
                MessageSource messageSource = SpringUtil.getBean(MessageSource.class);
                String title = messageSource.getMessage(rptField.msg(), null, new Locale("fa", "IR"));
                items.add(new ReportItem(rptField.order(), rptField.interceptor(), rptField.width(), title, field.getName(),
                        field.getType(), rptField.type(), rptField.exportTypes()));
            }
        }
        Accessor ac = new FieldAccessor(entityClass);
        if (BeanAccessor.PROPERTY.equals(accessor(entityClass))) ac = new PropertyAccessor(entityClass);
        List<ReportItem> reportItems = ac.getReportItems(); //
        Collections.sort(reportItems, new ReportItemCompare());
        if (ExportType.XLS.equals(getExportType()) == false) Collections.reverse(reportItems);
        for (ReportItem ri : reportItems)
        {
            List<ExportType> exportTypes = Arrays.asList(ri.getExportTypes());
            if(!exportTypes.contains(getExportType()))
            {
                continue;
            }
            if (FieldType.DATE.equals(ri.getType()) || FieldType.PRIMITIVE.equals(ri.getType()))
            {
                if (String.class.equals(ri.getFieldType())) frb.addColumn(ri.getTitle(), ri.getFieldName(), ri.getFieldType(),
                        ri.getWidth(), ReportStyleManager.INSTACE.cellStyle());
                else if (ri.getFieldType().equals(Date.class) || ri.getFieldType().equals(java.sql.Date.class)
                        || ri.getFieldType().equals(Timestamp.class))
                {
                    DateColumnCustomExpression customExpression = new DateColumnCustomExpression(ri.getFieldName());
                    ColumnBuilder columnBuilder = ColumnBuilder.getNew();
                    columnBuilder.setCustomExpression(customExpression).setTitle(ri.getTitle())
                            .setColumnProperty(ri.getFieldName(), Date.class.getName()).setWidth(ri.getWidth())
                            .setStyle(ReportStyleManager.INSTACE.cellStyle());
                    frb.addColumn(columnBuilder.build());
                }
                else if (ReflectionUtil.isPrimitive(ri.getFieldType()) || ReflectionUtil.isWrapper(ri.getFieldType()))
                {
                    Class primitive = ReflectionUtil.wrapperToPrimitive(ri.getFieldType());
                    if (primitive != null)
                    {
                        ColumnCastToValue expr = new ColumnCastToValue(ri.getFieldName(), primitive);
                        ColumnBuilder columnBuilder = ColumnBuilder.getNew();
                        columnBuilder.setCustomExpression(expr).setTitle(ri.getTitle())
                                .setColumnProperty(ri.getFieldName(), primitive.getName()).setWidth(ri.getWidth())
                                .setStyle(ReportStyleManager.INSTACE.cellStyle());
                        frb.addColumn(columnBuilder.build());
                    }
                }
            }
            else
            {
                List<Class<?>> interceptorList = ri.getInterceptor();
                if (interceptorList != null && interceptorList.size() > 0)
                {
                    for (Class<?> intercept : interceptorList)
                    {
                        if (intercept.equals(void.class) == false)
                        {
                            GenerateReportInterceptor interceptor = (GenerateReportInterceptor) intercept.newInstance();
                            interceptor.onRunReport(ColumnBuilder.getNew(), frb, ri);
                        }
                    }
                }
            }

        }
        // A4 Landscape
        int pageWidth = 595;
        int pageHeigth = 842;
        int totalWidth = 0;
        for (ReportItem reportItem : reportItems)
        {
            totalWidth += reportItem.getWidth();
        }
        Page page = new Page(pageHeigth, totalWidth, false);
        frb.setPageSizeAndOrientation(page);

    }

    private class ColumnCastToValue implements CustomExpression
    {
        private String columnName;
        private Class<?> clz;

        public ColumnCastToValue(String columnName, Class<?> clz)
        {
            this.columnName = columnName;
            this.clz = clz;
        }

        @Override
        public Object evaluate(Map fields, Map variables, Map parameters)
        {
            Object value = fields.get(columnName);
            if (value == null) return null;
            return ReflectionUtil.cast(value, clz);
        }

        @Override
        public String getClassName()
        {
            return clz.getName();
        }

    }

    public BeanAccessor accessor(Class<E> clz)
    {
        ReportAccessor a = clz.getAnnotation(ReportAccessor.class);
        if(a != null && a.accessor() == BeanAccessor.PROPERTY)
            return BeanAccessor.PROPERTY;
        return BeanAccessor.FIELD;
    }

    public enum BeanAccessor {
        FIELD, PROPERTY;
    }

    public interface Accessor
    {
        public List<ReportItem> getReportItems();
    }

    private static class FieldAccessor implements Accessor
    {

        private Class<?> entityClz;

        public FieldAccessor(Class<?> entityClz)
        {
            this.entityClz = entityClz;
        }

        @Override
        public List<ReportItem> getReportItems()
        {
            List<Field> fields = ReflectionUtil.getFields(entityClz);
            List<ReportItem> items = new ArrayList<>();
            for (Field field : fields)
            {
                ReportField rptField = field.getAnnotation(ReportField.class);
                if (rptField != null)
                {
                    MessageSource messageSource = SpringUtil.getBean(MessageSource.class);
                    String title = messageSource.getMessage(rptField.msg(), null, new Locale("fa"));
                    items.add(new ReportItem(rptField.order(), rptField.interceptor(), rptField.width(), title, field.getName(),
                            field.getType(), rptField.type(), rptField.exportTypes()));
                }
            }
            return items;
        }

    }

    private static class PropertyAccessor implements Accessor
    {

        private Class<?> entityClz;

        public PropertyAccessor(Class<?> entityClz)
        {
            this.entityClz = entityClz;
        }

        @Override
        public List<ReportItem> getReportItems()
        {
            List<ReportItem> items = new ArrayList<>();
            PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(entityClz);
            if (descriptors != null)
            {
                for (PropertyDescriptor pd : descriptors)
                {
                    Method method = pd.getReadMethod();
                    ReportField rptField = method.getAnnotation(ReportField.class);
                    if (rptField != null)
                    {
                        MessageSource messageSource = SpringUtil.getBean(MessageSource.class);
                        String title = messageSource.getMessage(rptField.msg(), null, new Locale("fa"));
                        items.add(new ReportItem(rptField.order(), rptField.interceptor(), rptField.width(), title, pd.getName(),
                                method.getReturnType(), rptField.type(), rptField.exportTypes()));
                    }
                }
            }
            return items;
        }

    }

    public static class ReportItemCompare implements Comparator<ReportItem>
    {
        @Override
        public int compare(ReportItem o1, ReportItem o2)
        {
            int x = o1.getOrder();
            int y = o2.getOrder();
            return (x < y) ? -1 : ((x == y) ? 0 : 1);
        }
    }

    private class DateColumnCustomExpression implements CustomExpression
    {
        String dateColumnName;

        public DateColumnCustomExpression(String dateColumnName)
        {
            this.dateColumnName = dateColumnName;
        }

        public Object evaluate(Map fields, Map variables, Map parameters)
        {
            Date gregorianDate = (Date) fields.get(dateColumnName);
            if (gregorianDate == null) return null;
            return PersianDateConverter.getInstance().GregorianToSolar(new SimpleDateFormat("yyyy/MM/dd HH:mm").format(gregorianDate));
        }

        public String getClassName()
        {
            return Date.class.getName();
        }

    }

    protected abstract JasperPaginationHandler getPaginationHandler();

    protected abstract ReportHeader getReportHeader();

    public static class ReportStyleManager
    {

        public static final ReportStyleManager INSTACE = new ReportStyleManager();

        public Style headerStyle()
        {
            Style headerStyle = new Style();
            headerStyle.setName("headerStyle");
            headerStyle.setHorizontalAlign(HorizontalAlign.CENTER);
            Font font = new Font(16, "ZTitr", "ZTitr", "UTF-8", true);
            font.setBold(true);
            headerStyle.setFont(font);
            headerStyle.setBorder(Border.DASHED());
            headerStyle.setVerticalAlign(VerticalAlign.MIDDLE);
            headerStyle.setStretchWithOverflow(true);
            headerStyle.setStreching(Stretching.RELATIVE_TO_BAND_HEIGHT);
            return headerStyle;
        }

        public Style cellStyle()
        {
            Style headerStyle = new Style();
            headerStyle.setName("cellStyle");
            headerStyle.setHorizontalAlign(HorizontalAlign.CENTER);
            Font font = new Font(12, "ZTitr", "ZTitr", "UTF-8", true);
            headerStyle.setFont(font);
            headerStyle.setBorder(Border.THIN());
            headerStyle.setVerticalAlign(VerticalAlign.MIDDLE);
            headerStyle.setStretchWithOverflow(true);
            headerStyle.setStreching(Stretching.RELATIVE_TO_BAND_HEIGHT);
            return headerStyle;
        }

        public Style font()
        {
            Style style = new Style();
            Font font = new Font(12, "ZTitr", "ZTitr", "UTF-8", true);
            style.setFont(font);
            return style;
        }

        public Style oddRowBackgroundStyle()
        {
            Style oddRowBackgroundStyle = new Style();
            oddRowBackgroundStyle.setBackgroundColor(Color.getHSBColor((float) 0.406, (float) 0.207, (float) 0.871));
            return oddRowBackgroundStyle;
        }
    }

    public Class<E> getEntityClass()
    {
        if (entityClass == null)
        {
            Type type = getClass().getGenericSuperclass();
            if (type instanceof ParameterizedType)
            {
                ParameterizedType paramType = (ParameterizedType) type;
                if (paramType.getActualTypeArguments().length == 2)
                {
                    if (paramType.getActualTypeArguments()[1] instanceof TypeVariable)
                    {
                        throw new IllegalArgumentException("Could not guess entity class by reflection");
                    }
                    else
                    {
                        entityClass = (Class<E>) paramType.getActualTypeArguments()[1];
                    }
                }
                else
                {
                    entityClass = (Class<E>) paramType.getActualTypeArguments()[0];
                }
            }
            else
            {
                throw new IllegalArgumentException("Could not guess entity class by reflection");
            }
        }
        return entityClass;
    }

    private static boolean isInternetExplorer(HttpServletRequest request)
    {
        String userAgent = request.getHeader("user-agent");
        return (userAgent.indexOf("MSIE") > -1);
    }

}
