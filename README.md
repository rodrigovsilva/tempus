Time zone mistakes can cause operational incidents in any type of system and application, which depends on handling correct timestamps.
To combat that, and to make testing more convenient, we have introduced `Tempus`, a breakthrough time component.

This time component has introduced the following aspects:
1. constrain timestamps to UTC
2. standardized serialization formats
3. provides a simple interface for manipulating time which respects these concerns

> **Note:** To simplify all timestamp related behaviors, it has been determined that we will not allow any kind of implicit time-zone conversion.

## Clocks and Points in Time

The `Clock` interface should be injected wherever a new `PointInTime` must be created.
This ensures that the component is easily testable.

### Creating Timestamps

There is no timestamp `now()` method. Timestamps come only from the `Clock` interface or as a translation from a string representation.

#### From the Clock

The `Clock` interface is implemented by `SystemClock` for production purposes and one or more test doubles for testing purposes. The Clock is the recommended way to instantiate all timestamps.

> When using `Clock`, a valid UTC time is always provided. It's possible to make mistakes when creating a timestamp from a string, so that should be avoided where possible.

```java
class Example {
    private Clock clock;
    
    Example(Clock clock) {
        this.clock = clock;
    }

    void timestampFromTheClock() {
        // this will have the current time in the UTC time zone
        // for example, 2022-01-02 02:00:00
        var timestamp = clock.now();
    }
}
```

#### Serialization

We serialize into an 8601 formatted string with microsecond precision and timezone offset. (eg. "2020-01-02 03:04:05.000000+0000")

Timestamps are serialized into a specific format. This format is used by `PointInTime` for all string related methods.

> Note: Instantiating a timestamp from a string is possible and is often necessary. However, it's one of the moments in which a bug is most likely to be introduced.

```java
void serialization() {
    var timestamp = PointInTime.fromSerializedString("2020-01-02 03:04:05.123456+0000");

    // will contain "2020-01-02 03:04:05.123456+0000"
    var serialized = timestamp.toSerializedString();
}
```

> Note: String formats may be added / modified as the platform evolves.

### Comparison

PointInTime objects are considered equal if they represent the same precise microsecond in time.

```java
void comparison() {
    if (
        timestampOne.equals(timestampTwo)
    ) {
        // ...
    }
}
```

**Relative Comparison**

```java
void relativeComparison() {
    /*
     * Returns true if timeOne occurs before timeTwo.
     */
    timeOne.isBefore(timeTwo);

    /*
     * Returns true if timeOne occurs after timeTwo.
     */
    timeOne.isAfter(timeTwo);
}
```

## Date

Clocks only make available "points in time".
These `PointInTime` objects actually refer (by default) to a specific microsecond in a specific second, in a specific minute, in a specific hour, of a specific day.
It's very precise.
Sometimes you want to work on a Date and not a time.

> **Note:** Similarly to timestamps, time zone mistakes are easy with dates. So, it's best to get the timestamp from the clock and then derive the date from that.

### Getting the Current Date

Each `PointInTime` class can return a `LocalDate` object.

```java
void getTheDate() {
    LocalDate date = timestamp.localDate();
}
```

### Modify the Timestamp

```java
void addFifteenMinutes(PointInTime timestamp) {
    return timestamp.plus(15, ChronoUnit.MINUTES);
}
```

## Testing

Currently, we ship with `FixedClock` for isolation testing. You're welcome to develop your own `Clock` interface test double implementation, should it improve your testing.

```java
void testCanFixANewClock() {
    var clock = FixedClock.at("2024-03-07 10:28:55.437286+0000");

    // this method can be called repeatedly and will always return the same time
    var timestamp = clock.now();
}
```

