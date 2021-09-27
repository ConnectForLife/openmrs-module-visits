package org.openmrs.module.visits.api.mapper;

import org.apache.commons.lang.NotImplementedException;
import org.openmrs.Visit;
import org.openmrs.module.visits.api.decorator.VisitDecorator;
import org.openmrs.module.visits.api.dto.NameUrlDTO;
import org.openmrs.module.visits.api.dto.OverviewDTO;

/**
 * Maps objects between the related visit overview types
 */
public final class OverviewMapper extends AbstractMapper<OverviewDTO, Visit> {

    private static final String URL = "/openmrs/coreapps/clinicianfacing/patient.page?patientId=";

    @Override
    public OverviewDTO toDto(Visit visit) {
        VisitDecorator visitDecorator = new VisitDecorator(visit);
        NameUrlDTO name = new NameUrlDTO(visit.getPatient().getPersonName().getFullName(),
                URL + visit.getPatient().getUuid());
        return new OverviewDTO()
                .setUuid(visit.getUuid())
                .setPatientIdentifier(visit.getPatient().getPatientIdentifier().getIdentifier())
                .setNameUrl(name)
                .setStartDate(visit.getStartDatetime())
                .setActualDate(visitDecorator.getActualDate())
                .setTime(visitDecorator.getTime())
                .setType(visit.getVisitType() == null ? null : visit.getVisitType().getName())
                .setStatus(visitDecorator.getStatus())
                .setLocation(visit.getLocation() == null ? null : visit.getLocation().getName());
    }

    @Override
    public Visit fromDto(OverviewDTO dto) {
        throw new NotImplementedException("Mapping from dto not supported");
    }
}
