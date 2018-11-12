package com.jedlab.framework.spring.validation;

import java.util.List;

import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

public class BindingValidationError extends Exception
{

    private List<ObjectError> errors;

    public BindingValidationError(List<ObjectError> errors)
    {
        this.errors = errors;
    }

    public List<ObjectError> getErrors()
    {
        return errors;
    }

}
