package com.jedlab.pm.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

/**
 * @author omidp
 *
 */
public class AuthLogoutSuccessHandler extends AbstractAuthenticationTargetUrlRequestHandler implements LogoutSuccessHandler
{

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException
    {
        HttpSession session = request.getSession(false);
        if(session != null)
        {
            session.removeAttribute(AuthSuccessHandler.CURRENT_USERNAME);
            session.removeAttribute(AuthSuccessHandler.CURRENT_USERID);
        }
        super.handle(request, response, authentication);
    }

}
