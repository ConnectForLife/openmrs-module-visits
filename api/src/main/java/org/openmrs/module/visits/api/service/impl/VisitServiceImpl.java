/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.api.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.visits.api.dto.VisitDTO;
import org.openmrs.module.visits.api.exception.ValidationException;
import org.openmrs.module.visits.api.mapper.VisitMapper;
import org.openmrs.module.visits.api.service.ConfigService;
import org.openmrs.module.visits.api.service.MissedVisitService;
import org.openmrs.module.visits.api.service.VisitService;
import org.openmrs.module.visits.domain.PagingInfo;
import org.openmrs.module.visits.domain.criteria.OverviewCriteria;
import org.openmrs.module.visits.domain.criteria.VisitCriteria;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implements methods for creating, reading, updating and deleting Visit entities
 */
public class VisitServiceImpl extends BaseOpenmrsDataService<Visit> implements VisitService {

    private static final Log LOGGER = LogFactory.getLog(VisitServiceImpl.class);

    private PatientService patientService;

    private LocationService locationService;

    private VisitMapper visitMapper;

    private ConfigService configService;

    @Override
    public List<Visit> getVisitsForPatient(String patientUuid, PagingInfo pagingInfo) {
        Patient patient = patientService.getPatientByUuid(patientUuid);
        if (patient == null) {
            throw new ValidationException(String.format("Patient with uuid %s does not exist", patientUuid));
        }
        return findAllByCriteria(new VisitCriteria(patient), pagingInfo);
    }

    @Override
    public void updateVisit(String visitUuid, VisitDTO visitDTO) {
        saveOrUpdate(visitMapper.fromDto(visitDTO.setUuid(visitUuid)));
    }

    @Override
    public void createVisit(VisitDTO visitDTO) {
        try {
            VisitDTO clone = (VisitDTO) visitDTO.clone();
            clone.setStatus(configService.getVisitInitialStatus());
            if (clone.getStartDate() == null) {
                clone.setStartDate(new Date());
            }

            saveOrUpdate(visitMapper.fromDto(clone));
        } catch (CloneNotSupportedException e) {
            LOGGER.error(e);
            throw new IllegalArgumentException("Unable to clone passed visit", e);
        }
    }

    @Override
    public void changeStatusForMissedVisits() {
        List<String> statusesEndingVisit = configService.getStatusesEndingVisit();

        List<String> eligibleStatusesToMarkVisitAsMissed = configService.getVisitStatuses()
                .stream()
                .filter(status -> !statusesEndingVisit.contains(status))
                .collect(Collectors.toList());

        List<Integer> missedVisitsIds = findAllByCriteria(VisitCriteria.forMissedVisits(
                configService.getMinimumVisitDelayToMarkItAsMissed(),
                configService.getStatusOfMissedVisit(),
                statusesEndingVisit)).stream().map(Visit::getVisitId).collect(Collectors.toList());

        MissedVisitService missedVisitService = Context.getService(MissedVisitService.class);
        missedVisitsIds.forEach(visitId -> missedVisitService
                .changeVisitStatusToMissed(visitId, eligibleStatusesToMarkVisitAsMissed));
    }

    public void setVisitMapper(VisitMapper visitMapper) {
        this.visitMapper = visitMapper;
    }

    @Override
    public List<Visit> getVisitsForLocation(String locationUuid, PagingInfo pagingInfo, String query,
                                                  String visitStatus, Long dateFrom, Long dateTo, String timePeriod) {
        Location location = locationService.getLocationByUuid(locationUuid);
        if (location == null) {
            throw new ValidationException(String.format("Location with uuid %s does not exist",
                    locationUuid));
        }
        return findAllByCriteria(new OverviewCriteria(location, query, visitStatus, dateFrom, dateTo, timePeriod),
                pagingInfo);
    }

    public VisitServiceImpl setPatientService(PatientService patientService) {
        this.patientService = patientService;
        return this;
    }

    public void setLocationService(LocationService locationService) {
        this.locationService = locationService;
    }

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }
}
