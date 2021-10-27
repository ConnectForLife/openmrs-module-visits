package org.openmrs.module.visits.api.validator;

import org.openmrs.api.context.Context;
import org.openmrs.module.visits.api.service.ConfigService;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.AbstractBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

abstract class BaseEncounterDatetimeOverrideValidator implements Validator {
    private final Class<?> validatedClass;

    BaseEncounterDatetimeOverrideValidator(Class<?> validatedClass) {
        this.validatedClass = validatedClass;
    }

    @Override
    public void validate(Object obj, Errors errors) {
        if (!(validatedClass.isInstance(obj))) {
            throw new IllegalArgumentException("The parameter obj should not be null and must be of type " + validatedClass);
        }

        if (isEncounterDatetimeValidationDisabled()) {
            handleDisabledEncounterDatetimeValidation(getFieldErrors(errors));
        }
    }

    abstract void handleDisabledEncounterDatetimeValidation(List<FieldError> fieldErrors);

    private boolean isEncounterDatetimeValidationDisabled() {
        return !getVisitConfigService().isEncounterDatetimeValidationEnabled();
    }

    /**
     * Gets the FieldErrors list from {@code errors}. This comes from private field, any changes to the result of this
     * methods affect the {@code errors}.
     *
     * @param errors the Validation errors
     * @return the FieldError list from {@code errors} or empty list of not found
     */
    private List<FieldError> getFieldErrors(Errors errors) {
        List<FieldError> result = Collections.emptyList();

        if (errors instanceof BindException &&
                ((BindException) errors).getBindingResult() instanceof AbstractBindingResult) {
            final Field errorsPrivateField = ReflectionUtils.findField(AbstractBindingResult.class, "errors");
            errorsPrivateField.setAccessible(true);
            final Object errorsPrivateFieldValue =
                    ReflectionUtils.getField(errorsPrivateField, ((BindException) errors).getBindingResult());

            if (errorsPrivateFieldValue instanceof List && !((List) errorsPrivateFieldValue).isEmpty() &&
                    ((List) errorsPrivateFieldValue).get(0) instanceof FieldError) {
                result = (List<FieldError>) errorsPrivateFieldValue;
            }
        }

        return result;
    }

    private ConfigService getVisitConfigService() {
        return Context.getRegisteredComponent("visits.configService", ConfigService.class);
    }
}
