package com.jedlab.framework.report;

import java.util.Collection;

/**
 * @author OmidPourhadi [AT] gmail [DOT] com
 *
 */
public interface JasperPaginationHandler
{

    public Collection<?> getResultList(Integer firstResult, Integer maxResult);

    public long getResultCount();
    
}
