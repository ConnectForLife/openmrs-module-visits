/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
 
package org.openmrs.module.visits.api.util;

import org.openmrs.customdatatype.datatype.FreeTextDatatype;

public final class ConfigConstants {

    public static final String VISIT_TIMES_KEY = "visits.visit-times";

    public static final String VISIT_TIMES_DEFAULT_VALUE = "Morning,Afternoon,Evening";

    public static final String VISIT_TIMES_SEPARATOR = ",";

    public static final String VISIT_TIMES_DESCRIPTION = "Coma separated list of visit types used to schedule a visit."
            + " Example of usage: "
            + "'Morning,Afternoon,Evening'";

    public static final String VISIT_TIME_ATTRIBUTE_TYPE_NAME = "Visit Time";

    public static final String VISIT_TIME_ATTRIBUTE_TYPE_DESCRIPTION = "Visit Time attribute";

    public static final String VISIT_TIME_ATTRIBUTE_TYPE_DATATYPE = FreeTextDatatype.class.getName();

    public static final String VISIT_TIME_ATTRIBUTE_TYPE_UUID = "0e8e9572-7a4e-44f5-b555-4f5bfd6415b2";

    public static final String VISIT_STATUS_ATTRIBUTE_TYPE_NAME = "Visit Status";

    public static final String VISIT_STATUS_ATTRIBUTE_TYPE_DESCRIPTION = "Visit Status attribute";

    public static final String VISIT_STATUS_ATTRIBUTE_TYPE_DATATYPE = FreeTextDatatype.class.getName();

    public static final String VISIT_STATUS_ATTRIBUTE_TYPE_UUID = "70ca70ac-53fd-49e4-9abe-663d4785fe62";

    private ConfigConstants() {
    }
}
