package org.openmrs.module.visits.api.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Visit;
import org.openmrs.VisitAttribute;
import org.openmrs.VisitAttributeType;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.visits.api.helper.VisitAttributeHelper;
import org.openmrs.module.visits.api.helper.VisitAttributeTypeHelper;
import org.openmrs.module.visits.api.service.impl.MissedVisitServiceImpl;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class})
public class MissedVisitServiceTest {

    private static final String SCHEDULED_VISIT_STATUS = "SCHEDULED";

    private static final String MISSED_VISIT_STATUS = "MISSED";

    @InjectMocks
    private MissedVisitService missedVisitService = new MissedVisitServiceImpl();

    @Mock
    private VisitService visitService;

    @Mock
    private ConfigService configService;

    private Visit visit;

    @Before
    public void setUp() {
        mockStatic(Context.class);
        when(Context.getVisitService()).thenReturn(visitService);
        when(Context.getRegisteredComponent("visits.configService", ConfigService.class))
                .thenReturn(configService);

        visit = buildTestVisit();
    }

    @Test
    public void shouldChangeStatusVisitToMissed() {
        when(visitService.getVisit(anyInt())).thenReturn(visit);
        when(configService.getStatusOfMissedVisit()).thenReturn(MISSED_VISIT_STATUS);

        List<String> eligibleStatusesListToMarkVisitAsMissed = Arrays.asList(SCHEDULED_VISIT_STATUS, MISSED_VISIT_STATUS);

        missedVisitService.changeVisitStatusToMissed(anyInt(), eligibleStatusesListToMarkVisitAsMissed);

        verify(visitService).getVisit(anyInt());
        verify(visitService).saveVisit(any(Visit.class));
        verify(configService).getStatusOfMissedVisit();
    }

    private Visit buildTestVisit() {
        Visit visit = new Visit();

        visit.setVisitId(1);
        VisitAttributeType visitStatusType = VisitAttributeTypeHelper
                .createVisitAttributeType("Visit Status", "70ca70ac-53fd-49e4-9abe-663d4785fe62");
        VisitAttribute statusAttribute = VisitAttributeHelper.createVisitAttribute(visitStatusType, SCHEDULED_VISIT_STATUS);
        visit.setAttribute(statusAttribute);
        return visit;
    }
}
