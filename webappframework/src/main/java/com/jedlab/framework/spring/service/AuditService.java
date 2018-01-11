package com.jedlab.framework.spring.service;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.hibernate.envers.query.criteria.AuditCriterion;
import org.springframework.transaction.annotation.Transactional;

import com.jedlab.framework.audit.Revision;
import com.jedlab.framework.audit.RevisionPO;
import com.jedlab.framework.util.CollectionUtil;
import com.jedlab.framework.util.StringUtil;
import com.jedlab.framework.web.ExtendedLazyDataModel.SortProperty;

public abstract class AuditService<T>
{

    @PersistenceContext(unitName = "entityManagerFactory")
    protected EntityManager entityManager;

    
    

    @Transactional
    public List<Revision<T>> lazyLoad(int first, int pageSize, List<SortProperty> sortFields, Map<String, Object> filters, Object id, Date from, Date to, String ipAddress, String username, RevisionType rt,
            List<AuditCriterion> criterions)
    {
        AuditReader auditReader = AuditReaderFactory.get(getEntityManager());
        AuditQuery auditQuery = auditReader.createQuery().forRevisionsOfEntity(getEntityClass(), false, true);
        AuditQuery query = createQuery(auditQuery, from,to,ipAddress,username, rt,criterions);
        
        // show versioning
        if (id != null)
        {
            // List<Number> revisions =
            // getAuditReader().getRevisions(getEntityClass(), getId());
            query.add(AuditEntity.id().eq(id));
        }
        query.setFirstResult(first);
        query.setMaxResults(pageSize);
        List resultList = query.getResultList();
        final List<Object[]> queryResult = (List<Object[]>) resultList;
        final List<Revision<T>> result = new ArrayList<Revision<T>>(resultList.size());
        for (final Object[] array : queryResult)
        {
            T instance = (T) array[0];
            RevisionPO revpo = (RevisionPO) array[1];
            RevisionType revType = (RevisionType) array[2];
            result.add(new Revision<T>(instance, revpo, revType));
        }
        return result;
    }

    @Transactional
    public Long rowCount(Map<String, Object> filters, Object id, Date from, Date to, String ipAddress, String username, RevisionType rt,
            List<AuditCriterion> criterions)
    {
        AuditReader auditReader = AuditReaderFactory.get(getEntityManager());
        AuditQuery auditQuery = auditReader.createQuery().forRevisionsOfEntity(getEntityClass(), false, true);
        AuditQuery query = createQuery(auditQuery, from,to,ipAddress,username, rt,criterions);
        if (id != null)
        {
            query.add(AuditEntity.id().eq(id));
        }
        return (Long) query.addProjection(AuditEntity.revisionNumber().count()).getSingleResult();
    }

    private AuditQuery createQuery(AuditQuery query, Date from, Date to, String ipAddress, String username, RevisionType rt,
            List<AuditCriterion> criterions)
    {
        if (from != null)
        {
            query.add(AuditEntity.revisionProperty("timestamp").ge(from.getTime()));
        }
        if (to != null)
        {
            query.add(AuditEntity.revisionProperty("timestamp").lt(to.getTime()));
        }
        if (StringUtil.isNotEmpty(ipAddress))
        {
            query.add(AuditEntity.revisionProperty("ipAddress").eq(ipAddress));
        }
        if (StringUtil.isNotEmpty(username))
        {
            query.add(AuditEntity.revisionProperty("username").eq(username));
        }
        if (rt != null)
        {
            query.add(AuditEntity.revisionType().eq(rt));
        }
        if (CollectionUtil.isNotEmpty(criterions))
        {
            for (AuditCriterion ac : criterions)
            {
                query.add(ac);
            }
        }

        return query;
    }

   

    @Transactional
    public String restore(T instance)
    {
        if (instance != null)
        {
            getEntityManager().merge(instance);
            getEntityManager().flush();
        }
        return null;
    }

    public Class<T> getEntityClass()
    {
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class<T>) parameterizedType.getActualTypeArguments()[0];
    }
    
    
    protected EntityManager getEntityManager()
    {
        return entityManager;
    }
}
