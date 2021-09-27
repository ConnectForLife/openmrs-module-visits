package org.openmrs.module.visits.builder;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitAttribute;
import org.openmrs.VisitType;

import java.util.Collections;
import java.util.Date;
import java.util.Set;

public class VisitBuilder extends AbstractBuilder<Visit> {

    private Integer id;
    private Patient patient;
    private VisitType visitType;
    private Date startDatetime;
    private Location location;
    private Set<VisitAttribute> attributes;

    public VisitBuilder() {
        super();
        patient = new PatientBuilder().build();
        startDatetime = new Date();
        visitType = new VisitTypeBuilder().build();
        location = new LocationBuilder().build();
        attributes = Collections.singleton(new VisitAttributeBuilder().build());
    }

    @Override
    public Visit build() {
        Visit visit = buildAsNew();
        visit.setId(id == null ? getAndIncrementNumber() : id);
        return visit;
    }

    @Override
    public Visit buildAsNew() {
        Visit visit = new Visit(patient, visitType, startDatetime);
        visit.setId(1);
        visit.setLocation(location);
        visit.setAttributes(attributes);
        return visit;
    }

    public VisitBuilder withId(Integer id) {
        this.id = id;
        return this;
    }

    public VisitBuilder withPatient(Patient patient) {
        this.patient = patient;
        return this;
    }

    public VisitBuilder withVisitType(VisitType visitType) {
        this.visitType = visitType;
        return this;
    }

    public VisitBuilder withStartDatetime(Date startDatetime) {
        this.startDatetime = startDatetime;
        return this;
    }

    public VisitBuilder withLocation(Location location) {
        this.location = location;
        return this;
    }
}
