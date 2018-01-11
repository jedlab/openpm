package com.jedlab.framework.audit;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.hibernate.envers.RevisionListener;

import com.jedlab.framework.spring.security.AuthenticationUtil;
import com.jedlab.framework.spring.security.SecurityUserContext;

public class RevPoListener implements RevisionListener
{

    @Override
    public void newRevision(Object revisionEntity)
    {
        RevisionPO rev = (RevisionPO) revisionEntity;
        SecurityUserContext revUser = AuthenticationUtil.getSecurityUser();
        if (revUser != null)
            rev.setUsername(revUser.getUsername());
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null)
        {
            HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
            String ipAddress = request.getHeader("X-FORWARDED-FOR");
            if (ipAddress == null)
            {
                ipAddress = request.getRemoteAddr();
            }
            rev.setIpAddress(ipAddress);
        }
    }

}
