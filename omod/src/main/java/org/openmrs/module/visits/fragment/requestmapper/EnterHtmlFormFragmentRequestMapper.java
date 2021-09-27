/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * <p>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p>
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.visits.fragment.requestmapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.visits.api.service.ConfigService;
import org.openmrs.module.visits.api.util.GlobalPropertiesConstants;
import org.openmrs.ui.framework.fragment.FragmentRequest;
import org.openmrs.ui.framework.fragment.FragmentRequestMapper;
import org.springframework.stereotype.Component;

/**
 * Allows to override default fragment request handling
 */
@Component
public class EnterHtmlFormFragmentRequestMapper implements FragmentRequestMapper {

    private static final Log LOGGER = LogFactory.getLog(EnterHtmlFormFragmentRequestMapper.class);

    private static final String OLD_PROVIDER = "htmlformentryui";

    private static final String OLD_FRAGMENT = "htmlform/enterHtmlForm";

    public static final String NEW_PROVIDER = "visits";

    public static final String NEW_FRAGMENT = "visitEnterHtmlForm";

    /**
     * Method used to determine if custom handling should be used. When encounter datetime validation is disabled,
     * the default fragment provider (handler) is overridden by the custom one.
     *
     * @param request object representing a fragment request
     * @return true if the request should be mapped with custom handler
     */
    @Override
    public boolean mapRequest(FragmentRequest request) {
        if (isHtmlFormFragment(request) && isEncounterDatetimeValidationDisabled()) {
            LOGGER.info(String.format("The encounter datetime validation is disabled (see %s global setting). "
                    + "The `%s` fragment from `%s` provider will be change to `%s` fragment from `%s` provider.",
                    GlobalPropertiesConstants.ENCOUNTER_DATETIME_VALIDATION.getKey(), OLD_FRAGMENT, OLD_PROVIDER,
                    NEW_FRAGMENT, NEW_PROVIDER));
            request.setProviderNameOverride(NEW_PROVIDER);
            request.setFragmentIdOverride(NEW_FRAGMENT);
            return true;
        }
        return false;
    }

    private boolean isEncounterDatetimeValidationDisabled() {
        return !getVisitConfigService().isEncounterDatetimeValidationEnabled();
    }

    private boolean isHtmlFormFragment(FragmentRequest request) {
        return request.getProviderName().equals(OLD_PROVIDER)
                && request.getFragmentId().equals(OLD_FRAGMENT);
    }

    private ConfigService getVisitConfigService() {
        return Context.getRegisteredComponent("visits.configService", ConfigService.class);
    }
}
