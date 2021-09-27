package org.openmrs.module.visits.builder;

import org.openmrs.VisitAttribute;
import org.openmrs.VisitAttributeType;

public class VisitAttributeBuilder extends AbstractBuilder<VisitAttribute> {
    private Integer visitAttributeId;
    private VisitAttributeType visitAttributeType;

    public VisitAttributeBuilder() {
        super();
        visitAttributeType = new VisitAttributeType();
    }

    @Override
    public VisitAttribute build() {
        return buildAsNew();
    }

    @Override
    public VisitAttribute buildAsNew() {
        VisitAttribute va = new VisitAttribute();
        va.setVisitAttributeId(visitAttributeId == null ? getAndIncrementNumber() : visitAttributeId);
        va.setAttributeType(visitAttributeType);
        return va;
    }
}
