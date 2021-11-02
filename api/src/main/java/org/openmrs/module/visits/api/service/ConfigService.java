package org.openmrs.module.visits.api.service;

import org.openmrs.module.visits.api.dto.VisitFormUrisMap;

import java.util.List;

/** Provides the module configuration set */
public interface ConfigService {

  /**
   * Provides the list specifying possible visit times.
   *
   * @return - the list with visit times
   */
  List<String> getVisitTimes();

  /**
   * Provides the list specifying possible visit statuses.
   *
   * @return - the list with visit statuses
   */
  List<String> getVisitStatuses();

  /**
   * Provides the number of days after which missed visits should be automatically marked as missed.
   *
   * @return - the number of days
   */
  int getMinimumVisitDelayToMarkItAsMissed();

  /**
   * Provides the initial status of visit set after creation (first on the statuses list).
   *
   * @return - the initial status of visit
   */
  String getVisitInitialStatus();

  /**
   * Provides the list specifying statuses which point on already completed visits. Visits with
   * these statuses should not be changed to missed.
   *
   * @return - the list with visit statuses
   */
  List<String> getStatusesEndingVisit();

  /**
   * Provides the value specifying a status that will be set if the visit will be determined to be
   * marked as missing.
   *
   * @return - the visit status
   */
  String getStatusOfMissedVisit();

  /**
   * Provides the value specifying a status that will be set if the visit will be determined to be
   * marked as occurred.
   *
   * @return - the visit status
   */
  String getStatusOfOccurredVisit();

  /**
   * Provides the map specifying visit form uris for create/edit visit details.
   *
   * @return - the map of visit form uris
   */
  VisitFormUrisMap getVisitFormUrisMap();

  /**
   * Provides the information if encounter datetime validation is enabled.
   *
   * @return - encounter datetime validation status
   */
  boolean isEncounterDatetimeValidationEnabled();

  /**
   * Determines if Missed Visits Status Changer job should be created during module startup
   *
   * @return missed visit changer job creation status
   */
  boolean isMissedVisitChangerJobShouldBeCreated();
}
