package com.headstartech.scheelite;


import com.google.common.base.Optional;
import com.headstartech.scheelite.test.TestBase;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class AbstractStateTreeTest extends TestBase {

    @Test
    public void getStateWhenStateExists() {
        // given
        MutableStateTree<TestEntity, StateId> tree = new MutableStateTree<TestEntity, StateId>();
        TestState state = new TestState(StateId.A);
        tree.addState(state);

        // when
        Optional<State<TestEntity, StateId>> res = tree.getState(StateId.A);

        // then
        assertTrue(res.isPresent());
        assertEquals(res.get(), state);
    }

    @Test
    public void getStateWhenNotExists() {
        // given
        MutableStateTree<TestEntity, StateId> tree = new MutableStateTree<TestEntity, StateId>();
        TestState state = new TestState(StateId.A);
        tree.addState(state);

        // when
        Optional<State<TestEntity, StateId>> res = tree.getState(StateId.B);

        // then
        assertFalse(res.isPresent());
    }

    @Test
    public void existsWhenTrue() {
        // given
        MutableStateTree<TestEntity, StateId> tree = new MutableStateTree<TestEntity, StateId>();
        TestState state = new TestState(StateId.A);
        tree.addState(state);

        // when
        boolean res = tree.exists(state);

        // then
        assertTrue(res);
    }

    @Test
    public void existsWhenFalse() {
        // given
        MutableStateTree<TestEntity, StateId> tree = new MutableStateTree<TestEntity, StateId>();
        TestState state = new TestState(StateId.A);
        tree.addState(state);

        // when
        boolean res = tree.exists(new TestState(StateId.B));

        // then
        assertFalse(res);
    }

    @Test
    public void isChildWhenTrue() {
        // given
        MutableStateTree<TestEntity, StateId> tree = new MutableStateTree<TestEntity, StateId>();
        TestState stateA = new TestState(StateId.A);
        TestState stateB = new TestState(StateId.B);
        tree.addState(stateA, stateB);

        // when
        boolean resStateA = tree.isChild(stateA);

        // then
        assertTrue(resStateA);
    }

    @Test
    public void isChildWhenFalse() {
        // given
        MutableStateTree<TestEntity, StateId> tree = new MutableStateTree<TestEntity, StateId>();
        TestState stateA = new TestState(StateId.A);
        tree.addState(stateA);

        // when
        boolean resStateA = tree.isChild(stateA);

        // then
        assertFalse(resStateA);
    }

    @Test
    public void getParentWhenNoParent() {
        // given
        MutableStateTree<TestEntity, StateId> tree = new MutableStateTree<TestEntity, StateId>();
        TestState stateA = new TestState(StateId.A);
        tree.addState(stateA);

        // when
        Optional<State<TestEntity, StateId>> res = tree.getParent(stateA);

        // then
        assertFalse(res.isPresent());
    }

    @Test
    public void getParentWhenHavingParent() {
        // given
        MutableStateTree<TestEntity, StateId> tree = new MutableStateTree<TestEntity, StateId>();
        TestState stateA = new TestState(StateId.A);
        TestState stateB = new TestState(StateId.B);
        tree.addState(stateA, stateB);

        // when
        Optional<State<TestEntity, StateId>> res = tree.getParent(stateA);

        // then
        assertTrue(res.isPresent());
        assertEquals(res.get(), stateB);
    }

    @Test
    public void isChildOfWhenTrue() {
        // given
        MutableStateTree<TestEntity, StateId> tree = new MutableStateTree<TestEntity, StateId>();
        TestState stateA = new TestState(StateId.A);
        TestState stateB = new TestState(StateId.B);
        tree.addState(stateA, stateB);

        // when
        boolean res = tree.isChildOf(stateA, stateB);

        // then
        assertTrue(res);
    }

    @Test
    public void isChildOfWhenFalse() {
        // given
        MutableStateTree<TestEntity, StateId> tree = new MutableStateTree<TestEntity, StateId>();
        TestState stateA = new TestState(StateId.A);
        TestState stateB = new TestState(StateId.B);
        TestState stateC = new TestState(StateId.C);

        tree.addState(stateA, stateB);
        tree.addState(stateC);

        // when
        boolean res1 = tree.isChildOf(stateB, stateA);
        boolean res2 = tree.isChildOf(stateC, stateB);

        // then
        assertFalse(res1);
        assertFalse(res2);
    }

    @Test
    public void isParentOfWhenTrue() {
        // given
        MutableStateTree<TestEntity, StateId> tree = new MutableStateTree<TestEntity, StateId>();
        TestState stateA = new TestState(StateId.A);
        TestState stateB = new TestState(StateId.B);
        tree.addState(stateA, stateB);

        // when
        boolean res = tree.isParentOf(stateB, stateA);

        // then
        assertTrue(res);
    }

    @Test
    public void isParentOfWhenFalse() {
        // given
        MutableStateTree<TestEntity, StateId> tree = new MutableStateTree<TestEntity, StateId>();
        TestState stateA = new TestState(StateId.A);
        TestState stateB = new TestState(StateId.B);
        TestState stateC = new TestState(StateId.C);

        tree.addState(stateA, stateB);
        tree.addState(stateC);

        // when
        boolean res1 = tree.isParentOf(stateA, stateB);
        boolean res2 = tree.isParentOf(stateC, stateB);

        // then
        assertFalse(res1);
        assertFalse(res2);
    }


    @Test
    public void isParentWhenTrue() {
        // given
        MutableStateTree<TestEntity, StateId> tree = new MutableStateTree<TestEntity, StateId>();
        TestState stateA = new TestState(StateId.A);
        TestState stateB = new TestState(StateId.B);
        tree.addState(stateA, stateB);

        // when
        boolean resStateA = tree.isParent(stateB);

        // then
        assertTrue(resStateA);
    }

    @Test
    public void isParentWhenFalse() {
        // given
        MutableStateTree<TestEntity, StateId> tree = new MutableStateTree<TestEntity, StateId>();
        TestState stateA = new TestState(StateId.A);
        tree.addState(stateA);

        // when
        boolean resStateA = tree.isParent(stateA);

        // then
        assertFalse(resStateA);
    }

}
