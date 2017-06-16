package com.jedlab.framework.exceptions;

/**
 * @author omidp
 *
 */
public class ServiceException extends RuntimeException
{

    public ServiceException()
    {
        super();
    }

    public ServiceException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ServiceException(String message)
    {
        super(message);
    }

    public ServiceException(Throwable cause)
    {
        super(cause);
    }

}
