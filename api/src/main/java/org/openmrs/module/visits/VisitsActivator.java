/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of
 * the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * OpenMRS is also distributed under the terms of the Healthcare Disclaimer located at
 * http://openmrs.org/license. Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the
 * OpenMRS graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.visits;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.VisitAttributeType;
import org.openmrs.api.APIException;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.DaemonTokenAware;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.module.visits.api.exception.VisitsRuntimeException;
import org.openmrs.module.visits.api.scheduler.job.JobRepeatInterval;
import org.openmrs.module.visits.api.scheduler.job.MissedVisitsStatusChangerJobDefinition;
import org.openmrs.module.visits.api.service.ConfigService;
import org.openmrs.module.visits.api.service.JobSchedulerService;
import org.openmrs.module.visits.api.tag.VisitNoteMetaTagHandler;
import org.openmrs.module.visits.api.util.ConfigConstants;
import org.openmrs.module.visits.api.util.GlobalPropertiesConstants;
import org.openmrs.module.visits.api.util.GlobalPropertyUtils;
import org.openmrs.module.visits.api.util.VisitsConstants;

import java.util.Arrays;

/**
 * This class contains the logic that is run every time this module is either started or shutdown
 */
public class VisitsActivator extends BaseModuleActivator implements DaemonTokenAware {

  private static final Log LOGGER = LogFactory.getLog(VisitsActivator.class);
  private static final String SCHEDULER_SERVICE_BEAN = "cfl.jobSchedulerService";

  /** @see #started() */
  @Override
  public void started() {
    LOGGER.info("Started Visits Module");
    try {
      createGlobalProperties();
      createVisitTimeAttributeType();
      createVisitStatusAttributeType();
      createMissedVisitStatusChangerConfig();
      scheduleMissedVisitsStatusChangerJob();
      configureDistribution();
    } catch (APIException e) {
      safeShutdownModule();
      throw new VisitsRuntimeException("Failed to setup the required modules", e);
    }
  }

  /** @see #shutdown() */
  public void shutdown() {
    LOGGER.info("Shutdown Visits Module");
  }

  /** @see #stopped() */
  @Override
  public void stopped() {
    LOGGER.info("Stopped Visits Module");
  }

  @Override
  public void setDaemonToken(DaemonToken token) {
    LOGGER.info("Set daemon token to Visits Module event listeners");
    getSchedulerService().setDaemonToken(token);
  }

  private void createGlobalProperties() {
    GlobalPropertyUtils.createGlobalSettingsIfNotExists(
        Arrays.asList(
            GlobalPropertiesConstants.VISIT_TIMES,
            GlobalPropertiesConstants.VISIT_STATUSES,
            GlobalPropertiesConstants.PAST_VISITS_LIMIT,
            GlobalPropertiesConstants.UPCOMING_VISITS_LIMIT,
            GlobalPropertiesConstants.ENCOUNTER_DATETIME_VALIDATION,
            GlobalPropertiesConstants.MISSED_VISIT_CHANGER_CREATION_GP));
  }

  private void createMissedVisitStatusChangerConfig() {
    GlobalPropertyUtils.createGlobalSettingsIfNotExists(
        Arrays.asList(
            GlobalPropertiesConstants.MINIMUM_VISIT_DELAY_TO_MARK_IT_AS_MISSED,
            GlobalPropertiesConstants.STATUSES_ENDING_VISIT,
            GlobalPropertiesConstants.STATUS_OF_MISSED_VISIT,
            GlobalPropertiesConstants.STATUS_OF_OCCURRED_VISIT));
  }

  private void configureDistribution() {
    disableUnusedApps(Context.getService(AppFrameworkService.class));
  }

  private void disableUnusedApps(AppFrameworkService appFrameworkService) {
    if (appFrameworkService.getApp(ConfigConstants.COREAPPS_RECENT_VISITS_FRAGMENT) != null) {
      appFrameworkService.disableApp(ConfigConstants.COREAPPS_RECENT_VISITS_FRAGMENT);
    }
  }

  public void contextRefreshed() {
    if (ModuleFactory.isModuleStarted("htmlformentry")) {
      HtmlFormEntryService htmlFormEntryService = Context.getService(HtmlFormEntryService.class);
      htmlFormEntryService.addHandler("visitNoteMetaTag", new VisitNoteMetaTagHandler());
    }
  }

  private void createVisitStatusAttributeType() {
    VisitAttributeType type = new VisitAttributeType();
    type.setName(ConfigConstants.VISIT_STATUS_ATTRIBUTE_TYPE_NAME);
    type.setDatatypeClassname(ConfigConstants.VISIT_STATUS_ATTRIBUTE_TYPE_DATATYPE);
    type.setDescription(ConfigConstants.VISIT_STATUS_ATTRIBUTE_TYPE_DESCRIPTION);
    type.setUuid(ConfigConstants.VISIT_STATUS_ATTRIBUTE_TYPE_UUID);
    createVisitAttributeTypeIfNotExists(ConfigConstants.VISIT_STATUS_ATTRIBUTE_TYPE_UUID, type);
  }

  private void createVisitTimeAttributeType() {
    VisitAttributeType type = new VisitAttributeType();
    type.setName(ConfigConstants.VISIT_TIME_ATTRIBUTE_TYPE_NAME);
    type.setDatatypeClassname(ConfigConstants.VISIT_TIME_ATTRIBUTE_TYPE_DATATYPE);
    type.setDescription(ConfigConstants.VISIT_TIME_ATTRIBUTE_TYPE_DESCRIPTION);
    type.setUuid(ConfigConstants.VISIT_TIME_ATTRIBUTE_TYPE_UUID);
    createVisitAttributeTypeIfNotExists(ConfigConstants.VISIT_TIME_ATTRIBUTE_TYPE_UUID, type);
  }

  private void createVisitAttributeTypeIfNotExists(String uuid, VisitAttributeType attributeType) {
    VisitService visitService = Context.getVisitService();
    VisitAttributeType actual = visitService.getVisitAttributeTypeByUuid(uuid);
    if (actual == null) {
      visitService.saveVisitAttributeType(attributeType);
    }
  }

  private void scheduleMissedVisitsStatusChangerJob() {
    if (getConfigService().isMissedVisitChangerJobShouldBeCreated()) {
      getSchedulerService()
          .rescheduleOrCreateNewTask(
              new MissedVisitsStatusChangerJobDefinition(), JobRepeatInterval.DAILY);
    }
  }

  private void safeShutdownModule() {
    Module mod = ModuleFactory.getModuleById(ConfigConstants.MODULE_ID);
    ModuleFactory.stopModule(mod);
  }

  private JobSchedulerService getSchedulerService() {
    return Context.getRegisteredComponent(SCHEDULER_SERVICE_BEAN, JobSchedulerService.class);
  }

  private ConfigService getConfigService() {
    return Context.getRegisteredComponent(
        VisitsConstants.VISIT_CONFIG_SERVICE_BEAN_NAME, ConfigService.class);
  }
}
