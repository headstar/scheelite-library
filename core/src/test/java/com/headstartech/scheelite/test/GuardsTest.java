package com.headstartech.scheelite.test;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.headstartech.scheelite.Guard;
import com.headstartech.scheelite.GuardArgs;
import com.headstartech.scheelite.Guards;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by per on 04/03/14.
 */
public class GuardsTest extends TestBase {

    @Test
    public void testSimple() throws Exception {
        // given
        Guard<TestEntity> guard = new TestPred(true);

        // when
        boolean res = guard.evaluate(new TestEntity(), Optional.absent());

        // then
        assertTrue(res);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testAndTrue() throws Exception {
        // given
        Guard<TestEntity> guard = Guards.and(new TestPred(true), new TestPred(true));

        // when
        boolean res = guard.evaluate(new TestEntity(), Optional.absent());

        // then
        assertTrue(res);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testAndFalse() throws Exception {
        // given
        Guard<TestEntity> guard = Guards.and(new TestPred(true), new TestPred(false));

        // when
        boolean res = guard.evaluate(new TestEntity(), Optional.absent());

        // then
        assertFalse(res);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testOrTrue() throws Exception {
        // given
        Guard<TestEntity> guard = Guards.or(new TestPred(true), new TestPred(false));

        // when
        boolean res = guard.evaluate(new TestEntity(), Optional.absent());

        // then
        assertTrue(res);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testOrFalse() throws Exception {
        // given
        Guard<TestEntity> guard = Guards.or(new TestPred(false), new TestPred(false));

        // when
        boolean res = guard.evaluate(new TestEntity(), Optional.absent());

        // then
        assertFalse(res);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testNotTrue() throws Exception {
        // given
        Guard<TestEntity> guard = Guards.not(new TestPred(false));

        // when
        boolean res = guard.evaluate(new TestEntity(), Optional.absent());

        // then
        assertTrue(res);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testNotFalse() throws Exception {
        // given
        Guard<TestEntity> guard = Guards.not(new TestPred(true));

        // when
        boolean res = guard.evaluate(new TestEntity(), Optional.absent());

        // then
        assertFalse(res);
    }

    private static class TestPred implements Guard<TestEntity> {

        private final boolean accept;

        TestPred(boolean accept) {
            this.accept = accept;
        }

        @Override
        public boolean evaluate(TestEntity context, Optional<?> event) {
            assertNotNull(context);
            assertNotNull(event);
            return accept;
        }

    }

}
