package org.openmrs.module.visits.api.model;

import org.junit.Test;
import org.mockito.Mockito;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.module.visits.BaseTest;
import org.openmrs.module.visits.api.dto.VisitFormUrisMap;
import org.openmrs.module.visits.builder.PatientBuilder;
import org.openmrs.module.visits.builder.VisitBuilder;
import org.openmrs.module.visits.builder.VisitTypeBuilder;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.openmrs.module.visits.api.util.VisitsConstants.CREATE_URI_NAME;
import static org.openmrs.module.visits.api.util.VisitsConstants.EDIT_URI_NAME;

public class VisitFormUrisMapTest extends BaseTest {
    private static final String VISIT_TYPE_1_NAME = "Follow-up";
    private static final String VISIT_TYPE_1_UUID = UUID.randomUUID().toString();

    private static final String INVALID_URI = "&&  @@@";

    private static final String VISIT_TYPE_1_CREATE_URI = "/a";
    private static final String VISIT_TYPE_1_EDIT_URI = "/b";
    private static final String DEFAULT_VISIT_TYPE_CREATE_URI = "/d";
    private static final String DEFAULT_VISIT_TYPE_EDIT_URI = "/e";

    private static final String EMPTY_STRING = "";
    private static final String INVALID_JSON = "{";
    private static final String PATIENT_UUID = "62f4ada0-8a14-4130-bae6-5ffef175f46b";

    @Test
    public void shouldParseTypeWith2Uris() {
        Visit visit = new VisitBuilder()
                .withVisitType(new VisitTypeBuilder().withName(VISIT_TYPE_1_NAME).build())
                .withPatient(new PatientBuilder().withUuid(PATIENT_UUID).build())
                .build();
        String input = getInputForType1With2Uri();
        VisitFormUrisMap actual = new VisitFormUrisMap(input);
        assertThat(actual.getUri(visit), is(VISIT_TYPE_1_CREATE_URI));
    }

    @Test
    public void shouldParseTypeWith1Uris() {
        Visit visit = new VisitBuilder()
                .withVisitType(new VisitTypeBuilder().withName(VISIT_TYPE_1_NAME).build())
                .withPatient(new PatientBuilder().withUuid(PATIENT_UUID).build())
                .build();
        String input = getInputForType1With1Uri();
        VisitFormUrisMap actual = new VisitFormUrisMap(input);
        assertThat(actual.getUri(visit), is(nullValue()));
    }

    @Test
    public void shouldParseJsonWithDefaultUri() {
        String input = getInputForDefaultType2With2Uris();
        VisitFormUrisMap actual = new VisitFormUrisMap(input);
        Visit visit = new VisitBuilder()
                .withVisitType(new VisitTypeBuilder().withName(VISIT_TYPE_1_NAME).build())
                .withPatient(new PatientBuilder().withUuid(PATIENT_UUID).build())
                .build();
        assertThat(actual.getUri(visit), is(DEFAULT_VISIT_TYPE_CREATE_URI));
    }

    @Test
    public void shouldParseJsonWithNoUris() {
        Visit visit = new VisitBuilder()
                .withVisitType(new VisitTypeBuilder().withName(VISIT_TYPE_1_NAME).build())
                .withPatient(new PatientBuilder().withUuid(PATIENT_UUID).build())
                .build();
        String input = getInputWithoutUris();
        VisitFormUrisMap actual = new VisitFormUrisMap(input);
        assertThat(actual.getUri(visit), is(nullValue()));
    }

    @Test
    public void shouldParseEmptyInput() {
        Visit visit = new VisitBuilder()
            .withVisitType(new VisitTypeBuilder().withName(VISIT_TYPE_1_NAME).build())
            .withPatient(new PatientBuilder().withUuid(PATIENT_UUID).build())
            .build();
        VisitFormUrisMap actual = new VisitFormUrisMap(EMPTY_STRING);
        assertThat(actual.getUri(visit), is(nullValue()));
    }

    @Test
    public void shouldNoThrowExceptionIfJsonIsInvalid() {
        Visit visit = new VisitBuilder()
                .withVisitType(new VisitTypeBuilder().withName(VISIT_TYPE_1_NAME).build())
                .withPatient(new PatientBuilder().withUuid(PATIENT_UUID).build())
                .build();
        VisitFormUrisMap actual = new VisitFormUrisMap(INVALID_JSON);
        assertThat(actual.getUri(visit), is(nullValue()));
    }

    @Test
    public void shouldReturnNullUriIfEmptyMap() {
        VisitFormUrisMap visitFormUrisMap = new VisitFormUrisMap(EMPTY_STRING);

        String actualForCreation = visitFormUrisMap.getUri(createVisit(VISIT_TYPE_1_NAME, VISIT_TYPE_1_UUID,  true));
        String actualForEdit = visitFormUrisMap.getUri(createVisit(VISIT_TYPE_1_NAME, VISIT_TYPE_1_UUID,  false));

        assertThat(actualForCreation, is(nullValue()));
        assertThat(actualForEdit, is(nullValue()));
    }

    @Test
    public void shouldReturnNullUriIfUrlIsInvalidEvenThoughThereAreDefaultUris() {
        String input = getInputForType1WithInvalidUrisAndForValidDefaultUris();
        VisitFormUrisMap visitFormUrisMap = new VisitFormUrisMap(input);

        String actualForCreation = visitFormUrisMap.getUri(createVisit(VISIT_TYPE_1_NAME, VISIT_TYPE_1_UUID, true));
        String actualForEdit = visitFormUrisMap.getUri(createVisit(VISIT_TYPE_1_NAME, VISIT_TYPE_1_UUID, false));

        assertThat(actualForCreation, is(nullValue()));
        assertThat(actualForEdit, is(nullValue()));
    }

    @Test
    public void shouldReturnDefaultUriWhenSpecifiedUrlsAreNotSet() {
        String input = getInputForDefaultType2With2Uris();
        VisitFormUrisMap visitFormUrisMap = new VisitFormUrisMap(input);

        String actualForCreation = visitFormUrisMap.getUri(createVisit(VISIT_TYPE_1_NAME, VISIT_TYPE_1_UUID, true));
        String actualForEdit = visitFormUrisMap.getUri(createVisit(VISIT_TYPE_1_NAME, VISIT_TYPE_1_UUID, false));

        assertEquals(DEFAULT_VISIT_TYPE_CREATE_URI, actualForCreation);
        assertEquals(DEFAULT_VISIT_TYPE_EDIT_URI, actualForEdit);
    }

    @Test
    public void shouldReturnSpecifiedUriByTypeName() {
        String input = getInputForType1With2Uri();
        VisitFormUrisMap visitFormUrisMap = new VisitFormUrisMap(input);

        String actualForCreation = visitFormUrisMap.getUri(createVisit(VISIT_TYPE_1_NAME, VISIT_TYPE_1_UUID, true));
        String actualForEdit = visitFormUrisMap.getUri(createVisit(VISIT_TYPE_1_NAME, VISIT_TYPE_1_UUID, false));

        assertEquals(VISIT_TYPE_1_CREATE_URI, actualForCreation);
        assertEquals(VISIT_TYPE_1_EDIT_URI, actualForEdit);
    }

    @Test
    public void shouldReturnSpecifiedUriBasedOnTypeUuid() {
        String input = getInputForType1With2UriBasedOnTypeUuid();
        VisitFormUrisMap visitFormUrisMap = new VisitFormUrisMap(input);

        String actualForCreation = visitFormUrisMap.getUri(createVisit(VISIT_TYPE_1_NAME, VISIT_TYPE_1_UUID, true));
        String actualForEdit = visitFormUrisMap.getUri(createVisit(VISIT_TYPE_1_NAME, VISIT_TYPE_1_UUID, false));

        assertEquals(VISIT_TYPE_1_CREATE_URI, actualForCreation);
        assertEquals(VISIT_TYPE_1_EDIT_URI, actualForEdit);
    }

    private String getInputForType1With2Uri() {
        return String.format("{'%s':{"
                            + "'%s':'%s',"
                            + "'%s':'%s'}}",
                VISIT_TYPE_1_NAME,
                CREATE_URI_NAME, VISIT_TYPE_1_CREATE_URI,
                EDIT_URI_NAME, VISIT_TYPE_1_EDIT_URI);
    }

    private String getInputForType1With2UriBasedOnTypeUuid() {
        return String.format("{'%s':{"
                        + "'%s':'%s',"
                        + "'%s':'%s'}}",
                VISIT_TYPE_1_UUID,
                CREATE_URI_NAME, VISIT_TYPE_1_CREATE_URI,
                EDIT_URI_NAME, VISIT_TYPE_1_EDIT_URI);
    }

    private String getInputForType1With1Uri() {
        return String.format("{'%s':{"
                        + "'%s':'%s'}}",
                VISIT_TYPE_1_NAME,
                EDIT_URI_NAME, VISIT_TYPE_1_EDIT_URI);
    }

    private String getInputForDefaultType2With2Uris() {
        return String.format("{'%s':{"
                        + "'%s':'%s',"
                        + "'%s':'%s'}}",
                VisitFormUrisMap.DEFAULT_VISIT_FORM_URIS_KEY,
                CREATE_URI_NAME, DEFAULT_VISIT_TYPE_CREATE_URI,
                EDIT_URI_NAME, DEFAULT_VISIT_TYPE_EDIT_URI);
    }

    private String getInputForType1WithInvalidUrisAndForValidDefaultUris() {
        return String.format("{'%s':{"
                        + "'%s':'%s',"
                        + "'%s':'%s'},"
                        + "'%s':{"
                        + "'%s':'%s',"
                        + "'%s':'%s'}"
                        + "}",
                VisitFormUrisMap.DEFAULT_VISIT_FORM_URIS_KEY,
                CREATE_URI_NAME, DEFAULT_VISIT_TYPE_CREATE_URI,
                EDIT_URI_NAME, DEFAULT_VISIT_TYPE_EDIT_URI,
                VISIT_TYPE_1_NAME,
                CREATE_URI_NAME, INVALID_URI,
                EDIT_URI_NAME, INVALID_URI);
    }

    private String getInputWithoutUris() {
        return "{}";
    }

    private Visit createVisit(String typeName, String typeUuid, boolean isFormCreation) {
        List<Encounter> encountersList = Mockito.mock(List.class);
        VisitType visitType = Mockito.mock(VisitType.class);
        Visit visit = Mockito.mock(Visit.class);
        Patient patient = Mockito.mock(Patient.class);

        when(visit.getVisitType()).thenReturn(visitType);
        when(visit.getPatient()).thenReturn(patient);
        when(visit.getNonVoidedEncounters()).thenReturn(encountersList);

        when(visitType.getName()).thenReturn(typeName);
        when(visitType.getUuid()).thenReturn(typeUuid);
        when(patient.getUuid()).thenReturn(UUID.randomUUID().toString());
        when(encountersList.isEmpty()).thenReturn(isFormCreation);

        return visit;
    }
}
