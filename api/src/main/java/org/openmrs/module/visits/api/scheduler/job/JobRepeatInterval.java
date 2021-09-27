package org.openmrs.module.visits.api.scheduler.job;

import org.openmrs.module.visits.api.util.DateUtil;

/**
 * Enum representing an interval between job execution
 */
public enum JobRepeatInterval {
    DAILY(DateUtil.DAY_IN_SECONDS),
    HOURLY(DateUtil.HOUR_IN_SECONDS),
    NEVER(0);

    /**
     * Job repeat interval enum constructor
     *
     * @param seconds job execution interval expressed in seconds
     */
    JobRepeatInterval(long seconds) {
        this.seconds = seconds;
    }

    private long seconds;

    /**
     * @return job execution interval expressed in seconds
     */
    public long getSeconds() {
        return seconds;
    }
}
