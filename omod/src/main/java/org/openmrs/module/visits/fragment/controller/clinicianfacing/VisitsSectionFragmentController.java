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

package org.openmrs.module.visits.fragment.controller.clinicianfacing;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.module.appframework.context.AppContextModel;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appframework.template.TemplateFactory;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.coreapps.CoreAppsProperties;
import org.openmrs.module.coreapps.contextmodel.PatientContextModel;
import org.openmrs.module.coreapps.contextmodel.VisitContextModel;
import org.openmrs.module.coreapps.utils.VisitTypeHelper;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.openmrs.module.visits.api.util.ConfigConstants;
import org.openmrs.module.visits.api.util.DateUtils;
import org.openmrs.module.visits.api.util.GlobalPropertiesConstants;
import org.openmrs.module.visits.api.util.GlobalPropertyUtils;
import org.openmrs.module.visits.util.ComparatorsHelper;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.page.PageModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Supports the containing PageModel having an "app" property whose config defines a "visitUrl" property
 */

@SuppressWarnings({"checkstyle:ParameterAssignment", "PMD.AvoidReassigningParameters"})
public class VisitsSectionFragmentController {

    private static final String PATIENT = "patient";
    private static final String VISIT = "visit";
    private static final String PATIENT_ID = "patientId";
    private static final String VISIT_URL = "visitUrl";
    private static final String EDIT_PAGE_URL = "editPageUrl";

    /**
     * Method used by OpenMRS for preparing the controller of Visit Section Fragment
     *
     * @param config fragment configuration object to initialize
     * @param pageModel represents page model
     * @param model represents fragment model
     * @param ui object containing UI util methods
     * @param sessionContext UI session context
     * @param templateFactory OpenMRS template factory
     * @param coreAppsProperties OpenMRS core apps properties
     * @param patientWrapper OpenMRS patient domain wrapper
     * @param adtService OpenMRS adt service
     * @param visitTypeHelper OpenMRS visit type helper
     */
    @SuppressWarnings({"checkstyle:parameterNumber", "PMD.ExcessiveParameterList"})
    public void controller(FragmentConfiguration config,
                           PageModel pageModel,
                           FragmentModel model,
                           UiUtils ui,
                           UiSessionContext sessionContext,
                           @SpringBean("appframeworkTemplateFactory") TemplateFactory templateFactory,
                           @SpringBean("coreAppsProperties") CoreAppsProperties coreAppsProperties,
                           @InjectBeans PatientDomainWrapper patientWrapper,
                           @SpringBean("adtService") AdtService adtService,
                           @SpringBean("visitTypeHelper") VisitTypeHelper visitTypeHelper) {
        config.require(PATIENT);
        Object patient = config.get(PATIENT);

        if (patient instanceof Patient) {
            patientWrapper.setPatient((Patient) patient);
            config.addAttribute(PATIENT, patientWrapper);
        } else if (patient instanceof PatientDomainWrapper) {
            patientWrapper = (PatientDomainWrapper) patient;
        }

        AppContextModel contextModel = sessionContext.generateAppContextModel();
        contextModel.put(PATIENT, new PatientContextModel(patientWrapper.getPatient()));
        contextModel.put(PATIENT_ID, patientWrapper.getPatient().getUuid());
        // backwards-compatible for links that still specify patient uuid substitution with "{{patientId}}"

        AppDescriptor app = (AppDescriptor) pageModel.get("app");

        // see if the app specifies urls to use
        String visitsPageWithSpecificVisitUrl = config.get(VISIT_URL).toString();
        visitsPageWithSpecificVisitUrl = "/" + ui.contextPath() + "/" + visitsPageWithSpecificVisitUrl;
        String visitsPageUrl = config.get(EDIT_PAGE_URL).toString();

        // hack fix for RA-1002--if there is an active visit,
        // and we are using the "regular" visit dashboard we actually want to link to the specific visit
        Location visitLocation = adtService.getLocationThatSupportsVisits(sessionContext.getSessionLocation());
        VisitDomainWrapper activeVisit = adtService.getActiveVisit(patientWrapper.getPatient(), visitLocation);
        if (visitsPageUrl.contains("/coreapps/patientdashboard/patientDashboard.page?") && activeVisit != null) {
            visitsPageUrl = coreAppsProperties.getVisitsPageWithSpecificVisitUrl();
            contextModel.put(VISIT, activeVisit.getVisit());
        }

        visitsPageUrl = "/" + ui.contextPath() + "/" + visitsPageUrl;
        model.addAttribute(EDIT_PAGE_URL, templateFactory.handlebars(visitsPageUrl, contextModel));

        List<VisitDomainWrapper> allVisits = patientWrapper.getAllVisitsUsingWrappers();
        List<VisitDomainWrapper> visitsToDisplay = getPastVisits(allVisits);
        visitsToDisplay.addAll(getUpcomingVisits(allVisits));

        Map<VisitDomainWrapper, Map<String, String>> recentVisitsWithLinks
                = new LinkedHashMap<VisitDomainWrapper, Map<String, String>>();
        for (VisitDomainWrapper visit : visitsToDisplay) {
            contextModel.put(VISIT, new VisitContextModel(visit));
            // since the "visit" isn't part of the context module, we bind it first to the visit url,
            // before doing the handlebars binding against the context
            recentVisitsWithLinks.put(visit,
                    getVisitParams(templateFactory, ui, visitsPageWithSpecificVisitUrl, visit, contextModel));
        }
        model.addAttribute("recentVisitsWithLinks", recentVisitsWithLinks);

        config.addAttribute("showVisitTypeOnPatientHeaderSection",
                visitTypeHelper.showVisitTypeOnPatientHeaderSection());
    }

    private Map<String, String> getVisitParams(TemplateFactory templateFactory, UiUtils ui,
                                               String visitsPageWithSpecificVisitUrl, VisitDomainWrapper visit,
                                               AppContextModel contextModel) {
        Map<String, String> result = new HashMap<>();
        result.put("url", templateFactory.handlebars(ui.urlBind(visitsPageWithSpecificVisitUrl,
                visit.getVisit()), contextModel));
        Object status = visit.getVisitAttribute(ConfigConstants.VISIT_STATUS_ATTRIBUTE_TYPE_UUID);
        if (status != null) {
            result.put("status", status.toString());
        }
        return result;
    }

    private List<VisitDomainWrapper> getUpcomingVisits(List<VisitDomainWrapper> allVisits) {
        List<VisitDomainWrapper> result = new ArrayList<>();
        for (VisitDomainWrapper visit : allVisits) {
            if (DateUtils.isTodayOrAfter(visit.getStartDate())) {
                result.add(visit);
            }
        }
        result.sort(ComparatorsHelper.getVisitsComparatorByStartDateAsc());
        return getLimitedResult(result, GlobalPropertyUtils.getInteger(
                GlobalPropertiesConstants.UPCOMING_VISITS_LIMIT.getKey()));
    }

    private List<VisitDomainWrapper> getPastVisits(List<VisitDomainWrapper> allVisits) {
        List<VisitDomainWrapper> result = new ArrayList<>();
        for (VisitDomainWrapper visit : allVisits) {
            if (DateUtils.isBeforeToday(visit.getStartDate())) {
                result.add(visit);
            }
        }
        result.sort(ComparatorsHelper.getVisitsComparatorByStartDateDesc());
        return getLimitedResult(result, GlobalPropertyUtils.getInteger(
                GlobalPropertiesConstants.PAST_VISITS_LIMIT.getKey()));
    }

    private List<VisitDomainWrapper> getLimitedResult(List<VisitDomainWrapper> visits, int limit) {
        if (visits.size() > limit) {
            return visits.subList(0, limit);
        } else {
            return visits;
        }
    }
}
