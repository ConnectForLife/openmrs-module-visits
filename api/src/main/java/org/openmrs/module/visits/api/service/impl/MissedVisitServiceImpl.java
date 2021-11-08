package org.openmrs.module.visits.api.service.impl;

import org.openmrs.Visit;
import org.openmrs.VisitAttributeType;
import org.openmrs.api.VisitService;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.visits.api.decorator.VisitDecorator;
import org.openmrs.module.visits.api.service.MissedVisitService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

public class MissedVisitServiceImpl extends BaseOpenmrsService implements MissedVisitService {

  private VisitService visitService;

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void changeVisitStatusesToMissed(
      List<Integer> visitIds,
      List<String> statusesToMarkVisitAsMissed,
      String missedVisitStatus,
      VisitAttributeType visitStatusAttributeType) {
    visitIds.forEach(
        id -> {
          Visit visit = visitService.getVisit(id);
          if (visit == null) {
            throw new EntityNotFoundException(String.format("Visit with id %d not found", id));
          }
          VisitDecorator visitDecorator = new VisitDecorator(visit);
          if (statusesToMarkVisitAsMissed.contains(visitDecorator.getStatus())) {
            visitDecorator.setVisitStatus(visitStatusAttributeType, missedVisitStatus);
            visitDecorator.setChanged();
            visitService.saveVisit(visitDecorator.getObject());
          }
        });
  }

  public void setVisitService(VisitService visitService) {
    this.visitService = visitService;
  }
}
