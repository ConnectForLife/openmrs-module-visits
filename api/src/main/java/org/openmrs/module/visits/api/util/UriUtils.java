package org.openmrs.module.visits.api.util;

import org.apache.commons.validator.routines.UrlValidator;

public final class UriUtils {
    private static final String PREFIX_URL = "https://example.org";
    private static final UrlValidator VALIDATOR = new UrlValidator(UrlValidator.ALLOW_ALL_SCHEMES);

    public static boolean isUriValid(String uri) {
        return VALIDATOR.isValid(PREFIX_URL + uri);
    }

    private UriUtils() {
    }
}
