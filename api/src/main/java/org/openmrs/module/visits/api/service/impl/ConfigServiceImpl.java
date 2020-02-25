package org.openmrs.module.visits.api.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.visits.api.service.ConfigService;
import org.openmrs.module.visits.api.util.GlobalPropertiesConstants;
import org.openmrs.module.visits.api.util.GlobalPropertyUtil;

import java.util.List;

/**
 * Provides the default implementation of module configuration set
 */
public class ConfigServiceImpl implements ConfigService {

    private static final Log LOGGER = LogFactory.getLog(ConfigServiceImpl.class);

    private static final String COMMA_DELIMITER = ",";
    private static final int ONE = 1;

    @Override
    public List<String> getVisitTimes() {
        return GlobalPropertyUtil.parseList(
                getGp(GlobalPropertiesConstants.VISIT_TIMES.getKey()),
                COMMA_DELIMITER);
    }

    @Override
    public List<String> getVisitStatuses() {
        return GlobalPropertyUtil.parseList(
                getGp(GlobalPropertiesConstants.VISIT_STATUSES.getKey()),
                COMMA_DELIMITER);
    }

    @Override
    public int getMinimumVisitDelayToMarkItAsMissed() {
        String gpName = GlobalPropertiesConstants.MINIMUM_VISIT_DELAY_TO_MARK_IT_AS_MISSED.getKey();
        int days = GlobalPropertyUtil.parseInt(gpName, getGp(gpName));
        if (days < ONE) {
            LOGGER.warn(String.format("The GP minimumVisitDelayToMarkItAsMissed could not be below 1 " +
                            "(the current value: %d). The value 1 will be used", days));
            days = ONE;
        }
        return days;
    }

    @Override
    public List<String> getStatusesEndingVisit() {
        List<String> statusesEndingVisits = GlobalPropertyUtil.parseList(
                getGp(GlobalPropertiesConstants.STATUSES_ENDING_VISIT.getKey()),
                COMMA_DELIMITER);
        List<String> visitStatuses = getVisitStatuses();
        if (!visitStatuses.containsAll(statusesEndingVisits)) {
            LOGGER.warn(String.format(
                    "Not all statuses ending a visit defined in GP (%s) are part of visit statuses (%s)",
                    statusesEndingVisits,
                    visitStatuses));
        }
        return statusesEndingVisits;
    }

    @Override
    public String getStatusOfMissedVisit() {
        String missedStatus = getGp(GlobalPropertiesConstants.STATUS_OF_MISSED_VISIT.getKey());
        List<String> visitStatuses = getVisitStatuses();
        if (!visitStatuses.contains(missedStatus)) {
            LOGGER.warn(String.format("The missed visit's status defined in GP (%s) is not part of visit statuses (%s)",
                    missedStatus,
                    visitStatuses));
        }
        return missedStatus;
    }

    @Override
    public String getStatusOfOccurredVisit() {
        String missedStatus = getGp(GlobalPropertiesConstants.STATUS_OF_OCCURRED_VISIT.getKey());
        List<String> visitStatuses = getVisitStatuses();
        if (!visitStatuses.contains(missedStatus)) {
            LOGGER.warn(String.format("The occurred visit's status defined in GP (%s) is not part of visit statuses (%s)",
                    missedStatus,
                    visitStatuses));
        }
        return missedStatus;
    }

    private String getGp(String propertyName) {
        return Context.getAdministrationService().getGlobalProperty(propertyName);
    }
}
