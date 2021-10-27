package org.openmrs.module.visits.api.validator;

import org.openmrs.Encounter;
import org.openmrs.Visit;
import org.openmrs.annotation.Handler;
import org.springframework.validation.FieldError;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Validator for {@link Encounter} class.
 * <p>
 * This validator is used to disable the validation of encounter datetime (provided by the OpenMRS core validator) during
 * Visit validation.
 * <p>
 * The order of the OpenMRS core validator is equaled to 50 that's why this validator has an order set as 100
 * to be executed after default one.
 * <p>
 * Introduced in order to extend the default logic without changing the OpenMRS core platform.
 */
@Handler(supports = {Visit.class}, order = 100)
public class CflVisitValidator extends BaseEncounterDatetimeOverrideValidator {

    private static final String VISIT_START_DATETIME_FIELD = "startDatetime";
    private static final String VISIT_STOP_DATETIME_FIELD = "stopDatetime";
    private static final Set<String> DISABLE_VALIDATION_FOR_FIELDS =
            new HashSet<>(Arrays.asList(VISIT_START_DATETIME_FIELD, VISIT_STOP_DATETIME_FIELD));

    public CflVisitValidator() {
        super(Visit.class);
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return Visit.class.isAssignableFrom(aClass);
    }

    @Override
    void handleDisabledEncounterDatetimeValidation(List<FieldError> fieldErrors) {
        fieldErrors.removeIf(error -> DISABLE_VALIDATION_FOR_FIELDS.contains(error.getField()));
    }
}
