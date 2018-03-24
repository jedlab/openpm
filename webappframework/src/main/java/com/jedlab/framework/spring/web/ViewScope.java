package com.jedlab.framework.spring.web;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.PreDestroyViewMapEvent;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.web.context.request.FacesRequestAttributes;

/**
 * @author omidp
 * <p>http://blog.harezmi.com.tr/uncategorized/spring-view-scope-for-jsf-2-users/</p>
 * <p>https://github.com/javaplugs/spring-jsf/</p>
 */
public class ViewScope implements Scope, Serializable, HttpSessionBindingListener
{

    private static final long serialVersionUID = 1L;

    private final WeakHashMap<HttpSession, Set<ViewScopeViewMapListener>> sessionToListeners = new WeakHashMap<>();

    public static final String VIEW_SCOPE_CALLBACKS = "viewScope.callbacks";

    @Override
    public Object get(String name, ObjectFactory objectFactory)
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if(facesContext == null)
            return objectFactory.getObject();
        if(facesContext.getViewRoot() == null)
            return objectFactory.getObject();
        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
        // noinspection SynchronizationOnLocalVariableOrMethodParameter
        if (viewMap.containsKey(name))
        {
            return viewMap.get(name);
        }
        else
        {
            synchronized (viewMap)
            {
                if (viewMap.containsKey(name))
                {
                    return viewMap.get(name);
                }
                else
                {
                    Object object = objectFactory.getObject();
                    viewMap.put(name, object);
                    return object;
                }
            }
        }
    }

    @Override
    public String getConversationId()
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        FacesRequestAttributes facesRequestAttributes = new FacesRequestAttributes(facesContext);
        return facesRequestAttributes.getSessionId() + "-" + facesContext.getViewRoot().getViewId();
    }

    /**
     * Removing bean from scope and unregister it destruction callback without
     * executing them.
     *
     * @see Scope for more details
     */
    @Override
    public Object remove(String name)
    {
        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
        if (viewMap.containsKey(name))
        {
            Object removed;
            synchronized (viewMap)
            {
                if (viewMap.containsKey(name))
                {
                    removed = FacesContext.getCurrentInstance().getViewRoot().getViewMap().remove(name);
                    if (removed != null)
                    {
                        Map callbacks = (Map) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get(VIEW_SCOPE_CALLBACKS);
                        if (callbacks != null)
                        {
                            callbacks.remove(name);
                        }
                    }
                }
                else
                {
                    return null;
                }
            }

            HttpSession httpSession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
            Set<ViewScopeViewMapListener> sessionListeners;
            sessionListeners = sessionToListeners.get(httpSession);
            if (sessionListeners != null)
            {
                Set<ViewScopeViewMapListener> toRemove = new HashSet<>();
                for (ViewScopeViewMapListener listener : sessionListeners)
                {
                    if (listener.getName().equals(name))
                    {
                        toRemove.add(listener);
                        FacesContext.getCurrentInstance().getViewRoot().unsubscribeFromViewEvent(PreDestroyViewMapEvent.class, listener);
                    }
                }
                synchronized (sessionListeners)
                {
                    sessionListeners.removeAll(toRemove);
                }
            }

            return removed;
        }
        return null;
    }

    /**
     * Register callback to be executed only on whole scope destroying (not
     * single object).
     *
     * @see Scope for more details
     */
    @Override
    public void registerDestructionCallback(String name, Runnable callback)
    {
        Map callbacks = (Map) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get(VIEW_SCOPE_CALLBACKS);
        if (callbacks != null)
        {
            callbacks.put(name, callback);
        }
        UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();
        ViewScopeViewMapListener listener = new ViewScopeViewMapListener(viewRoot, name, callback, this);

        viewRoot.subscribeToViewEvent(PreDestroyViewMapEvent.class, listener);

        HttpSession httpSession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);

        final Set<ViewScopeViewMapListener> sessionListeners;

        if (sessionToListeners.containsKey(httpSession))
        {
            sessionListeners = sessionToListeners.get(httpSession);
        }
        else
        {
            synchronized (sessionToListeners)
            {
                if (sessionToListeners.containsKey(httpSession))
                {
                    sessionListeners = sessionToListeners.get(httpSession);
                }
                else
                {
                    sessionListeners = new HashSet<>();
                    sessionToListeners.put(httpSession, sessionListeners);
                }
            }
        }

        synchronized (sessionListeners)
        {
            sessionListeners.add(listener);
        }

        if (!FacesContext.getCurrentInstance().getExternalContext().getSessionMap().containsKey("sessionBindingListener"))
        {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("sessionBindingListener", this);
        }

    }

    @Override
    public Object resolveContextualObject(String key)
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        FacesRequestAttributes facesRequestAttributes = new FacesRequestAttributes(facesContext);
        return facesRequestAttributes.resolveReference(key);
    }

    @Override
    public void valueBound(HttpSessionBindingEvent event)
    {
    }

    /**
     * Seems like it called after our listeners was unbounded from http session.
     * Looks like view scope it destroyed. But should we call callback or not is
     * a big question.
     *
     * @see HttpSessionBindingListener for more details
     */
    @Override
    public void valueUnbound(HttpSessionBindingEvent event)
    {
        final Set<ViewScopeViewMapListener> listeners;
        synchronized (sessionToListeners)
        {
            if (sessionToListeners.containsKey(event.getSession()))
            {
                listeners = sessionToListeners.get(event.getSession());
                sessionToListeners.remove(event.getSession());
            }
            else
            {
                listeners = null;
            }
        }
        if (listeners != null)
        {
            // I just hope that JSF context already done this job
            for (ViewScopeViewMapListener listener : listeners)
            {
                // As long as our callbacks can run only once - this is not such
                // big deal
                listener.doCallback();
            }
        }
    }

    /**
     * Will remove listener from session set and unregister it from UIViewRoot.
     */
    public void unregisterListener(ViewScopeViewMapListener listener)
    {
        HttpSession httpSession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        FacesContext.getCurrentInstance().getViewRoot().unsubscribeFromViewEvent(PreDestroyViewMapEvent.class, listener);
        if (httpSession != null)
        {
            synchronized (sessionToListeners)
            {
                if (sessionToListeners.containsKey(httpSession))
                {
                    sessionToListeners.get(httpSession).remove(listener);
                }
            }
        }
    }

    private Map getViewMap()
    {
        return FacesContext.getCurrentInstance().getViewRoot().getViewMap();
    }
}