package io.github.rodrigovsilva.tempus;

/**
 * This TimeException class is a generic exception for all time-related
 */
abstract class TimeException extends RuntimeException {
    public TimeException(String message, Exception cause) {
        super(message, cause);
    }
}
