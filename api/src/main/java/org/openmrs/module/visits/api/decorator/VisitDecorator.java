package org.openmrs.module.visits.api.decorator;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Visit;
import org.openmrs.VisitAttribute;
import org.openmrs.VisitAttributeType;
import org.openmrs.VisitType;
import org.openmrs.api.context.Context;
import org.openmrs.module.visits.api.util.ConfigConstants;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.openmrs.module.visits.api.util.ConfigConstants.VISIT_STATUS_ATTRIBUTE_TYPE_UUID;
import static org.openmrs.module.visits.api.util.ConfigConstants.VISIT_TIME_ATTRIBUTE_TYPE_UUID;

public class VisitDecorator extends ObjectDecorator<Visit> {

  public VisitDecorator(Visit object) {
    super(object);
  }

  public Integer getId() {
    return getObject().getId();
  }

  public String getUuid() {
    return getObject().getUuid();
  }

  public void setStartDatetime(Date startDate) {
    getObject().setStartDatetime(startDate);
  }

  public void setChanged() {
    getObject().setDateChanged(new Date());
    getObject().setChangedBy(Context.getAuthenticatedUser());
  }

  public void setLocation(Location locationByUuid) {
    getObject().setLocation(locationByUuid);
  }

  public void setVisitType(VisitType visitTypeByUuid) {
    getObject().setVisitType(visitTypeByUuid);
  }

  public String getTime() {
    return getAttribute(VISIT_TIME_ATTRIBUTE_TYPE_UUID);
  }

  public void setTime(String time) {
    setAttribute(ConfigConstants.VISIT_TIME_ATTRIBUTE_TYPE_UUID, time);
  }

  public String getStatus() {
    return getAttribute(VISIT_STATUS_ATTRIBUTE_TYPE_UUID);
  }

  public void setStatus(String status) {
    setAttribute(ConfigConstants.VISIT_STATUS_ATTRIBUTE_TYPE_UUID, status);
  }

  public void setVisitStatus(VisitAttributeType type, String status) {
    setVisitStatusAttribute(type, status);
  }

  public Date getActualDate() {
    if (getObject().getEncounters() == null || getObject().getEncounters().isEmpty()) {
      return null;
    }

    // we assume there is 1 encounter per visit
    Encounter encounter = getObject().getEncounters().iterator().next();
    return encounter.getEncounterDatetime();
  }

  public void setActualDate(Date actualDate) {
    if (actualDate == null) {
      return;
    }
    if (getObject().getEncounters() == null || getObject().getEncounters().isEmpty()) {
      throw new IllegalStateException(
          "Unable to set actual date (encounter date). No encounters related to visit.");
    }

    // we assume there is 1 encounter per visit
    Encounter encounter = getObject().getEncounters().iterator().next();
    encounter.setEncounterDatetime(actualDate);
  }

  private String getAttribute(String visitStatusAttributeTypeUuid) {
    String status = null;
    VisitAttribute attribute = getVisitAttribute(visitStatusAttributeTypeUuid);
    if (attribute != null) {
      status = String.valueOf(attribute.getValue());
    }
    return status;
  }

  private VisitAttribute getVisitAttribute(String typeUuid) {
    Set<VisitAttribute> attributes = new HashSet<>(getObject().getActiveAttributes());
    for (VisitAttribute attribute : attributes) {
      if (typeUuid.equals(attribute.getAttributeType().getUuid())) {
        return attribute;
      }
    }
    return null;
  }

  private void setAttribute(String typeUuid, String value) {
    if (StringUtils.isBlank(value)) {
      voidAllAttributesWithType(typeUuid);
    } else {
      getObject().setAttribute(createVisitAttribute(typeUuid, value));
    }
  }

  private void voidAllAttributesWithType(String typeUuid) {
    for (VisitAttribute attribute : getAttributesOfType(typeUuid)) {
      attribute.setVoided(Boolean.TRUE);
    }
  }

  private VisitAttribute createVisitAttribute(String typeUuid, String value) {
    VisitAttribute result = new VisitAttribute();
    result.setAttributeType(Context.getVisitService().getVisitAttributeTypeByUuid(typeUuid));
    result.setValueReferenceInternal(value);
    return result;
  }

  private void setVisitStatusAttribute(VisitAttributeType type, String value) {
    if (StringUtils.isBlank(value)) {
      voidAllAttributesWithType(type.getUuid());
    } else {
      getObject().setAttribute(createVisitAttributeFromAttributeTypeAndValue(type, value));
    }
  }

  private VisitAttribute createVisitAttributeFromAttributeTypeAndValue(
      VisitAttributeType type, String value) {
    VisitAttribute visitAttribute = new VisitAttribute();
    visitAttribute.setAttributeType(type);
    visitAttribute.setValueReferenceInternal(value);
    return visitAttribute;
  }

  private List<VisitAttribute> getAttributesOfType(String typeUuid) {
    List<VisitAttribute> results = new ArrayList<>();
    for (VisitAttribute attribute : getObject().getActiveAttributes()) {
      if (attribute.getAttributeType().getUuid().equals(typeUuid)) {
        results.add(attribute);
      }
    }
    return results;
  }
}
