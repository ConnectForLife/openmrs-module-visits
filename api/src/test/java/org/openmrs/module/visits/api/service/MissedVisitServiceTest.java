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

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class})
public class MissedVisitServiceTest {

  private static final String SCHEDULED_VISIT_STATUS = "SCHEDULED";

  private static final String MISSED_VISIT_STATUS = "MISSED";

  @InjectMocks private MissedVisitService missedVisitService = new MissedVisitServiceImpl();

  @Mock private VisitService visitService;

  @Before
  public void setUp() {
    mockStatic(Context.class);
    when(Context.getVisitService()).thenReturn(visitService);
  }

  @Test
  public void changeVisitStatusesToMissed_whenAllVisitsExist() {
    when(visitService.getVisit(1)).thenReturn(buildTestVisit(1));
    when(visitService.getVisit(2)).thenReturn(buildTestVisit(2));
    when(visitService.getVisit(3)).thenReturn(buildTestVisit(3));

    List<String> eligibleStatusesListToMarkVisitAsMissed =
        Arrays.asList(SCHEDULED_VISIT_STATUS, MISSED_VISIT_STATUS);

    missedVisitService.changeVisitStatusesToMissed(
        Arrays.asList(1, 2, 3),
        eligibleStatusesListToMarkVisitAsMissed,
        MISSED_VISIT_STATUS,
        any(VisitAttributeType.class));

    verify(visitService, times(3)).getVisit(anyInt());
    verify(visitService, times(3)).saveVisit(any(Visit.class));
  }

  @Test(expected = EntityNotFoundException.class)
  public void changeVisitStatusesToMissed_whenOneOfTheVisitsNotExist() {
    when(visitService.getVisit(1)).thenReturn(buildTestVisit(1));
    when(visitService.getVisit(2)).thenReturn(null);
    when(visitService.getVisit(3)).thenReturn(buildTestVisit(3));

    List<String> eligibleStatusesListToMarkVisitAsMissed =
        Arrays.asList(SCHEDULED_VISIT_STATUS, MISSED_VISIT_STATUS);

    missedVisitService.changeVisitStatusesToMissed(
        Arrays.asList(1, 2, 3),
        eligibleStatusesListToMarkVisitAsMissed,
        MISSED_VISIT_STATUS,
        any(VisitAttributeType.class));
  }

  private Visit buildTestVisit(Integer visitId) {
    Visit visit = new Visit();

    visit.setVisitId(visitId);
    VisitAttributeType visitStatusType =
        VisitAttributeTypeHelper.createVisitAttributeType(
            "Visit Status", "70ca70ac-53fd-49e4-9abe-663d4785fe62");
    VisitAttribute statusAttribute =
        VisitAttributeHelper.createVisitAttribute(visitStatusType, SCHEDULED_VISIT_STATUS);
    visit.setAttribute(statusAttribute);
    return visit;
  }
}
