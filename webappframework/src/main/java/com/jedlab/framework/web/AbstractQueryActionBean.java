package com.jedlab.framework.web;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

import com.jedlab.framework.db.EntityModel;
import com.jedlab.framework.report.JasperDataExporter;
import com.jedlab.framework.report.JasperPaginationHandler;
import com.jedlab.framework.report.ReportHeader;
import com.jedlab.framework.spring.service.AbstractCrudService;
import com.jedlab.framework.spring.service.JPARestriction;
import com.jedlab.framework.util.CollectionUtil;

import ar.com.fdvs.dj.domain.builders.FastReportBuilder;

/**
 * @author omidp
 *         <p>
 *         load list on page load
 *         </p>
 * @param <E>
 */
public abstract class AbstractQueryActionBean<E extends EntityModel> extends AbstractActionBean
{

    private static final Logger logger = Logger.getLogger(AbstractQueryActionBean.class.getName());

    private ExtendedLazyDataModel<E> resultList;
    private Long resultCount;
    private E selectedEntity;
    private Set<E> selectedEntityList = new HashSet<E>();
    private ReportFileType selectedReportFileTye;
    private Class<E> entityClass;

    public ReportFileType getSelectedReportFileTye()
    {
        return selectedReportFileTye;
    }

    public Set<E> getSelectedEntityList()
    {
        return selectedEntityList;
    }

    public void setSelectedEntityList(ArrayList<E> selectedEntityList)
    {
        // DO NOTHING
    }

    public void onRowSelectByCheckbox(SelectEvent event)
    {
        E selectedCompte = (E) event.getObject();
        getSelectedEntityList().add(selectedCompte);
    }

    public void onRowUnSelectByCheckbox(UnselectEvent event)
    {
        E unselectedCompte = (E) event.getObject();
        getSelectedEntityList().remove(unselectedCompte);
    }

    public void setSelectedReportFileTye(ReportFileType selectedReportFileTye)
    {
        this.selectedReportFileTye = selectedReportFileTye;
    }

    public abstract AbstractCrudService<E> getService();

    public E getSelectedEntity()
    {
        return selectedEntity;
    }

    public void setSelectedEntity(E selectedEntity)
    {
        this.selectedEntity = selectedEntity;
    }

    public void load()
    {
        logger.info("load");
    }

    @PostConstruct
    public void init()
    {
        logger.info("init");
        getEntityClass();
        initResultList();
    }

    protected void initResultList()
    {
        resultList = new ExtendedLazyDataModel<E>() {

            @Override
            protected List<E> lazyLoad(int first, int pageSize,
                    List<com.jedlab.framework.web.ExtendedLazyDataModel.SortProperty> sortFields, Map<String, Object> filters)
            {
                if (CollectionUtil.isEmpty(sortFields))
                    sortFields = getSortProperties();
                return getService().load(first, pageSize, sortFields, filters, getEntityClass(), getRestriction());
            }

            @Override
            protected Number rowCount(Map<String, Object> filters)
            {
                resultCount = getService().count(getEntityClass(), getRestriction());
                return resultCount;
            }

        };
    }

    protected List<com.jedlab.framework.web.ExtendedLazyDataModel.SortProperty> getSortProperties()
    {
        return null;
    }

    public ExtendedLazyDataModel<E> getResultList()
    {
        return resultList;
    }

    public Long getResultCount()
    {
        return resultCount;
    }

    public void export()
    {
        if (ReportFileType.XLS.equals(getSelectedReportFileTye()))
        {
            exportAsXls();
        }

        if (ReportFileType.INLINEPDF.equals(getSelectedReportFileTye()))
        {
            exportAsInlinePdf();
        }

        if (ReportFileType.PDF.equals(getSelectedReportFileTye()))
        {
            exportAsPdf();
        }
    }

    public void exportAsPdf()
    {
        new ExportRecords(getEntityClass()).exportByType("PDF");
    }

    public void exportAsXls()
    {
        new ExportRecords(getEntityClass()).exportByType("XLS");
    }

    public void exportAsInlinePdf()
    {
        new ExportRecords(getEntityClass()).exportByType("INLINEPDF");
    }

    private class ExportRecords extends JasperDataExporter<E>
    {

        private Class<E> clz;
        private ReportSection reportSection;

        public ExportRecords(Class<E> clz)
        {
            this.clz = clz;
            this.reportSection = getReportSection();
        }

        @Override
        protected JasperPaginationHandler getPaginationHandler()
        {
            return new JasperPaginationHandler() {

                @Override
                public Collection<?> getResultList(Integer firstResult, Integer maxResult)
                {
                    return getService().load(firstResult, maxResult, getSortProperties(), null, clz, getRestriction());
                }

                @Override
                public long getResultCount()
                {
                    return getService().count(clz, getRestriction());
                }
            };
        }

        @Override
        protected void beforeGenerateReport(FastReportBuilder frb)
        {
            if (this.reportSection != null)
            {
                this.reportSection.beforeFormGenerateReport(frb);
                Map<String, Object> parameters = this.reportSection.getParameters();
                if (parameters != null && parameters.isEmpty() == false)
                {
                    parameters.entrySet().forEach(item -> {
                        addParameter(item.getKey(), item.getValue());
                    });
                }
            }
        }

        @Override
        protected ReportHeader getReportHeader()
        {
            if (this.reportSection != null)
                return this.reportSection.getFormReportHeader();
            return null;
        }

        @Override
        public Class<E> getEntityClass()
        {
            return clz;
        }

    }

    public interface ReportSection
    {

        public void beforeFormGenerateReport(FastReportBuilder frb);

        public ReportHeader getFormReportHeader();

        public Map<String, Object> getParameters();
    }

    protected ReportSection getReportSection()
    {
        return null;
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

    protected JPARestriction getRestriction()
    {
        return null;
    }

    private PaginationHelper<E> pagination;

    public PaginationHelper<E> getPagination()
    {

        if (pagination == null)
        {

            pagination = new PaginationHelper<E>(getListPageSize()) {
                @Override
                public int getItemsCount()
                {
                    if(resultCount == null)
                        resultCount = getService().count(getEntityClass(), getRestriction());
                    return resultCount.intValue();
                }

                @Override
                public List<E> createPageDataModel()
                {
                    if(entityResultList == null)
                        entityResultList = getService().load(getPageFirstItem(), getPageSize(), getSortProperties(), null, getEntityClass(),
                            getRestriction());
                    return entityResultList;
                }
            };
        }
        return pagination;
    }


    protected int getListPageSize()
    {
        return 10;
    }

    private List<E> entityResultList;

    public List<E> getEntityResultList()
    {
        if (entityResultList == null)
            entityResultList = getPagination().createPageDataModel();
        return entityResultList;
    }

    public String next()
    {
        getPagination().nextPage();
        recreateModel();
        return null;
    }

    public String last()
    {
        getPagination().lastPage();
        recreateModel();
        return null;
    }

    public String first()
    {
        getPagination().firstPage();
        recreateModel();
        return null;
    }

    protected void recreateModel()
    {
        entityResultList = null;
        resultCount = null;
    }

    public String previous()
    {
        getPagination().previousPage();
        recreateModel();
        return null;
    }

}
