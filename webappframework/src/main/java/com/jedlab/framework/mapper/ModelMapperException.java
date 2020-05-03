package com.jedlab.framework.mapper;

public class ModelMapperException extends RuntimeException
{

    public ModelMapperException()
    {
        super();
    }

    public ModelMapperException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ModelMapperException(String message)
    {
        super(message);
    }

    public ModelMapperException(Throwable cause)
    {
        super(cause);
    }

}