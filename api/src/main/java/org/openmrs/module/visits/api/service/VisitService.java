/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.api.service;

import org.openmrs.Visit;
import org.openmrs.module.visits.api.dto.VisitDTO;
import org.openmrs.module.visits.domain.PagingInfo;

import java.util.List;

/** Provides methods for creating, reading, updating and deleting Visit entities */
public interface VisitService extends BaseOpenmrsCriteriaDataService<Visit> {

  /**
   * Finds paginated collection of the visits for the given patient
   *
   * @param patientUuid uuid of the patient
   * @param pagingInfo properties of the pagination
   * @return list of the patient's visits, implicitly paginated
   */
  List<Visit> getVisitsForPatient(String patientUuid, PagingInfo pagingInfo);

  /**
   * Finds paginated collection of the visits for the given location, optionally filtered by a
   * different options
   *
   * @param locationUuid uuid of the location
   * @param pagingInfo properties of the pagination
   * @param query used for searching visits by patient identifier or patient name
   * @param visitStatus used for filtering visits by visit status
   * @param dateFrom used for filtering visits where planned date of visit is greater or equals than
   *     dateFrom
   * @param dateTo used for filtering visit where planned date of visit is less or equals than
   *     dateTo
   * @param timePeriod used for filtering visits depending on value from {@link
   *     org.openmrs.module.visits.api.model.TimePeriod}. If value is not provided, default value =
   *     TODAY is used.
   * @return list of the location's visits, implicitly paginated
   */
  List<Visit> getVisitsForLocation(
      String locationUuid,
      PagingInfo pagingInfo,
      String query,
      String visitStatus,
      Long dateFrom,
      Long dateTo,
      String timePeriod);

  /**
   * Updates a visit
   *
   * @param visitUuid visit uuid
   * @param visitDTO visit DTO object
   */
  void updateVisit(String visitUuid, VisitDTO visitDTO);

  /**
   * Changes statuses of eligible visits to missed
   *
   * <p>This method is executed when MissedVisitStatusChanger job is run. Eligible visits for
   * changing status are collected and then statues are changed. Because of efficiency visits are
   * processed in batches
   */
  void changeStatusForMissedVisits();

  /**
   * Creates a visit
   *
   * @param visit visit DTO object
   */
  void createVisit(VisitDTO visit);
}
