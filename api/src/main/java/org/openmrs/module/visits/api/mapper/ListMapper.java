package org.openmrs.module.visits.api.mapper;

import java.util.List;
import org.openmrs.BaseOpenmrsData;

public interface ListMapper<T, R extends BaseOpenmrsData> {

    T toDto(List<R> daos);

    List<R> fromDto(T dao);
}
