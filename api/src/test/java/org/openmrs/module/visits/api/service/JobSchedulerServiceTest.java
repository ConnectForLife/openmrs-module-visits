package org.openmrs.module.visits.api.service;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.openmrs.api.context.Daemon;
import org.openmrs.module.visits.ContextMockedTest;
import org.openmrs.module.visits.api.exception.VisitsRuntimeException;
import org.openmrs.module.visits.api.scheduler.job.JobDefinition;
import org.openmrs.module.visits.api.scheduler.job.JobRepeatInterval;
import org.openmrs.module.visits.api.scheduler.job.MissedVisitsStatusChangerJobDefinition;
import org.openmrs.module.visits.api.service.impl.JobSchedulerServiceImpl;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.TaskDefinition;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class JobSchedulerServiceTest extends ContextMockedTest {

    @Spy
    private JobDefinition jobDefinition = new MissedVisitsStatusChangerJobDefinition();

    @InjectMocks
    private JobSchedulerService jobSchedulerService = new JobSchedulerServiceImpl();

    @Test
    public void rescheduleOrCreateNewTask() {
        AtomicInteger taskExecutionTriggeringCount = new AtomicInteger(0);
        doReturn(new TaskDefinition())
                .when(getSchedulerService()).getTaskByName(any());
        when(Daemon.runInDaemonThread(any(), any())).then(invocationOnMock -> {
            taskExecutionTriggeringCount.incrementAndGet();
            return new Thread();
        });

        jobSchedulerService.rescheduleOrCreateNewTask(jobDefinition, JobRepeatInterval.DAILY);
        assertEquals(1, taskExecutionTriggeringCount.get());
    }

    @Test(expected = VisitsRuntimeException.class)
    public void shouldNotCreateTaskIfAlreadyExist() {
        AtomicInteger taskExecutionTriggeringCount = new AtomicInteger(0);
        doReturn(new TaskDefinition())
                .when(getSchedulerService()).getTaskByName(any());
        when(Daemon.runInDaemonThread(any(), any())).then(invocationOnMock -> {
            taskExecutionTriggeringCount.incrementAndGet();
            return new Thread();
        });

        jobSchedulerService.createNewTask(jobDefinition, new Date(), JobRepeatInterval.DAILY);
    }

    @Test
    public void shouldCreateNewTask() throws SchedulerException {
        doReturn(jobDefinition).when(getSchedulerService()).scheduleTask(any());
        doReturn(null)
                .when(getSchedulerService()).getTaskByName(any());

        jobSchedulerService.createNewTask(jobDefinition, new Date(), JobRepeatInterval.DAILY);
        verify(getSchedulerService(), times(1)).scheduleTask(any());
    }
}
