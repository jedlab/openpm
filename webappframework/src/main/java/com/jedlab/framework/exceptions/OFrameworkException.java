package com.jedlab.framework.exceptions;

/**
 * @author omidp
 *
 */
public class OFrameworkException extends RuntimeException
{

    public OFrameworkException()
    {
        super();
    }

    public OFrameworkException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public OFrameworkException(String message)
    {
        super(message);
    }

    public OFrameworkException(Throwable cause)
    {
        super(cause);
    }

}
