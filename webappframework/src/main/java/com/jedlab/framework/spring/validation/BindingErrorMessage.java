package com.jedlab.framework.spring.validation;

import java.util.ArrayList;

/**
 * @author omidp
 *
 */
public class BindingErrorMessage
{

    ArrayList<ErrorMessage> errors;

    public BindingErrorMessage()
    {
        this.errors = new ArrayList<>(0);
    }

    public void addFieldError(String field, String localizedError)
    {
        this.errors.add(new ErrorMessage(0, localizedError,field));
    }

    public ArrayList<ErrorMessage> getErrors()
    {
        return errors;
    }
    
    

}
