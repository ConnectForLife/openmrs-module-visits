package org.openmrs.module.visits.api.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Visit;
import org.openmrs.api.VisitService;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.visits.api.decorator.VisitDecorator;
import org.openmrs.module.visits.api.service.ConfigService;
import org.openmrs.module.visits.api.service.MissedVisitService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

public class MissedVisitServiceImpl extends BaseOpenmrsService implements MissedVisitService {

    private static final Log LOGGER = LogFactory.getLog(MissedVisitServiceImpl.class);

    private VisitService visitService;

    private ConfigService configService;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void changeVisitStatusToMissed(Integer visitId, List<String> statusesToMarkVisitAsMissed) {
        Visit visit = visitService.getVisit(visitId);
        if (visit == null) {
            throw new EntityNotFoundException(String.format("Visit with id %d not found", visitId));
        } else {
            VisitDecorator visitDecorator = new VisitDecorator(visit);
            if (statusesToMarkVisitAsMissed.contains(visitDecorator.getStatus())) {
                String missedVisitStatus = configService.getStatusOfMissedVisit();
                LOGGER.info(String.format("Changing status visit with id: %d to %s",
                        visitDecorator.getObject().getVisitId(), missedVisitStatus));
                visitDecorator.setStatus(missedVisitStatus);
                visitDecorator.setChanged();
                visitService.saveVisit(visitDecorator.getObject());
            }
        }
    }

    public void setVisitService(VisitService visitService) {
        this.visitService = visitService;
    }

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }
}
