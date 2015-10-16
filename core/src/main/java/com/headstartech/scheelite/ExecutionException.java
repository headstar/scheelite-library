package com.headstartech.scheelite;

/**
 * Exception wrapping any exception thrown from {@link State}, {@link Action} or {@link Guard}.
 */
public class ExecutionException extends Exception {

    public ExecutionException(Throwable cause) {
        super(cause);
    }

    public ExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
