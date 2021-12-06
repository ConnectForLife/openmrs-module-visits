package org.openmrs.module.visits.api.mapper;

import java.util.ArrayList;
import java.util.List;
import org.openmrs.BaseOpenmrsData;

public abstract class AbstractMapper<T, R extends BaseOpenmrsData> implements Mapper<T, R> {

    public List<T> toDtos(List<R> daos) {
        List<T> dtos = new ArrayList<>(daos.size());
        for (R dao : daos) {
            dtos.add(toDto(dao));
        }
        return dtos;
    }

    public List<R> fromDtos(List<T> dtos) {
        List<R> daos = new ArrayList<>(dtos.size());
        for (T dto : dtos) {
            daos.add(fromDto(dto));
        }
        return daos;
    }
}
