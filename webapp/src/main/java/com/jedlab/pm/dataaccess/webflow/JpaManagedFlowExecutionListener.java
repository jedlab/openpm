package com.jedlab.pm.dataaccess.webflow;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.Session;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.webflow.execution.FlowSession;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.persistence.JpaFlowExecutionListener;

/**
 * @author omidp
 *
 */
public class JpaManagedFlowExecutionListener extends JpaFlowExecutionListener
{

    public JpaManagedFlowExecutionListener(EntityManagerFactory entityManagerFactory, PlatformTransactionManager transactionManager)
    {
        super(entityManagerFactory, transactionManager);
    }
    
    @Override
    public void paused(RequestContext context)
    {
        super.paused(context);
        Object commit = context.getCurrentState().getAttributes().get("commit");
        if(commit != null)
        {
            EntityManager em = getEntityManager(context.getFlowExecutionContext().getActiveSession());
            if(em != null)
            {
                Session session = em.unwrap(Session.class);
                if(session != null)
                {
                    if(session.isConnected())
                        session.disconnect();
                }
            }
        }
    }

    private EntityManager getEntityManager(FlowSession session) {
        return (EntityManager) session.getScope().get(PERSISTENCE_CONTEXT_ATTRIBUTE);
    }
    
}
