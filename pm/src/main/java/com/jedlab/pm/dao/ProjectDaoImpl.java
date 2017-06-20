package com.jedlab.pm.dao;

import javax.persistence.NoResultException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.jedlab.pm.model.Project;

/**
 * @author omidp
 *
 */
public class ProjectDaoImpl implements ProjectDaoCustom
{

    @PersistenceContext
    EntityManager entityManager;

    public Project findProjectByName(String projectName)
    {
        try
        {
            return (Project) entityManager.createNamedQuery(Project.FIND_BY_NAME).setParameter("name", projectName).setMaxResults(1)
                    .getSingleResult();
        }
        catch (NoResultException e)
        {
            return null;
        }
    }

    public Project findProjectByNameAndId(String name, Long id)
    {
        try
        {
            return (Project) entityManager.createQuery("select p from Project p where p.id <> :id and p.name = :name")
                    .setParameter("id", id)
                    .setParameter("name", name)
                    .setMaxResults(1)
                    .getSingleResult();
        }
        catch (NoResultException e)
        {
        }
        return null;
    }

}
