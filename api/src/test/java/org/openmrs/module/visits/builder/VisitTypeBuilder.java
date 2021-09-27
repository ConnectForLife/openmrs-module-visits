package org.openmrs.module.visits.builder;

import org.openmrs.VisitType;

public class VisitTypeBuilder extends AbstractBuilder<VisitType> {

    private Integer id;
    private String name;
    private String description;

    public VisitTypeBuilder() {
        name = "Visit type name";
        description = "Visit type description";
    }

    public VisitType build() {
        VisitType visitType = buildAsNew();
        visitType.setId(id == null ? getAndIncrementNumber() : id);
        return visitType;
    }

    @Override
    public VisitType buildAsNew() {
        return new VisitType(name, description);
    }

    public VisitTypeBuilder withId(Integer id) {
        this.id = id;
        return this;
    }

    public VisitTypeBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public VisitTypeBuilder withDescription(String description) {
        this.description = description;
        return this;
    }
}
