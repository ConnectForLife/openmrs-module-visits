package org.openmrs.module.visits.api.helper;

import org.openmrs.VisitAttribute;
import org.openmrs.VisitAttributeType;

public final class VisitAttributeHelper {

    public static VisitAttribute createVisitAttribute(VisitAttributeType type, String value) {
        VisitAttribute visitAttribute = new VisitAttribute();
        visitAttribute.setAttributeType(type);
        visitAttribute.setValueReferenceInternal(value);
        visitAttribute.setValue(value);
        return visitAttribute;
    }

    private VisitAttributeHelper() {
    }
}
