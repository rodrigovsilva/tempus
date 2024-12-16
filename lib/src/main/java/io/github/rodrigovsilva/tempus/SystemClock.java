package io.github.rodrigovsilva.tempus;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * This Clock implementation utilizes the system time to return
 * the current time in UTC as a PointInTime.
 * For testing purposes, use FixedClock when required.
 */
public final class SystemClock implements Clock {
    @Override
    public PointInTime now() {
        return PointInTime.fromZonedDateTime(ZonedDateTime.now(ZoneOffset.UTC));
    }
}
