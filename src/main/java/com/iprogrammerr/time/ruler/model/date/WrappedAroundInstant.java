package com.iprogrammerr.time.ruler.model.date;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class WrappedAroundInstant {

    private static final int MAX_DAY_SECONDS = (int) TimeUnit.DAYS.toSeconds(1);
    private final int utcOffset;

    public WrappedAroundInstant(int utcOffset) {
        this.utcOffset = utcOffset;
    }

    public Instant fromUtc(Instant time) {
        return adjusted(time.plusSeconds(utcOffset).getEpochSecond());
    }

    private Instant adjusted(long seconds) {
        if (seconds > MAX_DAY_SECONDS) {
            seconds -= MAX_DAY_SECONDS;
        } else if (seconds < 0) {
            seconds += MAX_DAY_SECONDS;
        }
        return Instant.ofEpochSecond(seconds);
    }

    public Instant toUtc(Instant time) {
        return adjusted(time.plusSeconds(-utcOffset).getEpochSecond());
    }
}
