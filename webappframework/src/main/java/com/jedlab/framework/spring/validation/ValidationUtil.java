package com.jedlab.framework.spring.validation;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.jedlab.framework.util.CollectionUtil;
import com.jedlab.framework.util.StringUtil;

public class ValidationUtil
{

    private MessageSource messageSource;

    @Autowired
    public ValidationUtil(MessageSource messageSource)
    {
        this.messageSource = messageSource;
    }

    public void processGlobalErrors(List<ObjectError> errors, BindingErrorMessage bem)
    {
        List<ObjectError> fieldErrors = errors.stream().filter(item -> item instanceof ObjectError).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(fieldErrors))
        {
            Locale current = LocaleContextHolder.getLocale();
            for (ObjectError fieldError : fieldErrors)
            {
                String code = fieldError.getCode();
                String defaultMessage = fieldError.getDefaultMessage();

                String localizedError = messageSource.getMessage(code, fieldError.getArguments(), current);
                if (localizedError != null && !localizedError.equals(code))
                {
                    bem.addFieldError(fieldError.getObjectName(), localizedError);
                }
                else
                {
                    bem.addFieldError(fieldError.getObjectName(),
                            StringUtil.isEmpty(defaultMessage) ? messageSource.getMessage(code, null, current) : defaultMessage);
                }
            }
        }
    }

    public void processFieldErrors(List<ObjectError> errors, BindingErrorMessage dto)
    {
        Locale current = LocaleContextHolder.getLocale();
        List<FieldError> fieldErrors = errors.stream().filter(item -> item instanceof FieldError).map(item -> (FieldError) item)
                .collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(fieldErrors))
        {
            for (FieldError fieldError : fieldErrors)
            {
                String code = fieldError.getCode();
                String defaultMessage = fieldError.getDefaultMessage();

                String localizedError = messageSource.getMessage(code, fieldError.getArguments(), current);
                if (localizedError != null && !localizedError.equals(code))
                {
                    dto.addFieldError(fieldError.getField(), localizedError);
                }
                else
                {
                    dto.addFieldError(fieldError.getObjectName(),
                            StringUtil.isEmpty(defaultMessage) ? messageSource.getMessage(code, null, current) : defaultMessage);
                }
            }
        }
    }

    public BindingErrorMessage invokeValidator(Validator validator, Object instance)
    {
        BindException bindException = new BindException(instance, instance.getClass().getSimpleName());

        ValidationUtils.invokeValidator(validator, instance, bindException);

        BindingValidationError bindingValidationError = new BindingValidationError(bindException.getAllErrors());
        BindingErrorMessage bem = new BindingErrorMessage();

        List<ObjectError> errors = bindingValidationError.getErrors();
        processFieldErrors(errors, bem);
        processGlobalErrors(errors, bem);
        return bem;
    }

}
