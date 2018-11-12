package com.jedlab.framework.spring.validation;

public class ErrorMessage
{

    private int code;
    private String message;
    private String field;
    
    

    public ErrorMessage(int code, String message)
    {
        this.code = code;
        this.message = message;
    }

    public ErrorMessage(int code, String message, String field)
    {
        this.code = code;
        this.message = message;
        this.field = field;
    }

    public int getCode()
    {
        return code;
    }

    public String getMessage()
    {
        return message;
    }

    public String getField()
    {
        return field;
    }

}
