package com.jedlab.framework.audit;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.hibernate.envers.query.criteria.AuditCriterion;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

import com.jedlab.framework.spring.service.AuditService;
import com.jedlab.framework.util.CollectionUtil;
import com.jedlab.framework.util.StringUtil;
import com.jedlab.framework.web.AbstractActionBean;
import com.jedlab.framework.web.ExtendedLazyDataModel;
import com.jedlab.framework.web.ExtendedLazyDataModel.SortProperty;

public abstract class AbstractAuditController<T> extends AbstractActionBean
{

    private static final Logger logger = Logger.getLogger(AbstractAuditController.class.getName());

    private static final String REV_SORT = "revpo.";

    private Date from;

    private Date to;

    private String username;

    private String ipAddress;

    private RevisionType revisionType;

    private LazyDataModel<Revision<T>> resultList;

    private Long resultCount;

    public Object getId()
    {
        return id;
    }

    public void setId(Object id)
    {
        this.id = id;
    }

    private Object id;

    public String getIpAddress()
    {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    public RevisionType getRevisionType()
    {
        return revisionType;
    }

    public void setRevisionType(RevisionType revisionType)
    {
        this.revisionType = revisionType;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public Date getTo()
    {
        return to;
    }

    public void setTo(Date to)
    {
        this.to = to;
    }

    public Date getFrom()
    {
        return from;
    }

    public void setFrom(Date from)
    {
        this.from = from;
    }

    public Class<T> getEntityClass()
    {
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class<T>) parameterizedType.getActualTypeArguments()[0];
    }

    public String restore(T instance)
    {
        if (instance != null)
        {
            getAuditService().restore(instance);
        }
        return null;
    }

    @PostConstruct
    public void init()
    {
        logger.info("init");
        initResultList();
    }
    
    protected void initResultList()
    {
        resultList = new LazyDataModel<Revision<T>>() {

            public List<Revision<T>> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters)
            {
                resultCount = getAuditService().rowCount(filters, getId(), getFrom(), getTo(), getIpAddress(), getUsername(),
                        getRevisionType(), getCriterions());
                if (resultCount == null)
                    return new ArrayList<>();
                setRowCount(resultCount.intValue());
                if (resultCount.intValue() > 0)
                {
                    if (filters != null)
                        filters.values().removeIf(Objects::isNull);
                    List<Revision<T>> load = getAuditService().lazyLoad(first, pageSize,
                            Arrays.asList(new SortProperty(sortField, sortOrder)), filters, getId(), getFrom(), getTo(), getIpAddress(),
                            getUsername(), getRevisionType(), getCriterions());
                    return load;
                }
                return new ArrayList<>();
            }

            public List<Revision<T>> load(int first, int pageSize, List<SortMeta> multiSortMeta, Map<String, Object> filters)
            {
                List<SortProperty> sorts = new ArrayList<ExtendedLazyDataModel.SortProperty>();
                if (multiSortMeta != null)
                {
                    multiSortMeta.forEach(item -> sorts.add(new SortProperty(item.getSortField(), item.getSortOrder())));
                }
                resultCount = getAuditService().rowCount(filters, getId(), getFrom(), getTo(), getIpAddress(), getUsername(),
                        getRevisionType(), getCriterions());
                if (resultCount == null)
                    return new ArrayList<>();
                setRowCount(resultCount.intValue());
                if (resultCount.intValue() > 0)
                {
                    if (filters != null)
                        filters.values().removeIf(Objects::isNull);
                    List<Revision<T>> load = getAuditService().lazyLoad(first, pageSize, sorts, filters, getId(), getFrom(), getTo(),
                            getIpAddress(), getUsername(), getRevisionType(), getCriterions());
                    return load;
                }
                return new ArrayList<>();
            }

        };
    }

    protected List<AuditCriterion> getCriterions()
    {
        return null;
    }

    public LazyDataModel<Revision<T>> getResultList()
    {
        return resultList;
    }

    public Long getResultCount()
    {
        return resultCount;
    }

    protected abstract AuditService<T> getAuditService();

}
