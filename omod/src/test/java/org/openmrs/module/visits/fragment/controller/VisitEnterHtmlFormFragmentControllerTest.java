package org.openmrs.module.visits.fragment.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.feature.FeatureToggleProperties;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.openmrs.module.visits.web.BaseModuleWebContextSensitiveWithActivatorTest;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

@WebAppConfiguration
public class VisitEnterHtmlFormFragmentControllerTest extends BaseModuleWebContextSensitiveWithActivatorTest {

    private static final String SAMPLE_XML_DATA = "<htmlform />";
    private static final String SAMPLE_URL = "/some/url";
    public static final String SUCCESS_KEY = "success";

    @Mock
    private UiSessionContext sessionContext;

    @Mock
    private HtmlForm htmlForm;

    @Mock
    private AdtService adtService;

    @Mock
    private FeatureToggleProperties featureToggles;

    @Mock
    private UiUtils uiUtils;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    private Encounter encounter;

    private Visit visit;

    private Patient patient;

    private Date currentDate;

    private VisitEnterHtmlFormFragmentController controller = new VisitEnterHtmlFormFragmentController();

    @Before
    public void setUp() {
        currentDate = new Date();
        patient = Context.getPatientService().getPatient(2);
        initializeVisit();
        initializeEncounter();

    }

    @Test
    public void shouldSubmitWhenCreateFlagDisabled() throws Exception {
        doReturn(currentDate).when(htmlForm).getDateChanged();
        doReturn(SAMPLE_XML_DATA).when(htmlForm).getXmlData();
        doReturn(session).when(request).getSession();
        doNothing().when(session).setAttribute(any(), any());

        SimpleObject result = controller.submit(
                sessionContext,
                patient,
                htmlForm,
                encounter,
                visit,
                false,
                SAMPLE_URL,
                adtService,
                featureToggles,
                uiUtils,
                request
        );
        assertTrue((boolean) result.get(SUCCESS_KEY));
    }

    @Test
    public void shouldSubmitWhenCreateFlagEnabled() throws Exception {
        doReturn(currentDate).when(htmlForm).getDateChanged();
        doReturn(SAMPLE_XML_DATA).when(htmlForm).getXmlData();
        doReturn(session).when(request).getSession();
        doNothing().when(session).setAttribute(any(), any());

        SimpleObject result = controller.submit(
                sessionContext,
                patient,
                htmlForm,
                encounter,
                visit,
                true,
                SAMPLE_URL,
                adtService,
                featureToggles,
                uiUtils,
                request
        );
        assertTrue((boolean) result.get(SUCCESS_KEY));
    }

    @Test
    public void shouldSubmitWhenCreateFlagEnabled2() throws Exception {
        doReturn(currentDate).when(htmlForm).getDateChanged();
        doReturn(SAMPLE_XML_DATA).when(htmlForm).getXmlData();
        doReturn(session).when(request).getSession();
        doNothing().when(session).setAttribute(any(), any());

        SimpleObject result = controller.submit(
                sessionContext,
                null,
                htmlForm,
                encounter,
                visit,
                true,
                SAMPLE_URL,
                adtService,
                featureToggles,
                uiUtils,
                request
        );

        assertFalse((boolean) result.get(SUCCESS_KEY));
    }

    private void initializeEncounter() {
        encounter = new Encounter(1);
        encounter.setDateChanged(currentDate);
        encounter.setDateCreated(currentDate);
        encounter.setEncounterDatetime(currentDate);
        encounter.setEncounterType(new EncounterType(1));
        encounter.setPatient(patient);
        encounter.setVisit(visit);
    }

    private void initializeVisit() {
        visit = new Visit();
        visit.setPatient(patient);
        visit.setStartDatetime(currentDate);
        visit.setVisitType(new VisitType(1));
    }
}
