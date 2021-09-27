package org.openmrs.module.visits.api.dto;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Date;

/**
 * Object representing a visit DTO
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VisitDTO implements Serializable, Cloneable {

    private static final long serialVersionUID = -6043667045678304408L;

    private String uuid;
    private Date startDate;
    private String time;
    private String location;
    private String type;
    private String status;
    private String formUri;
    private Date actualDate;
    private String patientUuid;

    public VisitDTO() {
    }

    /**
     * Constructor of a Visit DTO object
     *
     * @param uuid uuid of the visit
     * @param startDate start date of visit (planned date)
     * @param time time of day of the visit (eg. morning)
     * @param location location of the visit
     * @param type type of the visit (eg. follow-up)
     * @param status status of the visit (eg. SCHEDULED)
     * @param formUri visit form URI
     * @param actualDate actual date of the visit
     * @param patientUuid uuid of the patient
     */
    @SuppressWarnings("checkstyle:parameternumber")
    public VisitDTO(String uuid, Date startDate, String time, String location, String type, String status,
                    String formUri, Date actualDate, String patientUuid) {
        this.uuid = uuid;
        this.startDate = startDate;
        this.time = time;
        this.location = location;
        this.type = type;
        this.status = status;
        this.formUri = formUri;
        this.actualDate = actualDate;
        this.patientUuid = patientUuid;
    }

    /**
     * @return a clone of a visit DTO object
     * @throws CloneNotSupportedException
     */
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String getUuid() {
        return uuid;
    }

    public VisitDTO setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public Date getStartDate() {
        return startDate;
    }

    public VisitDTO setStartDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    public String getTime() {
        return time;
    }

    public VisitDTO setTime(String time) {
        this.time = time;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public VisitDTO setLocation(String location) {
        this.location = location;
        return this;
    }

    public String getType() {
        return type;
    }

    public VisitDTO setType(String type) {
        this.type = type;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public VisitDTO setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getFormUri() {
        return formUri;
    }

    public VisitDTO setFormUri(String formUri) {
        this.formUri = formUri;
        return this;
    }

    public Date getActualDate() {
        return actualDate;
    }

    public VisitDTO setActualDate(Date actualDate) {
        this.actualDate = actualDate;
        return this;
    }

    public String getPatientUuid() {
        return patientUuid;
    }

    public VisitDTO setPatientUuid(String patientUuid) {
        this.patientUuid = patientUuid;
        return this;
    }
}
