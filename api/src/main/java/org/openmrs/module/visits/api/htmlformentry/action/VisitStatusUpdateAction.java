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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.CustomFormSubmissionAction;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.visits.api.decorator.VisitDecorator;
import org.openmrs.module.visits.api.service.ConfigService;
import org.openmrs.module.visits.api.service.VisitService;

/**
 * The VisitStatusUpdateAction Class.
 *
 * <p>The implementation of Post Submission Action which sets the Visits Status (Visit attribute) to
 * correct status after the Visit occurred.
 *
 * <p>If the submitted form contains the "Did this visit occur?" question (concept UUID {@value
 * #DID_VISIT_OCCUR_CONCEPT_UUID}) answered with "No" (concept UUID {@value
 * #NO_ANSWER_CONCEPT_UUID}), the visit is marked as MISSED. In every other case (question absent
 * or answered "Yes") the visit is marked as OCCURRED.
 *
 * <p>Example: <br>
 * my-form.xml: <br>
 * {@code <postSubmissionAction
 * class="org.openmrs.module.visits.api.htmlformentry.action.VisitStatusUpdateAction"/>}
 */
public class VisitStatusUpdateAction implements CustomFormSubmissionAction {
  private static final String VISIT_SERVICE = "visits.visitService";
  private static final String CONFIG_SERVICE = "visits.configService";

  private static final String DID_VISIT_OCCUR_CONCEPT_UUID =
      "37cfcaeb-96e8-442a-a362-f26a08f85c93";
  private static final String NO_ANSWER_CONCEPT_UUID =
      "1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

  private static final Log LOGGER = LogFactory.getLog(VisitStatusUpdateAction.class);

  @Override
  public void applyAction(FormEntrySession formEntrySession) {
    if (formEntrySession.getContext().getVisit() != null) {
      final VisitDecorator visitDecorator =
          new VisitDecorator((Visit) formEntrySession.getContext().getVisit());
      final String targetStatus = resolveTargetStatus(formEntrySession.getEncounter());
      visitDecorator.setStatus(targetStatus);
      visitDecorator.setChanged();
      getVisitService().saveOrUpdate(visitDecorator.getObject());
      LOGGER.info(
          String.format(
              "Visit with uuid: %s has successfully changed the status to %s.",
              visitDecorator.getUuid(), targetStatus));
    }
  }

  private String resolveTargetStatus(Encounter encounter) {
    if (isDidVisitOccurAnsweredNo(encounter)) {
      return getConfigService().getMissedVisitStatuses().get(0);
    }
    return getConfigService().getOccurredVisitStatues().get(0);
  }

  private boolean isDidVisitOccurAnsweredNo(Encounter encounter) {
    if (encounter == null || encounter.getAllObs() == null) {
      return false;
    }
    for (Obs obs : encounter.getAllObs()) {
      final Concept question = obs.getConcept();
      final Concept answer = obs.getValueCoded();
      if (question != null
          && DID_VISIT_OCCUR_CONCEPT_UUID.equals(question.getUuid())
          && answer != null
          && NO_ANSWER_CONCEPT_UUID.equals(answer.getUuid())) {
        return true;
      }
    }
    return false;
  }

  private ConfigService getConfigService() {
    return Context.getRegisteredComponent(CONFIG_SERVICE, ConfigService.class);
  }

  private VisitService getVisitService() {
    return Context.getRegisteredComponent(VISIT_SERVICE, VisitService.class);
  }
}
