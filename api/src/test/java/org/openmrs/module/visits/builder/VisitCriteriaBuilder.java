package org.openmrs.module.visits.builder;

import org.openmrs.Patient;
import org.openmrs.module.visits.domain.criteria.VisitCriteria;

public class VisitCriteriaBuilder extends AbstractBuilder<VisitCriteria> {

    private static final int PATIENT_ID = 2512;
    private Integer patientId;

    public VisitCriteriaBuilder() {
        this.patientId = PATIENT_ID;
    }

    @Override
    public VisitCriteria build() {
        Patient patient = new PatientBuilder().withId(this.patientId).build();
        VisitCriteria criteria = new VisitCriteria(patient);
        return criteria;
    }

    @Override
    public VisitCriteria buildAsNew() {
        return withPatientId(null).build();
    }

    public VisitCriteriaBuilder withPatientId(Integer patientId) {
        this.patientId = patientId;
        return this;
    }
}
