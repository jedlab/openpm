package com.jedlab.framework.audit;

import java.util.Date;

/**
 * @author Omid Pourhadi
 *
 */
public interface RevisionPO
{

    public String getUsername();

    public void setUsername(String username);

    public String getIpAddress();

    public void setIpAddress(String ipAddress);
    
    public Date getRevisionDate();
    
    public int getId();

}
