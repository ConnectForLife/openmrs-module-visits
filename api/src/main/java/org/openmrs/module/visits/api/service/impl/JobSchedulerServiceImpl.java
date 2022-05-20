/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.api.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Daemon;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.visits.api.exception.VisitsRuntimeException;
import org.openmrs.module.visits.api.scheduler.job.JobDefinition;
import org.openmrs.module.visits.api.scheduler.job.JobRepeatInterval;
import org.openmrs.module.visits.api.service.JobSchedulerService;
import org.openmrs.module.visits.api.util.DateUtil;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.SchedulerService;
import org.openmrs.scheduler.TaskDefinition;

import java.util.Date;

/**
 * Implements a functionality related to the job scheduling management
 */
public class JobSchedulerServiceImpl extends BaseOpenmrsService implements JobSchedulerService {

    private static final Log LOGGER = LogFactory.getLog(JobSchedulerServiceImpl.class);

    private SchedulerService schedulerService;

    private DaemonToken daemonToken;

    @Override
    public void rescheduleOrCreateNewTask(JobDefinition jobDefinition, JobRepeatInterval repeatInterval) {
        TaskDefinition previousTask = schedulerService.getTaskByName(jobDefinition.getTaskName());
        TaskDefinition newTask = prepareTask(jobDefinition, previousTask, repeatInterval.getSeconds());

        if (shouldBeExecuted(newTask, jobDefinition)) {
            newTask.setLastExecutionTime(DateUtil.now());
            executeJob(jobDefinition);
        }

        if (previousTask == null) {
            scheduleTask(newTask);
        } else {
            try {
                schedulerService.shutdownTask(previousTask);
                schedulerService.deleteTask(previousTask.getId());
                scheduleTask(newTask);
            } catch (SchedulerException ex) {
                LOGGER.error(ex);
            }
        }
    }

    @Override
    public void createNewTask(JobDefinition jobDefinition, Date startTime, JobRepeatInterval repeatInterval) {
        TaskDefinition previousTask = schedulerService.getTaskByName(jobDefinition.getTaskName());
        if (previousTask != null) {
            throw new VisitsRuntimeException(
                    String.format("A task with name %s has been " + "already scheduled", jobDefinition.getTaskName()));
        }

        TaskDefinition newTask = prepareTask(jobDefinition, null, startTime, repeatInterval.getSeconds());
        scheduleTask(newTask);
    }

    private void executeJob(JobDefinition jobDefinition) {
        LOGGER.info("Executing Job before scheduling: " + jobDefinition.getTaskName());
        Daemon.runInDaemonThread(jobDefinition::execute, daemonToken);
    }

    private void scheduleTask(TaskDefinition task) {
        try {
            schedulerService.saveTaskDefinition(task);
            schedulerService.scheduleTask(task);
        } catch (SchedulerException ex) {
            throw new VisitsRuntimeException(ex);
        }
    }

    private boolean isPrimaryTaskCreation(TaskDefinition task) {
        return task.getLastExecutionTime() == null;
    }

    private boolean shouldBeExecuted(TaskDefinition task, JobDefinition jobDefinition) {
        final boolean executeNow;

        if (DateUtil.now().after(task.getStartTime())) {
            if (isPrimaryTaskCreation(task)) {
                executeNow = jobDefinition.shouldStartAtFirstCreation();
            } else {
                executeNow = task.getLastExecutionTime().before(DateUtil.getDateSecondsAgo(task.getRepeatInterval()));
            }
        } else {
            executeNow = false;
        }

        return executeNow;
    }

    private TaskDefinition prepareTask(JobDefinition jobDefinition, TaskDefinition previousTask, long repeatInterval) {
        if (previousTask == null) {
            return prepareTask(jobDefinition, null, DateUtil.now(), repeatInterval);
        } else {
            return prepareTask(jobDefinition, previousTask.getLastExecutionTime(), previousTask.getStartTime(),
                    repeatInterval);
        }
    }

    private TaskDefinition prepareTask(JobDefinition jobDefinition, Date lastExecutionTime, Date startTime,
                                       long repeatInterval) {
        TaskDefinition task = new TaskDefinition();
        task.setName(jobDefinition.getTaskName());
        task.setLastExecutionTime(lastExecutionTime);
        task.setRepeatInterval(repeatInterval);
        task.setTaskClass(jobDefinition.getTaskClass().getName());
        task.setStartTime(startTime);
        task.setStartOnStartup(Boolean.FALSE);
        task.setProperties(jobDefinition.getProperties());
        return task;
    }

    public void setSchedulerService(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    public void setDaemonToken(DaemonToken daemonToken) {
        this.daemonToken = daemonToken;
    }
}
