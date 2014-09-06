package com.headstartech.scheelite.test;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.headstartech.scheelite.Guard;
import com.headstartech.scheelite.GuardArgs;
import com.headstartech.scheelite.Guards;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Created by per on 04/03/14.
 */
public class GuardsTest extends TestBase {

    @Test
    public void testOf() {
        // given
        Guard<TestEntity> guard = Guards.of(new TestPred(true));

        // when
        boolean res = guard.apply(new GuardArgs<TestEntity>(new TestEntity(), Optional.absent()));

        // then
        assertEquals(true, res);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testAndTrue() {
        // given
        Guard<TestEntity> guard = Guards.and(Guards.of(new TestPred(true)), Guards.of(new TestPred(true)));

        // when
        boolean res = guard.apply(new GuardArgs<TestEntity>(new TestEntity(), Optional.absent()));

        // then
        assertEquals(true, res);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testAndFalse() {
        // given
        Guard<TestEntity> guard = Guards.and(Guards.of(new TestPred(true)), Guards.of(new TestPred(false)));

        // when
        boolean res = guard.apply(new GuardArgs<TestEntity>(new TestEntity(), Optional.absent()));

        // then
        assertEquals(false, res);
    }

    private static class TestPred implements Predicate<GuardArgs<TestEntity>> {

        private final boolean accept;

        TestPred(boolean accept) {
            this.accept = accept;
        }

        @Override
        public boolean apply(GuardArgs<TestEntity> input) {
            assertNotNull(input.getEntity());
            assertNotNull(input.getEvent());
            return accept;
        }
    }


}
