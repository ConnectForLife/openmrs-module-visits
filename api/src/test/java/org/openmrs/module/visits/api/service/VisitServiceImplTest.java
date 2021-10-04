package org.openmrs.module.visits.api.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.Visit;
import org.openmrs.VisitAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.visits.ContextMockedTest;
import org.openmrs.module.visits.api.dao.BaseOpenmrsPageableDao;
import org.openmrs.module.visits.api.dao.impl.VisitDaoImpl;
import org.openmrs.module.visits.api.dto.VisitDTO;
import org.openmrs.module.visits.api.service.impl.VisitServiceImpl;
import org.openmrs.module.visits.domain.PagingInfo;
import org.openmrs.module.visits.domain.criteria.VisitCriteria;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class VisitServiceImplTest extends ContextMockedTest {

    @Spy
    private BaseOpenmrsPageableDao<Visit> dao = new VisitDaoImpl();

    @InjectMocks
    private VisitService visitService = new VisitServiceImpl();

    private Patient patient;

    private Visit visit;

    private VisitDTO visitDTO;

    private Location location;

    @Before
    public void setUp() {
        super.setUp();
        patient = new Patient(1);
        patient.setUuid("123-456-789");
        visit = new Visit(1);
        visitDTO = new VisitDTO();
        visitDTO.setUuid("123-456-789");
        location = new Location(1);
    }

    @Test
    public void getVisitsForPatient() {
        doReturn(patient).when(getPatientService()).getPatientByUuid(patient.getUuid());
        doReturn(Collections.emptyList()).when(dao).findAllByCriteria(any(VisitCriteria.class), any(PagingInfo.class));

        visitService.getVisitsForPatient(patient.getUuid(), new PagingInfo());
        verify(dao, times(1)).findAllByCriteria(any(VisitCriteria.class), any(PagingInfo.class));
    }

    @Test
    public void updateVisit() {
        doReturn(visit).when(getVisitMapper()).fromDto(visitDTO);
        doReturn(visit).when(dao).saveOrUpdate(visit);

        visitService.updateVisit(visitDTO.getUuid(), visitDTO);
        verify(dao, times(1)).saveOrUpdate(any(Visit.class));
    }

    @Test
    public void createVisit() {
        doReturn(visit).when(getVisitMapper()).fromDto(visitDTO);
        doReturn(visit).when(dao).saveOrUpdate(any(Visit.class));
        doReturn("SCHEDULED").when(getConfigService()).getVisitInitialStatus();

        visitService.createVisit(visitDTO);
        verify(dao, times(1)).saveOrUpdate(any(Visit.class));
    }

    @Test
    public void changeStatusForMissedVisits() {
        doReturn(Collections.singletonList(visit))
                .when(dao).findAllByCriteria(any(VisitCriteria.class), any());
        doReturn("MISSED")
                .when(getConfigService()).getStatusOfMissedVisit();
        doReturn(new VisitAttributeType())
                .when(getVisitService()).getVisitAttributeTypeByUuid(anyString());
        doReturn(visit)
                .when(dao).saveOrUpdate(any(Visit.class));
        when(Context.getAuthenticatedUser()).thenReturn(new User(1));

        visitService.changeStatusForMissedVisits();
        verify(getMissedVisitService()).changeVisitStatusToMissed(anyInt(), anyListOf(String.class));
    }

    @Test
    public void getVisitsForLocation() {
        doReturn(Collections.singletonList(visit))
                .when(dao).findAllByCriteria(any(VisitCriteria.class), any());
        doReturn(location)
                .when(getLocationService()).getLocationByUuid(location.getUuid());

        List<Visit> visitsForLocation = visitService.getVisitsForLocation(location.getUuid(), new PagingInfo(),
                null, null, null, null, null);
        assertThat(visitsForLocation, org.hamcrest.Matchers.contains(visit));
    }
}
