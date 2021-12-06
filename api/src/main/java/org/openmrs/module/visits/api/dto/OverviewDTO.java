package org.openmrs.module.visits.api.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Object representing a visit overview DTO, used to display data
 */
public class OverviewDTO implements Serializable {

    private static final long serialVersionUID = 7L;

    private String uuid;
    private String patientIdentifier;
    private NameUrlDTO nameUrl;
    private Date startDate;
    private String time;
    private String type;
    private String status;
    private String location;
    private Date actualDate;

    public String getUuid() {
        return uuid;
    }

    public OverviewDTO setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getPatientIdentifier() {
        return patientIdentifier;
    }

    public OverviewDTO setPatientIdentifier(String patientIdentifier) {
        this.patientIdentifier = patientIdentifier;
        return this;
    }

    public NameUrlDTO getNameUrl() {
        return nameUrl;
    }

    public OverviewDTO setNameUrl(NameUrlDTO nameUrl) {
        this.nameUrl = nameUrl;
        return this;
    }

    public Date getStartDate() {
        if (null != startDate) {
            return new Date(startDate.getTime());
        } else {
            return null;
        }
    }

    public OverviewDTO setStartDate(Date startDate) {
        this.startDate = startDate != null ? new Date(startDate.getTime()) : null;
        return this;
    }

    public String getTime() {
        return time;
    }

    public OverviewDTO setTime(String time) {
        this.time = time;
        return this;
    }

    public String getType() {
        return type;
    }

    public OverviewDTO setType(String type) {
        this.type = type;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public OverviewDTO setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public OverviewDTO setLocation(String location) {
        this.location = location;
        return this;
    }

    public Date getActualDate() {
        if (null != actualDate) {
            return new Date(actualDate.getTime());
        } else {
            return null;
        }
    }

    public OverviewDTO setActualDate(Date actualDate) {
        this.actualDate = actualDate != null ? new Date(actualDate.getTime()) : null;
        return this;
    }
}
