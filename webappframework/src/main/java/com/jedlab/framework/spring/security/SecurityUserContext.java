package com.jedlab.framework.spring.security;

import java.io.Serializable;

public interface SecurityUserContext
{

    
    public String getUsername();
    public boolean isEnabled();
    public Long getId();
    
    
    
}
