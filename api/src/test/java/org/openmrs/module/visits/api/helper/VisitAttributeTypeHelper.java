package org.openmrs.module.visits.api.helper;

import org.openmrs.VisitAttributeType;
import org.openmrs.customdatatype.datatype.FreeTextDatatype;

public final class VisitAttributeTypeHelper {

    public static VisitAttributeType createVisitAttributeType(String attrTypeName, String uuid) {
        VisitAttributeType visitAttributeType = new VisitAttributeType();
        visitAttributeType.setName(attrTypeName);
        visitAttributeType.setDatatypeClassname(FreeTextDatatype.class.getName());
        visitAttributeType.setUuid(uuid);

        return visitAttributeType;
    }

    private VisitAttributeTypeHelper() {
    }
}
