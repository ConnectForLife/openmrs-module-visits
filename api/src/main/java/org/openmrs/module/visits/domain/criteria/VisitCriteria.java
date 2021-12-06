package org.openmrs.module.visits.domain.criteria;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.type.StandardBasicTypes;
import org.openmrs.Patient;
import org.openmrs.VisitAttribute;
import org.openmrs.module.visits.api.util.ConfigConstants;
import org.openmrs.module.visits.api.util.DateUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VisitCriteria extends BaseCriteria implements Serializable {

    private static final long serialVersionUID = 9L;

    private Patient patient;

    private boolean onlyMissedVisitsWithIncorrectStatus;
    private int minDelaysInDays;
    private List<String> statusesForEndedAndMissedVisit;

    private boolean sortResults;
    private String sortFieldName;
    private boolean sortAscending;

    public VisitCriteria(Patient patient) {
        this.onlyMissedVisitsWithIncorrectStatus = false;
        this.patient = patient;

        this.sortResults = true;
        this.sortFieldName = "startDatetime";
        this.sortAscending = false;
    }

    @Override
    public void loadHibernateCriteria(Criteria criteria) {
        if (patient != null) {
            criteria.add(Restrictions.eq("patient", patient));
        }
        if (onlyMissedVisitsWithIncorrectStatus) {
            addOnlyMissedVisitWithIncorrectStatusRestrictions(criteria);
        }
        if (sortResults) {
            addResultSorting(criteria);
        }
    }

    public String getPatientUuid() {
        return patient.getUuid();
    }

    public void setOnlyMissedVisitsWithIncorrectStatusRestrictions(int minDelaysInDays, String statusOfMissedVisit,
                                                                   List<String> statusesEndingVisit) {
        this.onlyMissedVisitsWithIncorrectStatus = true;
        this.minDelaysInDays = minDelaysInDays;
        this.statusesForEndedAndMissedVisit = new ArrayList<>(statusesEndingVisit);
        this.statusesForEndedAndMissedVisit.add(statusOfMissedVisit);
    }

    public void setSortingByField(String fieldName, boolean sortAscending) {
        this.sortResults = true;
        this.sortFieldName = fieldName;
        this.sortAscending = sortAscending;
    }

    public void disableSorting() {
        this.sortResults = false;
    }

    public static VisitCriteria forPatientUuid(String uuid) {
        Patient patient = new Patient();
        patient.setUuid(uuid);
        return new VisitCriteria(patient);
    }

    public static VisitCriteria forMissedVisits(int minDelayInDays, String statusOfMissedVisit,
                                                List<String> statusesEndingVisit) {
        VisitCriteria criteria = new VisitCriteria(null);
        criteria.setOnlyMissedVisitsWithIncorrectStatusRestrictions(
                minDelayInDays, statusOfMissedVisit, statusesEndingVisit);
        return criteria;
    }

    private void addOnlyMissedVisitWithIncorrectStatusRestrictions(Criteria criteria) {
        Date maxStartDate = DateUtil.getDateSecondsAgo(DateUtil.DAY_IN_SECONDS * minDelaysInDays);

        DetachedCriteria subCriteria = DetachedCriteria.forClass(VisitAttribute.class, "va")
                .createAlias("va.attributeType", "vat")
                .add(Restrictions.eq("vat.uuid", ConfigConstants.VISIT_STATUS_ATTRIBUTE_TYPE_UUID))
                .add(Restrictions.eq("va.voided", Boolean.FALSE))
                .add(Restrictions.not(Restrictions.in("va.valueReference", statusesForEndedAndMissedVisit)))
                .setProjection(Projections.projectionList().add(Projections.property("va.visit.visitId")));

        criteria.add(Subqueries.propertyIn("visitId", subCriteria))
                .add(Restrictions.sqlRestriction(
                        "TIMESTAMP(date_started) <= TIMESTAMP(?)",
                        maxStartDate, StandardBasicTypes.TIMESTAMP));
    }

    private void addResultSorting(Criteria criteria) {
        if (sortAscending) {
            criteria.addOrder(Order.asc(sortFieldName));
        } else {
            criteria.addOrder(Order.desc(sortFieldName));
        }
    }
}
