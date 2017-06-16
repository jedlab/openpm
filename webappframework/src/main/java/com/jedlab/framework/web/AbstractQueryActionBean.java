package com.jedlab.framework.web;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.springframework.data.jpa.domain.Specification;

import com.jedlab.framework.db.EntityModel;
import com.jedlab.framework.db.QueryMapper;
import com.jedlab.framework.report.JasperDataExporter;
import com.jedlab.framework.report.JasperPaginationHandler;
import com.jedlab.framework.report.ReportHeader;
import com.jedlab.framework.spring.service.AbstractCrudService;

public abstract class AbstractQueryActionBean<E extends EntityModel> extends AbstractActionBean
{

    private static final Logger logger = Logger.getLogger(AbstractQueryActionBean.class.getName());

    private ExtendedLazyDataModel<E> resultList;
    private Long resultCount;
    private E selectedEntity;
    private ArrayList<E> selectedEntityList = new ArrayList<E>();
    private ReportFileType selectedReportFileTye;
    private Class<E> entityClass;

    public ReportFileType getSelectedReportFileTye()
    {
        return selectedReportFileTye;
    }

    public List<E> getSelectedEntityList()
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
        resultList = new ExtendedLazyDataModel<E>() {

            @Override
            protected List<E> lazyLoad(int first, int pageSize,
                    List<com.jedlab.framework.web.ExtendedLazyDataModel.SortProperty> sortFields, Map<String, Object> filters)
            {
                return getService().load(first, pageSize, sortFields, filters, getQueryFilter(filters));
            }

            @Override
            protected Number rowCount(Map<String, Object> filters)
            {
                resultCount = getService().count(getQueryFilter(filters));
                return resultCount;
            }

        };
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

        public ExportRecords(Class<E> clz)
        {
            this.clz = clz;
        }

        @Override
        protected JasperPaginationHandler getPaginationHandler()
        {
            return new JasperPaginationHandler() {

                @Override
                public Collection<?> getResultList(Integer firstResult, Integer maxResult)
                {
                    return getService().load(firstResult, maxResult, null, null, getQueryFilter(null));
                }

                @Override
                public long getResultCount()
                {
                    return getService().count(getQueryFilter(null));
                }
            };
        }

        @Override
        protected ReportHeader getReportHeader()
        {
            return null;
        }

        @Override
        public Class<E> getEntityClass()
        {
            return clz;
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

    public abstract Specification<E> getQueryFilter(Map<String, Object> dataTableFilterMap);


}
