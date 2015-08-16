package com.headstartech.scheelite;

/**
 * Exception mapper which propagates {@link java.lang.RuntimeException}s and wraps a checked exception in a RuntimeException.
 */
public class DefaultExceptionMapper implements ExceptionMapper {

    @Override
    public RuntimeException mapException(Exception e) {
        if(e instanceof RuntimeException) {
            return (RuntimeException) e;
        } else {
            return new RuntimeException(e);
        }
    }
}
