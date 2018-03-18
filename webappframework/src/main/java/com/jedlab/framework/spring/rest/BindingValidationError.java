package com.jedlab.framework.spring.rest;

import java.util.List;

import org.springframework.validation.FieldError;

public class BindingValidationError extends Exception
{

    private List<FieldError> fieldErrors;

    public BindingValidationError(List<FieldError> fieldErrors)
    {
        this.fieldErrors = fieldErrors;
    }

    public List<FieldError> getFieldErrors()
    {
        return fieldErrors;
    }

}
