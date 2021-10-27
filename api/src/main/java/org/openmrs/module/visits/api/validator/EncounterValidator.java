/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.api.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.annotation.Handler;
import org.springframework.validation.FieldError;

import java.util.List;

/**
 * Validator for {@link Encounter} class.
 * This validator is used to disable the validation of encounter datetime (provided by the OpenMRS core validator).
 * The order of the OpenMRS core validator is equaled to 50 that's why this validator has an order set as 100
 * to be executed after default one.
 * Introduced in order to extend the default logic without changing the OpenMRS core platform.
 */
@Handler(supports = {Encounter.class}, order = 100)
public class EncounterValidator extends BaseEncounterDatetimeOverrideValidator {

    private static final Log LOGGER = LogFactory.getLog(org.openmrs.validator.EncounterValidator.class);

    private static final String ENCOUNTER_DATETIME_FIELD = "encounterDatetime";

    public EncounterValidator() {
        super(Encounter.class);
    }

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

    @Override
    void handleDisabledEncounterDatetimeValidation(List<FieldError> fieldErrors) {
        LOGGER.debug("The encounter datetime validation is disabled.");
        fieldErrors.removeIf(error -> error.getField().equals(ENCOUNTER_DATETIME_FIELD));
    }
}
