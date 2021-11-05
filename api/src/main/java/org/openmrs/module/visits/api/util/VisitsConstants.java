package org.openmrs.module.visits.api.util;

public final class VisitsConstants {

    public static final String DEFAULT_SERVER_SIDE_DATE_FORMAT = "yyyy-MM-dd";

    /**
     * The datetime format which is used on server side.
     *
     * Note: To be careful with changing it. Date parsed to this format are directly passed to queries.
     * DB engines are sensitive to the precision of dates. Eg MySQL's functions are mainly based on the precision on
     * the seconds level (the function DATE). To be sure that comparing dates on the DB side works correctly we must use
     * consistent format on the backend server side.
     */
    public static final String DEFAULT_SERVER_SIDE_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String DEFAULT_FRONT_END_DATE_FORMAT = "dd MMM yyyy";

    public static final String CREATE_URI_NAME = "create";
    public static final String EDIT_URI_NAME = "edit";

    public static final String VISIT_CONFIG_SERVICE_BEAN_NAME = "visits.configService";

    private VisitsConstants() {
    }
}
