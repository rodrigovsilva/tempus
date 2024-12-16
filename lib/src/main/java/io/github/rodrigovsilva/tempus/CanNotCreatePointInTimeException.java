package io.github.rodrigovsilva.tempus;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public final class CanNotCreatePointInTimeException extends TimeException {
    private CanNotCreatePointInTimeException(String message, Exception cause) {
        super(message, cause);
    }

    public static CanNotCreatePointInTimeException fromTimestampWithIncompatibleZone(
            ZonedDateTime timestamp, ZoneOffset expectedOffset) {
        return new CanNotCreatePointInTimeException(
                "Attempted to create a PointInTime with an incompatible Zone offset. Expected: "
                        + expectedOffset.toString() + ", but received: "
                        + timestamp.getZone().toString() + ".",
                null);
    }

    public static CanNotCreatePointInTimeException fromInvalidStringFormat(String timestampString, Exception previous) {
        return new CanNotCreatePointInTimeException(
                "Attempted to create a PointInTime from an invalidly formatted timestamp string. Received: "
                        + timestampString + ".",
                previous);
    }
}
