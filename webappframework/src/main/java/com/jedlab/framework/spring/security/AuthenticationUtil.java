package com.jedlab.framework.spring.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class AuthenticationUtil
{

    public static String getUsername()
    {
        if (SecurityContextHolder.getContext() == null)
        {
            return null;
        }
        if (SecurityContextHolder.getContext().getAuthentication() == null)
        {
            return null;
        }
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public static boolean isAuthenticated()
    {
        if (SecurityContextHolder.getContext() == null || SecurityContextHolder.getContext().getAuthentication() == null)
        {
            return false;
        }
        return true;
    }

    public static boolean isLoggedIn()
    {
        if (isAuthenticated())
            return SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
        else
            return false;

    }

    public static Authentication getAuthentication()
    {
        if (SecurityContextHolder.getContext() == null)
        {
            return null;
        }
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static Long getUserId()
    {
        if (isLoggedIn())
        {
            Authentication auth = getAuthentication();
            Object principal = auth.getPrincipal();
            if (principal instanceof String || principal.equals("anonymousUser"))
                return null;
            SecurityUser applicationUser = ((SecurityUser) principal);
            return applicationUser.getId();
        }
        return null;
    }

    public static SecurityUser getSecurityUser()
    {
        if (isLoggedIn())
        {
            Authentication auth = getAuthentication();
            Object principal = auth.getPrincipal();
            if (principal instanceof String || principal.equals("anonymousUser"))
                return null;
            return (SecurityUser) auth.getPrincipal();
        }
        return null;
    }

}