/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

/**
 * The contents of this file are subject to the OpenMRS Public License Version 1.0 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://license.openmrs.org
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis, WITHOUT WARRANTY OF
 * ANY KIND, either express or implied. See the License for the specific language governing rights
 * and limitations under the License.
 *
 * <p>Copyright (C) OpenMRS, LLC. All Rights Reserved.
 */
package org.openmrs.module.visits.fragment.controller.clinicianfacing;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.attribute.BaseAttribute;
import org.openmrs.module.appframework.context.AppContextModel;
import org.openmrs.module.appframework.template.TemplateFactory;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.coreapps.CoreAppsProperties;
import org.openmrs.module.coreapps.contextmodel.PatientContextModel;
import org.openmrs.module.coreapps.contextmodel.VisitContextModel;
import org.openmrs.module.coreapps.utils.VisitTypeHelper;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.openmrs.module.visits.api.dto.VisitDTO;
import org.openmrs.module.visits.api.service.ConfigService;
import org.openmrs.module.visits.api.util.ConfigConstants;
import org.openmrs.module.visits.api.util.DateUtil;
import org.openmrs.module.visits.api.util.DateUtils;
import org.openmrs.module.visits.api.util.GlobalPropertiesConstants;
import org.openmrs.module.visits.api.util.GlobalPropertyUtil;
import org.openmrs.module.visits.api.util.GlobalPropertyUtils;
import org.openmrs.module.visits.api.util.VisitsConstants;
import org.openmrs.module.visits.util.ComparatorsHelper;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Supports the containing PageModel having an "app" property whose config defines a "visitUrl"
 * property
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
   * @param model represents fragment model
   * @param ui object containing UI util methods
   * @param sessionContext UI session context
   * @param patientWrapper OpenMRS patient domain wrapper
   */
  public void controller(
      FragmentConfiguration config,
      FragmentModel model,
      UiUtils ui,
      UiSessionContext sessionContext,
      @InjectBeans PatientDomainWrapper patientWrapper) {

    TemplateFactory templateFactory =
        Context.getRegisteredComponent("appframeworkTemplateFactory", TemplateFactory.class);
    AdtService adtService = Context.getService(AdtService.class);
    VisitTypeHelper visitTypeHelper =
        Context.getRegisteredComponent("visitTypeHelper", VisitTypeHelper.class);

    config.require(PATIENT);
    Object patient = config.get(PATIENT);

    if (patient instanceof Patient) {
      patientWrapper.setPatient((Patient) patient);
    } else if (patient instanceof PatientDomainWrapper) {
      patientWrapper = (PatientDomainWrapper) patient;
    }
    config.addAttribute(PATIENT, patientWrapper.getPatient());

    AppContextModel contextModel = sessionContext.generateAppContextModel();
    contextModel.put(PATIENT, new PatientContextModel(patientWrapper.getPatient()));
    contextModel.put(PATIENT_ID, patientWrapper.getPatient().getUuid());
    // backwards-compatible for links that still specify patient uuid substitution with
    // "{{patientId}}"

    // see if the app specifies urls to use
    String visitsPageWithSpecificVisitUrl = config.get(VISIT_URL).toString();
    visitsPageWithSpecificVisitUrl = "/" + ui.contextPath() + "/" + visitsPageWithSpecificVisitUrl;
    String visitsPageUrl = config.get(EDIT_PAGE_URL).toString();

    // hack fix for RA-1002--if there is an active visit,
    // and we are using the "regular" visit dashboard we actually want to link to the specific visit
    Location visitLocation =
        adtService.getLocationThatSupportsVisits(sessionContext.getSessionLocation());
    VisitDomainWrapper activeVisit =
        adtService.getActiveVisit(patientWrapper.getPatient(), visitLocation);
    if (visitsPageUrl.contains("/coreapps/patientdashboard/patientDashboard.page?")
        && activeVisit != null) {
      visitsPageUrl =
          Context.getRegisteredComponent("coreAppsProperties", CoreAppsProperties.class)
              .getVisitsPageWithSpecificVisitUrl();
      contextModel.put(VISIT, activeVisit.getVisit());
    }

    visitsPageUrl = "/" + ui.contextPath() + "/" + visitsPageUrl;
    model.addAttribute(EDIT_PAGE_URL, templateFactory.handlebars(visitsPageUrl, contextModel));

    List<VisitDomainWrapper> allVisits = patientWrapper.getAllVisitsUsingWrappers();
    List<VisitDomainWrapper> visitsToDisplay = getPastVisits(allVisits);
    visitsToDisplay.addAll(getUpcomingVisits(allVisits));

    Map<VisitDomainWrapper, Map<String, Object>> recentVisitsWithLinks =
        new LinkedHashMap<>(visitsToDisplay.size());
    for (VisitDomainWrapper visit : visitsToDisplay) {
      contextModel.put(VISIT, new VisitContextModel(visit));
      // since the "visit" isn't part of the context module, we bind it first to the visit url,
      // before doing the handlebars binding against the context
      recentVisitsWithLinks.put(
          visit,
          getVisitParams(templateFactory, ui, visitsPageWithSpecificVisitUrl, visit, contextModel));
    }
    model.addAttribute("recentVisitsWithLinks", recentVisitsWithLinks);
    config.addAttribute(
        "showVisitTypeOnPatientHeaderSection",
        visitTypeHelper.showVisitTypeOnPatientHeaderSection());

    model.addAttribute(
        "isExtraInfoDialogEnabled",
        GlobalPropertyUtil.parseBool(
            GlobalPropertyUtils.getGlobalProperty(
                GlobalPropertiesConstants.SCHEDULE_VISIT_EXTRA_INFORMATION_GP.getKey())));
    model.addAttribute(
        "holidayWeekdays",
        GlobalPropertyUtils.getGlobalProperty(
            GlobalPropertiesConstants.VISITS_HOLIDAY_WEEKDAYS_GP.getKey()));
    model.addAttribute("locale", Context.getLocale().toLanguageTag().replace('_', '-'));

    List<String> stringVisitDates =
        allVisits.stream()
            .map(VisitDomainWrapper::getStartDatetime)
            .map(
                v ->
                    DateUtil.convertDateWithLocale(
                        v, VisitsConstants.DEFAULT_SERVER_SIDE_DATE_FORMAT, null))
            .collect(Collectors.toList());

    String commaSeparatedVisitDates = String.join(",", stringVisitDates);
    model.addAttribute("commaSeparatedVisitDates", commaSeparatedVisitDates);

    addAttributesForEditVisitWidget(model);
  }

  private Map<String, Object> getVisitParams(
      TemplateFactory templateFactory,
      UiUtils ui,
      String visitsPageWithSpecificVisitUrl,
      VisitDomainWrapper visit,
      AppContextModel contextModel) {
    Map<String, Object> result = new HashMap<>();
    result.put(
        "url",
        templateFactory.handlebars(
            ui.urlBind(visitsPageWithSpecificVisitUrl, visit.getVisit()), contextModel));
    VisitDTO visitDTO = new VisitDTO();
    Object status = visit.getVisitAttribute(ConfigConstants.VISIT_STATUS_ATTRIBUTE_TYPE_UUID);
    if (status != null) {
      result.put("status", status.toString());
      visitDTO.setStatus(status.toString());
      result.put("statusIconColor", getStatusIconColor(status.toString()));
    }

    Visit extractedVisit = visit.getVisit();
    Location visitLocation = extractedVisit.getLocation();
    VisitType visitType = extractedVisit.getVisitType();
    visitDTO
        .setStartDate(extractedVisit.getStartDatetime())
        .setTime(getVisitTime(extractedVisit))
        .setLocation(visitLocation != null ? visitLocation.getName() : null)
        .setType(visitType != null ? visitType.getName() : null)
        .setFormUri(
            Context.getService(ConfigService.class).getVisitFormUrisMap().getUri(extractedVisit))
        .setUuid(extractedVisit.getUuid());

    result.put("visitDetails", visitDTO);
    result.put("visitLocationUuid", visitLocation != null ? visitLocation.getUuid() : null);
    result.put("visitTypeUuid", visitType != null ? visitType.getUuid() : null);
    result.put("visitUuid", extractedVisit.getUuid());
    result.put(
        "visitDateInServerFormat",
        DateUtil.convertDateWithLocale(
            extractedVisit.getStartDatetime(),
            VisitsConstants.DEFAULT_SERVER_SIDE_DATE_FORMAT,
            null));
    result.put(
        "visitDateInDisplayFormat",
        DateUtil.convertDateWithLocale(
            extractedVisit.getStartDatetime(), "dd MMM yyyy", Context.getLocale()));
    result.put("isVisitHasEncounters", CollectionUtils.isNotEmpty(extractedVisit.getEncounters()));

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
    return getLimitedResult(
        result,
        GlobalPropertyUtils.getInteger(GlobalPropertiesConstants.UPCOMING_VISITS_LIMIT.getKey()));
  }

  private List<VisitDomainWrapper> getPastVisits(List<VisitDomainWrapper> allVisits) {
    List<VisitDomainWrapper> result = new ArrayList<>();
    for (VisitDomainWrapper visit : allVisits) {
      if (DateUtils.isBeforeToday(visit.getStartDate())) {
        result.add(visit);
      }
    }
    result.sort(ComparatorsHelper.getVisitsComparatorByStartDateDesc());
    List<VisitDomainWrapper> limitedVisits =
        getLimitedResult(
            result,
            GlobalPropertyUtils.getInteger(GlobalPropertiesConstants.PAST_VISITS_LIMIT.getKey()));
    Collections.reverse(limitedVisits);
    return limitedVisits;
  }

  private List<VisitDomainWrapper> getLimitedResult(List<VisitDomainWrapper> visits, int limit) {
    if (visits.size() > limit) {
      return visits.subList(0, limit);
    } else {
      return visits;
    }
  }

  private String getVisitTime(Visit visit) {
    return visit.getActiveAttributes().stream()
        .filter(
            visitAttribute ->
                ConfigConstants.VISIT_TIME_ATTRIBUTE_TYPE_NAME.equals(
                    visitAttribute.getAttributeType().getName()))
        .findFirst()
        .map(BaseAttribute::getValueReference)
        .orElse(StringUtils.EMPTY);
  }

  private void addAttributesForEditVisitWidget(FragmentModel model) {
    ConfigService configService = Context.getService(ConfigService.class);
    model.addAttribute("visitTimes", configService.getVisitTimes());
    model.addAttribute("visitStatuses", configService.getVisitStatuses());
    model.addAttribute("visitTypes", Context.getVisitService().getAllVisitTypes(false));
    model.addAttribute("visitLocations", getVisitLocations());
  }

  private List<Location> getVisitLocations() {
    LocationService locationService = Context.getLocationService();
    LocationTag visitLocationTag = locationService.getLocationTagByName("Visit Location");
    return locationService.getLocationsByTag(visitLocationTag);
  }

  private String getStatusIconColor(String visitStatus) {
    String statusIconColor = null;
    ConfigService configService = Context.getService(ConfigService.class);
    if (StringUtils.equalsIgnoreCase(visitStatus, configService.getVisitInitialStatus())) {
      statusIconColor = "orange";
    }

    if (configService.getMissedVisitStatuses().contains(visitStatus)) {
      statusIconColor = "red";
    }

    if (configService.getOccurredVisitStatues().contains(visitStatus)) {
      statusIconColor = "green";
    }

    return statusIconColor;
  }
}
