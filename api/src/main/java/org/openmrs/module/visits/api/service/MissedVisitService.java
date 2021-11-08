package org.openmrs.module.visits.api.service;

import org.openmrs.VisitAttributeType;

import java.util.List;

/** Provides methods related to MissedVisitChanger job functionalities */
public interface MissedVisitService {

  /**
   * Changes visit statuses to missed. Method is always executed in new transaction to be sure that
   * we have the latest version of visit status. Sometimes more than one action changes visit status
   * at the same time and the visit may no longer be appropriate for the change. List of visitIds
   * from first parameter has fixed size according to batch size set in {@link
   * org.openmrs.module.visits.api.service.impl.MissedVisitServiceImpl}. Processing visits in
   * batches improves performance.
   *
   * @param visitIds ids of visits to process
   * @param statusesToMarkVisitAsMissed list of eligible visit statues to mark visit as missed
   * @param missedVisitStatus status of missed visit
   * @param visitStatusAttributeType visit status attribute type
   */
  void changeVisitStatusesToMissed(
      List<Integer> visitIds,
      List<String> statusesToMarkVisitAsMissed,
      String missedVisitStatus,
      VisitAttributeType visitStatusAttributeType);
}
