package org.openmrs.module.visits.api.dto;

import java.io.Serializable;

/**
 * Object representing a visit DTO extended with the additional properties
 */
public class VisitDetailsDTO extends VisitDTO implements Serializable {

    private static final long serialVersionUID = 1330524598690465688L;

    private String locationName;
    private String typeName;

    public VisitDetailsDTO() {
    }

    /**
     * Constructor of the visit details DTO
     *
     * @param visitDTO base visit DTO object
     * @param locationName name of the location
     * @param typeName name of the type
     */
    public VisitDetailsDTO(VisitDTO visitDTO, String locationName, String typeName) {
        super(
                visitDTO.getUuid(),
                visitDTO.getStartDate(),
                visitDTO.getTime(),
                visitDTO.getLocation(),
                visitDTO.getType(),
                visitDTO.getStatus(),
                visitDTO.getFormUri(),
                visitDTO.getActualDate(),
                visitDTO.getPatientUuid()
        );
        this.locationName = locationName;
        this.typeName = typeName;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
