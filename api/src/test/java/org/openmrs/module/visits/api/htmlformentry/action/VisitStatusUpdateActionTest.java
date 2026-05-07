/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.api.htmlformentry.action;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doReturn;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Visit;
import org.openmrs.VisitAttribute;
import org.openmrs.VisitAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.visits.ContextMockedTest;
import org.openmrs.module.visits.api.service.VisitService;
import org.openmrs.module.visits.api.util.ConfigConstants;
import org.openmrs.module.visits.builder.VisitBuilder;

public class VisitStatusUpdateActionTest extends ContextMockedTest {

  private static final String DID_VISIT_OCCUR_CONCEPT_UUID =
      "37cfcaeb-96e8-442a-a362-f26a08f85c93";
  private static final String YES_ANSWER_CONCEPT_UUID =
      "1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
  private static final String NO_ANSWER_CONCEPT_UUID =
      "1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
  private static final String OCCURRED_STATUS = "OCCURRED";
  private static final String MISSED_STATUS = "MISSED";

  private final VisitStatusUpdateAction action = new VisitStatusUpdateAction();

  private VisitService visitsVisitService;

  @Before
  public void setUpAction() {
    visitsVisitService = mock(VisitService.class);
    when(Context.getRegisteredComponent("visits.visitService", VisitService.class))
        .thenReturn(visitsVisitService);

    doReturn(Collections.singletonList(OCCURRED_STATUS))
        .when(getConfigService())
        .getOccurredVisitStatues();
    doReturn(Collections.singletonList(MISSED_STATUS))
        .when(getConfigService())
        .getMissedVisitStatuses();

    VisitAttributeType statusAttributeType = new VisitAttributeType();
    statusAttributeType.setUuid(ConfigConstants.VISIT_STATUS_ATTRIBUTE_TYPE_UUID);
    when(getVisitService().getVisitAttributeTypeByUuid(ConfigConstants.VISIT_STATUS_ATTRIBUTE_TYPE_UUID))
        .thenReturn(statusAttributeType);
  }

  @Test
  public void shouldSetOccurredStatusWhenDidVisitOccurQuestionIsAbsent() {
    FormEntrySession session = buildSession(buildVisit(), encounterWithObs(new HashSet<>()));

    action.applyAction(session);

    assertVisitStatusSavedAs(OCCURRED_STATUS);
  }

  @Test
  public void shouldSetOccurredStatusWhenDidVisitOccurAnsweredYes() {
    Set<Obs> obs = new HashSet<>();
    obs.add(buildObs(DID_VISIT_OCCUR_CONCEPT_UUID, YES_ANSWER_CONCEPT_UUID));
    FormEntrySession session = buildSession(buildVisit(), encounterWithObs(obs));

    action.applyAction(session);

    assertVisitStatusSavedAs(OCCURRED_STATUS);
  }

  @Test
  public void shouldSetMissedStatusWhenDidVisitOccurAnsweredNo() {
    Set<Obs> obs = new HashSet<>();
    obs.add(buildObs(DID_VISIT_OCCUR_CONCEPT_UUID, NO_ANSWER_CONCEPT_UUID));
    FormEntrySession session = buildSession(buildVisit(), encounterWithObs(obs));

    action.applyAction(session);

    assertVisitStatusSavedAs(MISSED_STATUS);
  }

  @Test
  public void shouldIgnoreUnrelatedObsAndFallBackToOccurred() {
    Set<Obs> obs = new HashSet<>();
    obs.add(buildObs("some-other-question-uuid", NO_ANSWER_CONCEPT_UUID));
    FormEntrySession session = buildSession(buildVisit(), encounterWithObs(obs));

    action.applyAction(session);

    assertVisitStatusSavedAs(OCCURRED_STATUS);
  }

  @Test
  public void shouldDoNothingWhenVisitIsMissing() {
    FormEntrySession session = mock(FormEntrySession.class);
    FormEntryContext context = mock(FormEntryContext.class);
    when(session.getContext()).thenReturn(context);
    when(context.getVisit()).thenReturn(null);

    action.applyAction(session);

    verify(visitsVisitService, never()).saveOrUpdate(any(Visit.class));
  }

  private void assertVisitStatusSavedAs(String expectedStatus) {
    ArgumentCaptor<Visit> captor = ArgumentCaptor.forClass(Visit.class);
    verify(visitsVisitService).saveOrUpdate(captor.capture());

    String savedStatus = null;
    for (VisitAttribute attribute : captor.getValue().getActiveAttributes()) {
      if (ConfigConstants.VISIT_STATUS_ATTRIBUTE_TYPE_UUID.equals(
          attribute.getAttributeType().getUuid())) {
        savedStatus = attribute.getValueReference();
      }
    }
    assertEquals(expectedStatus, savedStatus);
  }

  private Visit buildVisit() {
    Visit visit = new VisitBuilder().buildAsNew();
    visit.setAttributes(new HashSet<VisitAttribute>());
    return visit;
  }

  private Encounter encounterWithObs(Set<Obs> obs) {
    Encounter encounter = new Encounter();
    for (Obs ob : obs) {
      encounter.addObs(ob);
    }
    return encounter;
  }

  private Obs buildObs(String questionUuid, String answerUuid) {
    Concept question = new Concept();
    question.setUuid(questionUuid);
    Concept answer = new Concept();
    answer.setUuid(answerUuid);
    Obs obs = new Obs();
    obs.setConcept(question);
    obs.setValueCoded(answer);
    return obs;
  }

  private FormEntrySession buildSession(Visit visit, Encounter encounter) {
    FormEntrySession session = mock(FormEntrySession.class);
    FormEntryContext context = mock(FormEntryContext.class);
    when(session.getContext()).thenReturn(context);
    when(context.getVisit()).thenReturn(visit);
    when(session.getEncounter()).thenReturn(encounter);
    return session;
  }
}
