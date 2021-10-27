package org.openmrs.module.visits.api.validator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.Visit;
import org.openmrs.api.ValidationException;
import org.openmrs.api.context.Context;
import org.openmrs.module.visits.api.util.GlobalPropertiesConstants;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.validator.ValidateUtil;
import org.springframework.validation.FieldError;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class VisitValidatorTest extends BaseModuleContextSensitiveTest {
    private static final String TEST_VISIT_TYPE = "9768c389-6c3a-47c0-0004-000000000001";
    private static final String TEST_PATIENT = "9768c389-6c3a-47c0-0002-000000000001";
    private static final String TEST_ENCOUNTER_TYPE = "9768c389-6c3a-47c0-0005-000000000001";
    private static final ZonedDateTime TEST_TIMESTAMP = ZonedDateTime.of(2021, 10, 27, 15, 30, 0, 0, ZoneId.systemDefault());

    @Before
    public void prepareTestEnv() throws Exception {
        executeDataSet("datasets/VisitValidatorTest.xml");
    }

    @Test
    public void visitStartDatetimeShouldBeValidated() {
        // given
        Context
                .getAdministrationService()
                .setGlobalProperty(GlobalPropertiesConstants.ENCOUNTER_DATETIME_VALIDATION.getKey(), "true");

        final Visit visit = prepareVisit();
        Context.getVisitService().saveVisit(visit);

        final Encounter encounter = prepareEncounter(TEST_TIMESTAMP.plusHours(1));
        encounter.setVisit(visit);
        Context.getEncounterService().saveEncounter(encounter);

        // then
        try {
            encounter.setEncounterDatetime(Date.from(TEST_TIMESTAMP.minusHours(1).toInstant()));
            ValidateUtil.validate(visit);
            Assert.fail("ValidateUtil.validate should throw ValidationException.");
        } catch (ValidationException ve) {
            final FieldError startDatetimeError = ve.getErrors().getFieldError("startDatetime");
            if (ve.getErrors().getAllErrors().size() == 1 && startDatetimeError != null &&
                    startDatetimeError.getCode().equals("Visit.encountersCannotBeBeforeStartDate")) {
                // expected
                return;
            }

            // not expected, re-throw
            throw ve;
        }
    }

    @Test
    public void visitStartDatetimeShouldNotValidatedWhenEncounterDatetimeValidationIsDisabled() {
        // given
        Context
                .getAdministrationService()
                .setGlobalProperty(GlobalPropertiesConstants.ENCOUNTER_DATETIME_VALIDATION.getKey(), "false");

        final Visit visit = prepareVisit();
        Context.getVisitService().saveVisit(visit);

        final Encounter encounter = prepareEncounter(TEST_TIMESTAMP.minusHours(1));
        encounter.setVisit(visit);
        Context.getEncounterService().saveEncounter(encounter);

        // then
        ValidateUtil.validate(visit);
    }

    private static Visit prepareVisit() {
        final Visit visit = new Visit();
        visit.setPatient(Context.getPatientService().getPatientByUuid(TEST_PATIENT));
        visit.setVisitType(Context.getVisitService().getVisitTypeByUuid(TEST_VISIT_TYPE));
        visit.setStartDatetime(Date.from(TEST_TIMESTAMP.toInstant()));
        return visit;
    }

    private static Encounter prepareEncounter(ZonedDateTime encounterDateTime) {
        final Encounter encounter = new Encounter();
        encounter.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(TEST_ENCOUNTER_TYPE));
        encounter.setPatient(Context.getPatientService().getPatientByUuid(TEST_PATIENT));
        encounter.setEncounterDatetime(Date.from(encounterDateTime.toInstant()));
        return encounter;
    }
}
