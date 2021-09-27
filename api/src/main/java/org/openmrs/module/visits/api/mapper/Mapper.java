package org.openmrs.module.visits.api.mapper;

import org.openmrs.BaseOpenmrsData;

import java.util.List;

/**
 * Maps objects between related types
 *
 * @param <T> type of DTO object
 * @param <R> type of DAO object
 */
public interface Mapper<T, R extends BaseOpenmrsData> {

    /**
     * Maps a DAO object to a DTO object
     *
     * @param dao DAO object
     * @return a DTO object
     */
    T toDto(R dao);

    /**
     * Maps a DTO object to a DAO object
     *
     * @param dto DTO object
     * @return a DAO object
     */
    R fromDto(T dto);

    /**
     * Maps a list of DAO objects to a list of DTO objects
     *
     * @param daos list of DAO objects
     * @return a list of DTO objects
     */
    List<T> toDtos(List<R> daos);

    /**
     * Maps a list of DTO objects to a list of DAO objects
     *
     * @param dtos list of DTO objects
     * @return a list of DAO objects
     */
    List<R> fromDtos(List<T> dtos);
}
