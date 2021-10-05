package org.openmrs.module.visits;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.Daemon;
import org.openmrs.module.visits.api.mapper.VisitMapper;
import org.openmrs.module.visits.api.service.ConfigService;
import org.openmrs.module.visits.api.service.MissedVisitService;
import org.openmrs.scheduler.SchedulerService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class, Daemon.class})
public abstract class ContextMockedTest extends BaseTest {

    @Mock
    private AdministrationService administrationService;

    @Mock
    private VisitMapper visitMapper;

    @Mock
    private PatientService patientService;

    @Mock
    private ConfigService configService;

    @Mock
    private VisitService visitService;

    @Mock
    private LocationService locationService;

    @Mock
    private SchedulerService schedulerService;

    @Mock
    private MissedVisitService missedVisitService;

    @Before
    public void setUp() {
        mockStatic(Context.class);
        mockStatic(Daemon.class);
        when(Context.getAdministrationService()).thenReturn(administrationService);
        when(Context.getPatientService()).thenReturn(patientService);
        when(Context.getRegisteredComponent("visits.visitMapper", VisitMapper.class)).thenReturn(visitMapper);
        when(Context.getRegisteredComponent("visits.configService", ConfigService.class)).thenReturn(configService);
        when(Context.getVisitService()).thenReturn(visitService);
        when(Context.getLocationService()).thenReturn(locationService);
        when(Context.getSchedulerService()).thenReturn(schedulerService);
        when(Context.getService(MissedVisitService.class)).thenReturn(missedVisitService);
    }

    public AdministrationService getAdministrationService() {
        return administrationService;
    }

    public ConfigService getConfigService() {
        return configService;
    }

    public VisitMapper getVisitMapper() {
        return visitMapper;
    }

    public PatientService getPatientService() {
        return patientService;
    }

    public VisitService getVisitService() {
        return visitService;
    }

    public LocationService getLocationService() {
        return locationService;
    }

    public SchedulerService getSchedulerService() {
        return schedulerService;
    }

    public MissedVisitService getMissedVisitService() {
        return missedVisitService;
    }
}
