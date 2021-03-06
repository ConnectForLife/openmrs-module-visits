package org.openmrs.module.visits.api.dto;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Date;

/** Object representing a visit DTO */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VisitDTO implements Serializable {

  private static final long serialVersionUID = 3L;

  private String uuid;
  private Date startDate;
  private String time;
  private String location;
  private String type;
  private String status;
  private String formUri;
  private Date actualDate;
  private String patientUuid;

  public VisitDTO() {}

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
  public VisitDTO(
      String uuid,
      Date startDate,
      String time,
      String location,
      String type,
      String status,
      String formUri,
      Date actualDate,
      String patientUuid) {
    this.uuid = uuid;
    this.startDate = (startDate != null ? new Date(startDate.getTime()) : null );
    this.time = time;
    this.location = location;
    this.type = type;
    this.status = status;
    this.formUri = formUri;
    this.actualDate = (actualDate != null ? new Date(actualDate.getTime()) : null);
    this.patientUuid = patientUuid;
  }

  // copy constructor
  public VisitDTO(VisitDTO visitDTO) {
    this.uuid = visitDTO.getUuid();
    this.startDate = visitDTO.getStartDate();
    this.time = visitDTO.getTime();
    this.location = visitDTO.getLocation();
    this.type = visitDTO.getType();
    this.status = visitDTO.getStatus();
    this.formUri = visitDTO.getFormUri();
    this.actualDate = visitDTO.getActualDate();
    this.patientUuid = visitDTO.getPatientUuid();
  }

  public String getUuid() {
    return uuid;
  }

  public VisitDTO setUuid(String uuid) {
    this.uuid = uuid;
    return this;
  }

  public Date getStartDate() {
    return startDate != null ? new Date(startDate.getTime()) : null;
  }

  public VisitDTO setStartDate(Date startDate) {
    this.startDate = startDate != null ? new Date(startDate.getTime()) : null;
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
    return actualDate != null ? new Date(actualDate.getTime()) : null;
  }

  public VisitDTO setActualDate(Date actualDate) {
    this.actualDate = actualDate != null ? new Date(actualDate.getTime()) : null;
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
