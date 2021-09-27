package org.openmrs.module.visits.api.dao.impl;

import org.openmrs.Visit;
import org.openmrs.module.visits.api.dao.BaseOpenmrsDataDao;
import org.openmrs.module.visits.api.dao.VisitDao;

public class VisitDaoImpl extends BaseOpenmrsDataDao<Visit> implements VisitDao {
    public VisitDaoImpl() {
        super(Visit.class);
    }
}
