package com.headstartech.scheelite.test;

import com.headstartech.scheelite.exceptionmapper.DefaultExceptionMapper;
import com.headstartech.scheelite.exceptionmapper.ExceptionMapper;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertSame;

/**
 * Created by per on 8/16/15.
 */
public class DefaultExceptionMapperTest {

    @Test
    public void testRuntimeException() {
        // given
        ExceptionMapper mapper = new DefaultExceptionMapper();
        RuntimeException e = new RuntimeException();

        // when
        RuntimeException mapped = mapper.mapException(e);

        // then
        assertSame(e, mapped);
    }

    @Test
    public void testCheckedException() {
        // given
        ExceptionMapper mapper = new DefaultExceptionMapper();
        Exception e = new IOException();

        // when
        RuntimeException mapped = mapper.mapException(e);

        // then
        assertSame(mapped.getCause(), e);
    }

}
