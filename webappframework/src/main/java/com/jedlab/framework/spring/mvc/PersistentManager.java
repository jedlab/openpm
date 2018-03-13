package com.jedlab.framework.spring.mvc;

/**
 * @author omidp
 * <pre>
 * @Repository
@Transactional
public class PersistenceUtil implements PersistentManager
{

    @PersistenceContext
    protected EntityManager em;

    public <E> E findById(Class<E> entityClass, Long primaryKey)
    {
        return em.find(entityClass, primaryKey);
    }

}
 * </pre>
 */
public interface PersistentManager
{

    
    public <E> E findById(Class<E> entityClass, Long primaryKey);
    
}
