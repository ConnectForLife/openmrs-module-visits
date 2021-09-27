package org.openmrs.module.visits.api.mapper;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.Visit;
import org.openmrs.VisitAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.visits.ContextMockedTest;
import org.openmrs.module.visits.api.dto.VisitDTO;
import org.openmrs.module.visits.api.dto.VisitDetailsDTO;
import org.openmrs.module.visits.api.dto.VisitFormUrisMap;
import org.openmrs.module.visits.builder.PatientBuilder;
import org.openmrs.module.visits.builder.VisitBuilder;
import org.openmrs.module.visits.builder.VisitDTOBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.openmrs.module.visits.api.util.TestConstants.VISIT_URI_MAP_JSON;

public class VisitMapperTest extends ContextMockedTest {

    @InjectMocks
    private VisitMapper visitMapper = new VisitMapper();

    @Test
    public void shouldMapToDto() {
        doReturn(new VisitFormUrisMap(VISIT_URI_MAP_JSON))
                .when(getConfigService()).getVisitFormUrisMap();

        Visit visit = new VisitBuilder().build();
        VisitDTO visitDTO = visitMapper.toDto(visit);

        assertEquals(visit.getUuid(), visitDTO.getUuid());
        assertEquals(visit.getStartDatetime(), visitDTO.getStartDate());
        assertEquals(visit.getPatient().getUuid(), visitDTO.getPatientUuid());
    }

    @Test
    public void shouldMapToDtos() {
        doReturn(new VisitFormUrisMap(VISIT_URI_MAP_JSON))
                .when(getConfigService()).getVisitFormUrisMap();

        Visit visit = new VisitBuilder().build();
        VisitDTO visitDTO = visitMapper.toDtos(Collections.singletonList(visit)).get(0);

        assertEquals(visit.getUuid(), visitDTO.getUuid());
        assertEquals(visit.getStartDatetime(), visitDTO.getStartDate());
        assertEquals(visit.getPatient().getUuid(), visitDTO.getPatientUuid());
    }

    @Test
    public void shouldMapFromDtoWhenVisitIsNew() {
        final Patient patient = new PatientBuilder().build();
        doReturn(new VisitAttributeType())
                .when(getVisitService()).getVisitAttributeTypeByUuid(anyString());
        doReturn(patient)
                .when(getPatientService()).getPatientByUuid(any());
        VisitDTO visitDTO = new VisitDTOBuilder().build();
        Visit visit = visitMapper.fromDto(visitDTO);

        assertEquals(visitDTO.getStartDate(), visit.getStartDatetime());
        assertEquals(patient.getUuid(), visit.getPatient().getUuid());
    }

    @Test
    public void shouldMapFromDtoWhenVisitAlreadyExists() {
        final Patient patient = new PatientBuilder().build();
        doReturn(new VisitAttributeType())
                .when(getVisitService()).getVisitAttributeTypeByUuid(anyString());
        doReturn(patient)
                .when(getPatientService()).getPatientByUuid(any());
        Visit testVisit = new Visit(1);
        when(getVisitService().getVisitByUuid(anyString())).thenReturn(testVisit);
        when(Context.getAuthenticatedUser()).thenReturn(new User(1));

        assertNull(testVisit.getDateChanged());
        assertNull(testVisit.getChangedBy());

        VisitDTO visitDTO = new VisitDTOBuilder().build();
        Visit visit = visitMapper.fromDto(visitDTO);

        assertEquals(visitDTO.getStartDate(), visit.getStartDatetime());
        assertEquals(patient.getUuid(), visit.getPatient().getUuid());
        assertNotNull(testVisit.getDateChanged());
        assertNotNull(testVisit.getChangedBy());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionForNoEncounters() {
        final Patient patient = new PatientBuilder().build();
        doReturn(new VisitAttributeType())
                .when(getVisitService()).getVisitAttributeTypeByUuid(anyString());
        doReturn(patient).when(getPatientService()).getPatientByUuid(any());

        VisitDTO visitDTO = new VisitDTOBuilder()
                .withActualDate(new Date())
                .build();
        visitMapper.fromDto(visitDTO);
    }

    @Test
    public void shouldMapFromDtos() {
        final Patient patient = new PatientBuilder().build();
        doReturn(new VisitAttributeType())
                .when(getVisitService()).getVisitAttributeTypeByUuid(anyString());
        doReturn(patient).when(getPatientService()).getPatientByUuid(any());

        VisitDTO visitDTO = new VisitDTOBuilder().build();
        Visit visit = visitMapper.fromDtos(Collections.singletonList(visitDTO)).get(0);

        assertEquals(visitDTO.getStartDate(), visit.getStartDatetime());
        assertEquals(patient.getUuid(), visit.getPatient().getUuid());
    }

    @Test
    public void shouldMapToDtoWithDetails() {
        doReturn(new VisitFormUrisMap(VISIT_URI_MAP_JSON))
                .when(getConfigService()).getVisitFormUrisMap();

        Visit visit = new VisitBuilder().build();
        VisitDetailsDTO visitDTO = visitMapper.toDtoWithDetails(visit);

        assertEquals(visit.getUuid(), visitDTO.getUuid());
        assertEquals(visit.getStartDatetime(), visitDTO.getStartDate());
        assertEquals(visit.getPatient().getUuid(), visitDTO.getPatientUuid());
        assertEquals(visit.getLocation().getName(), visitDTO.getLocationName());
        assertEquals(visit.getVisitType().getName(), visitDTO.getTypeName());
    }

    @Test
    public void shouldMapToDtosWithDetails() {
        doReturn(new VisitFormUrisMap(VISIT_URI_MAP_JSON))
                .when(getConfigService()).getVisitFormUrisMap();

        VisitBuilder vb = new VisitBuilder();
        List<VisitDetailsDTO> dtos = visitMapper.toDtosWithDetails(Arrays.asList(
                vb.build(),
                vb.build(),
                vb.build()
        ));

        Visit sampleVisit = new VisitBuilder().build();
        for (VisitDetailsDTO dto : dtos) {
            assertEquals(sampleVisit.getLocation().getName(), dto.getLocationName());
            assertEquals(sampleVisit.getVisitType().getName(), dto.getTypeName());
        }
    }
}
