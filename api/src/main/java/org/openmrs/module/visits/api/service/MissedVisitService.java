package org.openmrs.module.visits.api.service;

import java.util.List;

/**
 * Provides methods related to MissedVisitChanger job functionalities
 */
public interface MissedVisitService {

    /**
     * Changes visit status to missed. Method is always executed in new transaction to be sure that we have the latest
     * version of visit status. Sometimes more than one action changes visit status at the same time and the visit
     * may no longer be appropriate for the change.
     *
     * @param visitId id of visit
     * @param statusesToMarkVisitAsMissed list of eligible visit statues to mark visit as missed
     */
    void changeVisitStatusToMissed(Integer visitId, List<String> statusesToMarkVisitAsMissed);
}
