package org.openmrs.module.visits.fragment.controller.clinicianfacing;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.module.appframework.context.AppContextModel;
import org.openmrs.module.appframework.template.TemplateFactory;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.coreapps.CoreAppsProperties;
import org.openmrs.module.coreapps.utils.VisitTypeHelper;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.openmrs.module.visits.web.BaseModuleWebContextSensitiveWithActivatorTest;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;

import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@WebAppConfiguration
public class VisitsSectionFragmentControllerTest extends BaseModuleWebContextSensitiveWithActivatorTest {

    @Mock
    private PageModel pageModel;

    @Mock
    private UiUtils ui;

    @Mock
    private UiSessionContext sessionContext;

    @Mock
    private TemplateFactory templateFactory;

    @Mock
    private CoreAppsProperties coreAppsProperties;

    @Spy
    private PatientDomainWrapper patientWrapper;

    @Mock
    private AdtService adtService;

    @Mock
    private VisitTypeHelper visitTypeHelper;

    @Spy
    private PatientDomainWrapper patientDomainWrapper;

    @Spy
    private FragmentModel model;

    @Mock
    private FragmentConfiguration config;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private VisitsSectionFragmentController visitsSectionFragmentController = new VisitsSectionFragmentController();

    @Before
    public void setUp() {
        Patient patient = new Patient(new Person(1));
        patientDomainWrapper.setPatient(patient);
    }

    @Test
    public void shouldPrepareControllerOfVisitSectionFragment() {
        AppContextModel appContextModel = new AppContextModel();

        doNothing().when(config).require(anyString());
        doReturn(patientDomainWrapper).when(config).get(anyString());
        doReturn(appContextModel).when(sessionContext).generateAppContextModel();
        doReturn(new ArrayList<VisitDomainWrapper>()).when(patientDomainWrapper).getAllVisitsUsingWrappers();

        visitsSectionFragmentController.controller(
                config,
                pageModel,
                model,
                ui,
                sessionContext,
                templateFactory,
                coreAppsProperties,
                patientWrapper,
                adtService,
                visitTypeHelper);

        verify(model).addAttribute(eq("recentVisitsWithLinks"), anyCollection());
        verify(config).addAttribute(eq("showVisitTypeOnPatientHeaderSection"), eq(false));
    }
}
