package io.github.rodrigovsilva.tempus;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

/**
 * This PointInTime class represents a specific microsecond in time
 * specified in UTC. It can serialize to and deserialize from the timestamp
 * format used for messages.
 */
public final class PointInTime {
    private static final DateTimeFormatter MESSAGE_SERIALIZATION_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSZ");
    private static final DateTimeFormatter MYSQL_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
    private static final DateTimeFormatter RFC3339_WITH_MICROSECONDS =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS[xxx]");

    private static final ZoneOffset zoneOffset = ZoneOffset.UTC;

    private final ZonedDateTime timestamp;

    private PointInTime(ZonedDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Create a PointInTime from a ZonedDateTime. The ZonedDateTime must already
     * be represented in UTC format. This ensures that no implicit time conversions
     * are occurring.
     */
    public static PointInTime fromZonedDateTime(ZonedDateTime timestamp) {
        if (!timestamp.getZone().equals(zoneOffset)) {
            throw CanNotCreatePointInTimeException.fromTimestampWithIncompatibleZone(timestamp, zoneOffset);
        }
        return new PointInTime(timestamp);
    }

    public static PointInTime from(final Instant instant) {
        return new PointInTime(ZonedDateTime.ofInstant(instant, zoneOffset));
    }

    public PointInTime plus(int amount, ChronoUnit unit) {
        return new PointInTime(timestamp.plus(amount, unit));
    }

    public boolean equals(PointInTime that) {
        return this.timestamp.equals(that.timestamp);
    }

    public boolean isBefore(PointInTime that) {
        return this.timestamp.isBefore(that.timestamp);
    }

    public boolean isAfter(PointInTime that) {
        return this.timestamp.isAfter(that.timestamp);
    }

    public Duration durationTill(PointInTime that) {
        return Duration.between(this.toZonedDateTime(), that.toZonedDateTime());
    }

    public LocalDate localDate() {
        return timestamp.toLocalDate();
    }

    public ZonedDateTime toZonedDateTime() {
        return timestamp;
    }

    public Instant toInstant() {
        return timestamp.toInstant();
    }

    /**
     * Create a PointInTime from a string formatted for message serialization.
     * eg. "2024-03-07 10:28:55.437286+0000"
     */
    public static PointInTime fromSerializedString(String messageSerializedTimestamp)
            throws CanNotCreatePointInTimeException {
        ZonedDateTime dateTime;

        try {
            dateTime = ZonedDateTime.parse(messageSerializedTimestamp, MESSAGE_SERIALIZATION_FORMAT);
        } catch (DateTimeParseException cause) {
            throw CanNotCreatePointInTimeException.fromInvalidStringFormat(messageSerializedTimestamp, cause);
        }

        return PointInTime.fromZonedDateTime(dateTime);
    }

    /**
     * Create a PointInTime from a RFC3339 string format with offset
     * eg. "2024-03-07T0:28:55.437286+00:00"
     */
    public static PointInTime fromRFC3339SerializedString(String messageSerializedTimestamp)
            throws CanNotCreatePointInTimeException {
        ZonedDateTime dateTime;

        try {
            dateTime = ZonedDateTime.parse(messageSerializedTimestamp, RFC3339_WITH_MICROSECONDS);
        } catch (DateTimeParseException cause) {
            throw CanNotCreatePointInTimeException.fromInvalidStringFormat(messageSerializedTimestamp, cause);
        }

        return PointInTime.fromZonedDateTime(dateTime);
    }

    /**
     * Create a PointInTime from a string formatted for mysql datetime serialization.
     * eg. "2024-03-07 10:28:55.437286"
     */
    public static PointInTime fromMysqlDateTimeString(String mysqlDateTimeString) {
        ZonedDateTime dateTime;

        try {
            dateTime = LocalDateTime.parse(mysqlDateTimeString, MYSQL_DATE_TIME_FORMAT)
                    .atZone(ZoneOffset.UTC);
        } catch (DateTimeParseException cause) {
            throw CanNotCreatePointInTimeException.fromInvalidStringFormat(mysqlDateTimeString, cause);
        }

        return PointInTime.fromZonedDateTime(dateTime);
    }

    /**
     * Create a PointInTime from an ISO 8601 string format
     * eg. "1985-04-12T23:20:50.52Z"
     */
    public static PointInTime fromIso8601String(String messageSerializedTimestamp)
            throws CanNotCreatePointInTimeException {
        ZonedDateTime dateTime;

        try {
            dateTime = ZonedDateTime.parse(messageSerializedTimestamp);
        } catch (DateTimeParseException cause) {
            throw CanNotCreatePointInTimeException.fromInvalidStringFormat(messageSerializedTimestamp, cause);
        }

        return PointInTime.fromZonedDateTime(dateTime);
    }

    /**
     * Return a string representation of the point in time in the message serialization
     * format.
     * eg. "2024-03-07 10:28:55.437286+0000"
     */
    public String toSerializedString() {
        return MESSAGE_SERIALIZATION_FORMAT.withZone(zoneOffset).format(timestamp);
    }

    /**
     * Return a string representation of the point in time in MySQL's DateTime
     * string format. (UTC)
     * eg. "2024-03-07 10:28:55.437286"
     */
    public String toMysqlDateTimeString() {
        return MYSQL_DATE_TIME_FORMAT.withZone(zoneOffset).format(timestamp);
    }

    /**
     * Return a string representation of the point in time in RFC3339
     * string format. (UTC)
     * eg. "2024-03-07T10:28:55.437286+00:00"
     */
    public String toRFC3339DateTimeString() {
        return RFC3339_WITH_MICROSECONDS.format(timestamp);
    }

    @Override
    public String toString() {
        return toSerializedString();
    }
}
