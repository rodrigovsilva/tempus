package io.github.rodrigovsilva.tempus;

/**
 * This clock replaces all uses of the `java.time` for transaction
 * processing. It is used to make the system more testable and to
 * ensure that timestamps are always in UTC and are serialized to
 * the correct format.
 */
public interface Clock {
    PointInTime now();
}
