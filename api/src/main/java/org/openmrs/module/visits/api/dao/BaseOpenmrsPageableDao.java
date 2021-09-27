package org.openmrs.module.visits.api.dao;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.api.db.OpenmrsDataDAO;
import org.openmrs.module.visits.domain.PagingInfo;
import org.openmrs.module.visits.domain.criteria.BaseCriteria;

import java.util.List;

/**
 * Data access object for the entities extending BaseOpenmrsData
 */
public interface BaseOpenmrsPageableDao<T extends BaseOpenmrsData> extends OpenmrsDataDAO<T> {

    List<T> findAllByCriteria(BaseCriteria criteria, PagingInfo pagingInfo);
}
