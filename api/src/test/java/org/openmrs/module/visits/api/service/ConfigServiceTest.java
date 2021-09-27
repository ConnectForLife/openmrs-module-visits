package org.openmrs.module.visits.api.service;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.openmrs.module.visits.ContextMockedTest;
import org.openmrs.module.visits.api.dto.VisitFormUrisMap;
import org.openmrs.module.visits.api.service.impl.ConfigServiceImpl;
import org.openmrs.module.visits.api.util.GlobalPropertiesConstants;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.openmrs.module.visits.api.util.TestConstants.VISIT_URI_MAP_JSON;
import static org.powermock.api.mockito.PowerMockito.doReturn;

public class ConfigServiceTest extends ContextMockedTest {

    private ConfigService configService = new ConfigServiceImpl();

    @Test
    public void shouldReturnVisitTimes() {
        List<String> times = Arrays.asList("morning", "afternoon", "evening");
        doReturn(StringUtils.join(times, ','))
                .when(getAdministrationService())
                .getGlobalProperty(GlobalPropertiesConstants.VISIT_TIMES.getKey());

        List<String> visitTimes = configService.getVisitTimes();
        assertThat(visitTimes, contains(times.toArray()));
    }

    @Test
    public void shouldReturnVisitStatuses() {
        List<String> statuses = Arrays.asList("SCHEDULED", "OCCURRED", "MISSED");
        doReturn(StringUtils.join(statuses, ','))
                .when(getAdministrationService())
                .getGlobalProperty(GlobalPropertiesConstants.VISIT_STATUSES.getKey());

        List<String> visitTimes = configService.getVisitStatuses();
        assertThat(visitTimes, contains(statuses.toArray()));
    }

    @Test
    public void shouldReturnMinimumVisitDelayToMarkItAsMissed() {
        doReturn("2")
                .when(getAdministrationService())
                .getGlobalProperty(GlobalPropertiesConstants.MINIMUM_VISIT_DELAY_TO_MARK_IT_AS_MISSED.getKey());

        int delay = configService.getMinimumVisitDelayToMarkItAsMissed();
        assertThat(delay, equalTo(2));
    }

    @Test
    public void shouldReturnVisitInitialStatus() {
        List<String> statuses = Arrays.asList("SCHEDULED", "OCCURRED", "MISSED");
        doReturn(StringUtils.join(statuses, ','))
                .when(getAdministrationService())
                .getGlobalProperty(GlobalPropertiesConstants.VISIT_STATUSES.getKey());

        String initialStatus = configService.getVisitInitialStatus();
        assertThat(initialStatus, equalTo(statuses.get(0)));
    }

    @Test
    public void shouldReturnStatusesEndingVisit() {
        String endingStatuses = GlobalPropertiesConstants.STATUSES_ENDING_VISIT.getDefaultValue();
        doReturn(endingStatuses)
                .when(getAdministrationService())
                .getGlobalProperty(GlobalPropertiesConstants.STATUSES_ENDING_VISIT.getKey());

        List<String> endingStatusesList = configService.getStatusesEndingVisit();
        assertThat(endingStatusesList, contains(endingStatuses.split(",")));
    }

    @Test
    public void shouldReturnStatusOfMissedVisit() {
        String missedStatus = GlobalPropertiesConstants.STATUS_OF_MISSED_VISIT.getDefaultValue();
        doReturn(missedStatus)
                .when(getAdministrationService())
                .getGlobalProperty(GlobalPropertiesConstants.STATUS_OF_MISSED_VISIT.getKey());

        String statusOfMissedVisit = configService.getStatusOfMissedVisit();
        assertThat(statusOfMissedVisit, equalTo(missedStatus));
    }

    @Test
    public void shouldReturnStatusOfOccurredVisit() {
        String occurredStatus = GlobalPropertiesConstants.STATUS_OF_OCCURRED_VISIT.getDefaultValue();
        doReturn(occurredStatus)
                .when(getAdministrationService())
                .getGlobalProperty(GlobalPropertiesConstants.STATUS_OF_OCCURRED_VISIT.getKey());

        String statusOfOccurredVisit = configService.getStatusOfOccurredVisit();
        assertThat(statusOfOccurredVisit, equalTo(occurredStatus));
    }

    @Test
    public void shouldReturnVisitFormUrisMap() {
        doReturn(VISIT_URI_MAP_JSON)
                .when(getAdministrationService())
                .getGlobalProperty(GlobalPropertiesConstants.VISIT_FORM_URIS_KEY);

        VisitFormUrisMap urisMap = configService.getVisitFormUrisMap();
        assertNotNull(urisMap);
    }

    @Test
    public void shouldReturnIfIsEncounterDatetimeValidationEnabled() {
        String isValidationEnabledString = GlobalPropertiesConstants.ENCOUNTER_DATETIME_VALIDATION.getDefaultValue();
        doReturn(isValidationEnabledString)
                .when(getAdministrationService())
                .getGlobalProperty(GlobalPropertiesConstants.STATUS_OF_MISSED_VISIT.getKey());

        boolean isEnabled = configService.isEncounterDatetimeValidationEnabled();
        assertThat(isEnabled, equalTo(false));
    }
}
