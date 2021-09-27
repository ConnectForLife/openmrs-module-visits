/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.api.validator;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.annotation.Handler;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.visits.api.service.ConfigService;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.AbstractBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;

/**
 * Validator for {@link Encounter} class.
 * This validator is used to disable the validation of encounter datetime (provided by the OpenMRS core validator).
 * The order of the OpenMRS core validator is equaled to 50 that's why this validator has an order set as 100
 * to be executed after default one.
 * Introduced in order to extend the default logic without changing the OpenMRS core platform.
 */
@Handler(supports = { Encounter.class }, order = 100)
public class EncounterValidator implements Validator {

    private static final Log LOGGER = LogFactory.getLog(org.openmrs.validator.EncounterValidator.class);

    private static final String ENCOUNTER_DATETIME_FIELD = "encounterDatetime";

    /**
     * Returns whether or not this validator supports validating a given class.
     *
     * @param c The class to check for support.
     * @see org.springframework.validation.Validator#supports(java.lang.Class)
     */
    @Override
    public boolean supports(Class<?> c) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(this.getClass().getName() + ".supports: " + c.getName());
        }
        return Encounter.class.isAssignableFrom(c);
    }

    /**
     * Validates the given Encounter. Currently checks if encounter datetime validation is enabled.
     * If validation is disabled then ensure that all possible errors will be removed.
     *
     * @param obj The encounter to validate.
     * @param errors Errors
     * @see org.springframework.validation.Validator#validate(java.lang.Object,
     *      org.springframework.validation.Errors)
     */
    @Override
    public void validate(Object obj, Errors errors) throws APIException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(this.getClass().getName() + ".validate...");
        }

        if (!(obj instanceof Encounter)) {
            throw new IllegalArgumentException("The parameter obj should not be null and must be of type "
                    + Encounter.class);
        }

        if (isEncounterDatetimeValidationDisabled()) {
            LOGGER.debug("The encounter datetime validation is disabled.");
            disableEncounterDatetimeValidation(errors);
        }
    }

    private boolean isEncounterDatetimeValidationDisabled() {
        return !getVisitConfigService().isEncounterDatetimeValidationEnabled();
    }

    private ConfigService getVisitConfigService() {
        return Context.getRegisteredComponent("visits.configService", ConfigService.class);
    }

    private void disableEncounterDatetimeValidation(Errors errors) {
        if (errors instanceof BindException
                && ((BindException) errors).getBindingResult() instanceof AbstractBindingResult) {
            Field field = ReflectionUtils.findField(AbstractBindingResult.class, "errors");
            field.setAccessible(true);
            Object fieldValue = ReflectionUtils.getField(field, ((BindException) errors).getBindingResult());
            removeEncounterDatetimeErrors(fieldValue);
        }
    }

    private void removeEncounterDatetimeErrors(Object fieldValue) {
        if (fieldValue instanceof List
                && !((List) fieldValue).isEmpty()
                && ((List) fieldValue).get(0) instanceof FieldError) {
            List<FieldError> errorList = (List<FieldError>) fieldValue;
            Iterator<FieldError> i = errorList.iterator();
            while (i.hasNext()) {
                FieldError error = i.next();
                if (error.getField().equals(ENCOUNTER_DATETIME_FIELD)) {
                    i.remove();
                }
            }
        }
    }
}
