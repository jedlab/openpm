package com.jedlab.framework.spring.web;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIViewRoot;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.PostConstructViewMapEvent;
import javax.faces.event.PreDestroyViewMapEvent;
import javax.faces.event.SystemEvent;
import javax.faces.event.ViewMapListener;

public class ViewScopeViewMapListener implements ViewMapListener
{

    private final String name;

    private final Runnable callback;

    private boolean callbackCalled = false;

    private final WeakReference<UIViewRoot> uiViewRootWeakReference;

    private final ViewScope viewScope;

    public ViewScopeViewMapListener(UIViewRoot root, String name, Runnable callback, ViewScope viewScope)
    {
        this.name = name;
        this.callback = callback;
        this.uiViewRootWeakReference = new WeakReference<>(root);
        this.viewScope = viewScope;
    }

    public synchronized void doCallback()
    {
        if (!callbackCalled)
        {
            try
            {
                callback.run();
            }
            finally
            {
                callbackCalled = true;
            }
        }
    }

    public String getName()
    {
        return name;
    }

    @Override
    public boolean isListenerForSource(Object source)
    {
        return (source == uiViewRootWeakReference.get());
    }

    @Override
    public void processEvent(SystemEvent event) throws AbortProcessingException
    {
        if (event instanceof PostConstructViewMapEvent)
        {
            PostConstructViewMapEvent viewMapEvent = (PostConstructViewMapEvent) event;
            UIViewRoot viewRoot = (UIViewRoot) viewMapEvent.getComponent();
            viewRoot.getViewMap().put(ViewScope.VIEW_SCOPE_CALLBACKS, new HashMap());
        }
        if (event instanceof PreDestroyViewMapEvent)
        {
            doCallback();
            viewScope.unregisterListener(this);
            //
            PreDestroyViewMapEvent viewMapEvent = (PreDestroyViewMapEvent) event;
            UIViewRoot viewRoot = (UIViewRoot) viewMapEvent.getComponent();
            Map<String, Runnable> callbacks = (Map) viewRoot.getViewMap().get(ViewScope.VIEW_SCOPE_CALLBACKS);
            if (callbacks != null)
            {
                for (Runnable c : callbacks.values())
                {
                    c.run();
                }
                callbacks.clear();
            }
        }
    }

}
