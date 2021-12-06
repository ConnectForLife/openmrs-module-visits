package org.openmrs.module.visits.domain.criteria;

import java.io.Serializable;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

public abstract class BaseCriteria implements Serializable {

    private static final long serialVersionUID = 6L;

    private boolean includeVoided;

    public abstract void loadHibernateCriteria(Criteria hibernateCriteria);

    public void initHibernateCriteria(Criteria hibernateCriteria) {
        if (!includeVoided) {
            hibernateCriteria.add(Restrictions.eq("voided", Boolean.FALSE));
        }
    }
}
