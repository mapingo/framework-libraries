package uk.gov.justice.services.ejb.timer;

import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;

public class TimerServiceManager {

    @Inject
    private TimerConfigFactory timerConfigFactory;

    public Timer createIntervalTimer(
            final String timerJobName,
            final long timerStartWaitMilliseconds,
            final long timerIntervalMilliseconds,
            final TimerService timerService) {
        final TimerConfig timerConfig = timerConfigFactory.createNew();

        timerConfig.setPersistent(false);
        timerConfig.setInfo(timerJobName);

        return timerService.createIntervalTimer(timerStartWaitMilliseconds, timerIntervalMilliseconds, timerConfig);
    }

    public Timer createSingleActionTimer(
            final String timerJobName,
            final long duration,
            final TimerService timerService) {
        final TimerConfig timerConfig = timerConfigFactory.createNew();

        timerConfig.setPersistent(false);
        timerConfig.setInfo(timerJobName);

        return timerService.createSingleActionTimer(duration, timerConfig);
    }
}
