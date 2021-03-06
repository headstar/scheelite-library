package com.headstartech.scheelite.test;

import com.google.common.base.Optional;
import com.headstartech.scheelite.Guard;
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
    public void testAndEqualsTrue() throws Exception {
        // given
        Guard<TestEntity> guard = Guards.and(new TestPred(true), new TestPred(false));

        // when
        boolean res = guard.equals(Guards.and(new TestPred(true), new TestPred(false)));

        // then
        assertTrue(res);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testAndEqualsFalse() throws Exception {
        // given
        Guard<TestEntity> guard = Guards.and(new TestPred(true), new TestPred(false));

        // when
        boolean res = guard.equals(Guards.and(new TestPred(true), new TestPred(true)));

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
    public void testOrEqualsTrue() throws Exception {
        // given
        Guard<TestEntity> guard = Guards.or(new TestPred(true), new TestPred(false));

        // when
        boolean res = guard.equals(Guards.or(new TestPred(true), new TestPred(false)));

        // then
        assertTrue(res);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testOrEqualsFalse() throws Exception {
        // given
        Guard<TestEntity> guard = Guards.or(new TestPred(true), new TestPred(false));

        // when
        boolean res = guard.equals(Guards.or(new TestPred(false), new TestPred(true)));

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

    @SuppressWarnings("unchecked")
    @Test
    public void testNotEqualsTrue() throws Exception {
        // given
        Guard<TestEntity> guard = Guards.not(new TestPred(true));

        // when
        boolean res = guard.equals(Guards.not(new TestPred(true)));

        // then
        assertTrue(res);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testNotEqualsFalse() throws Exception {
        // given
        Guard<TestEntity> guard = Guards.not(new TestPred(true));

        // when
        boolean res = guard.equals(Guards.not(new TestPred(false)));

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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestPred testPred = (TestPred) o;

            return accept == testPred.accept;

        }

        @Override
        public int hashCode() {
            return (accept ? 1 : 0);
        }
    }

}
