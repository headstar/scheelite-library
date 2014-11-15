package com.headstartech.scheelite;


import com.google.common.base.Optional;
import com.headstartech.scheelite.test.TestBase;
import org.testng.annotations.Test;

import java.util.List;

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
    public void getParentWhenRootParent() {
        // given
        MutableStateTree<TestEntity, StateId> tree = new MutableStateTree<TestEntity, StateId>();
        TestState stateA = new TestState(StateId.A);
        tree.addState(stateA);

        // when
        Optional<State<TestEntity, StateId>> res = tree.getParent(stateA);

        // then
        assertTrue(res.isPresent());
        assertEquals(res.get(), tree.getRootState());
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
    public void isAncestorOfWhenFalse1() {
        // given
        MutableStateTree<TestEntity, StateId> tree = new MutableStateTree<TestEntity, StateId>();
        TestState stateA = new TestState(StateId.A);
        TestState stateB = new TestState(StateId.B);
        TestState stateC = new TestState(StateId.C);
        tree.addState(stateA, stateB);
        tree.addState(stateC);

        // when
        boolean res = tree.isAncestorOf(stateB, stateC);

        // then
        assertFalse(res);
    }

    @Test
    public void isAncestorOfWhenFalse2() {
        // given
        MutableStateTree<TestEntity, StateId> tree = new MutableStateTree<TestEntity, StateId>();
        TestState stateA = new TestState(StateId.A);
        TestState stateB = new TestState(StateId.B);
        TestState stateC = new TestState(StateId.C);
        tree.addState(stateA, stateB);
        tree.addState(stateC);

        // when
        boolean res = tree.isAncestorOf(stateA, stateB);

        // then
        assertFalse(res);
    }

    @Test
    public void isAncestorOfWhenTrue1() {
        // given
        MutableStateTree<TestEntity, StateId> tree = new MutableStateTree<TestEntity, StateId>();
        TestState stateA = new TestState(StateId.A);
        TestState stateB = new TestState(StateId.B);
        TestState stateC = new TestState(StateId.C);
        tree.addState(stateA, stateB);
        tree.addState(stateC, stateA);

        // when
        boolean res = tree.isAncestorOf(stateB, stateC);

        // then
        assertTrue(res);
    }

    @Test
    public void isAncestorOfWhenTrue2() {
        // given
        MutableStateTree<TestEntity, StateId> tree = new MutableStateTree<TestEntity, StateId>();
        TestState stateA = new TestState(StateId.A);
        tree.addState(stateA);

        // when
        boolean res = tree.isAncestorOf(stateA, stateA);

        // then
        assertTrue(res);
    }


    @Test
    public void isDescendantOfWhenFalse1() {
        // given
        MutableStateTree<TestEntity, StateId> tree = new MutableStateTree<TestEntity, StateId>();
        TestState stateA = new TestState(StateId.A);
        TestState stateB = new TestState(StateId.B);
        TestState stateC = new TestState(StateId.C);
        tree.addState(stateA, stateB);
        tree.addState(stateC);

        // when
        boolean res = tree.isDescendantOf(stateA, stateC);

        // then
        assertFalse(res);
    }

    @Test
    public void isDescendantOfWhenFalse2() {
        // given
        MutableStateTree<TestEntity, StateId> tree = new MutableStateTree<TestEntity, StateId>();
        TestState stateA = new TestState(StateId.A);
        TestState stateB = new TestState(StateId.B);
        TestState stateC = new TestState(StateId.C);
        tree.addState(stateA, stateB);
        tree.addState(stateC);

        // when
        boolean res = tree.isDescendantOf(stateB, stateA);

        // then
        assertFalse(res);
    }

    @Test
    public void isDescendantOfWhenTrue1() {
        // given
        MutableStateTree<TestEntity, StateId> tree = new MutableStateTree<TestEntity, StateId>();
        TestState stateA = new TestState(StateId.A);
        TestState stateB = new TestState(StateId.B);
        TestState stateC = new TestState(StateId.C);
        tree.addState(stateA, stateB);
        tree.addState(stateC, stateA);

        // when
        boolean res = tree.isDescendantOf(stateC, stateB);

        // then
        assertTrue(res);
    }

    @Test
    public void isDescendantOfWhenTrue2() {
        // given
        MutableStateTree<TestEntity, StateId> tree = new MutableStateTree<TestEntity, StateId>();
        TestState stateA = new TestState(StateId.A);
        tree.addState(stateA);

        // when
        boolean res = tree.isDescendantOf(stateA, stateA);

        // then
        assertTrue(res);
    }

    @Test
    public void getLowestCommonAncestorWhenRoot() {
        // given
        MutableStateTree<TestEntity, StateId> tree = new MutableStateTree<TestEntity, StateId>();
        TestState stateA = new TestState(StateId.A);
        TestState stateB = new TestState(StateId.B);
        tree.addState(stateA);
        tree.addState(stateB);

        // when
        State<TestEntity, StateId> res = tree.getLowestCommonAncestor(stateA, stateB);

        // then
        assertEquals(res, tree.getRootState());
    }

    @Test
    public void getLowestCommonAncestorWhenSelf() {
        // given
        MutableStateTree<TestEntity, StateId> tree = new MutableStateTree<TestEntity, StateId>();
        TestState stateA = new TestState(StateId.A);
        tree.addState(stateA);

        // when
        State<TestEntity, StateId> res = tree.getLowestCommonAncestor(stateA, stateA);

        // then
        assertEquals(res, stateA);
    }

    @Test
    public void getLowestCommonAncestorWhenParentChild() {
        // given
        MutableStateTree<TestEntity, StateId> tree = new MutableStateTree<TestEntity, StateId>();
        TestState stateA = new TestState(StateId.A);
        TestState stateB = new TestState(StateId.B);
        tree.addState(stateA, stateB);

        // when
        State<TestEntity, StateId> res = tree.getLowestCommonAncestor(stateB, stateA);

        // then
        assertEquals(res, stateB);
    }

    @Test
    public void getLowestCommonAncestorWhenParentGrandChild() {
        // given
        MutableStateTree<TestEntity, StateId> tree = new MutableStateTree<TestEntity, StateId>();
        TestState stateA = new TestState(StateId.A);
        TestState stateB = new TestState(StateId.B);
        TestState stateC = new TestState(StateId.C);
        tree.addState(stateA, stateB);
        tree.addState(stateC, stateA);

        // when
        State<TestEntity, StateId> res = tree.getLowestCommonAncestor(stateC, stateB);

        // then
        assertEquals(res, stateB);
    }

    @Test(expectedExceptions = {IllegalArgumentException.class})
    public void getPathBetweenWhenNotRelated() {
        // given
        MutableStateTree<TestEntity, StateId> tree = new MutableStateTree<TestEntity, StateId>();
        State<TestEntity, StateId> stateA = new TestState(StateId.A);
        State<TestEntity, StateId> stateB = new TestState(StateId.B);
        tree.addState(stateA);
        tree.addState(stateB);

        // when
        tree.getPathToAncestor(stateA, stateB, true);

        // then
    }

    @Test
    public void getPathBetweenSelfIncludeAncestor() {
        // given
        MutableStateTree<TestEntity, StateId> tree = new MutableStateTree<TestEntity, StateId>();
        State<TestEntity, StateId> stateA = new TestState(StateId.A);
        tree.addState(stateA);

        // when
        List<State<TestEntity, StateId>> res = tree.getPathToAncestor(stateA, stateA, true);

        // then
        assertTrue(res.isEmpty());
    }

    @Test
    public void getPathBetweenSelfNotIncludeAncestor() {
        // given
        MutableStateTree<TestEntity, StateId> tree = new MutableStateTree<TestEntity, StateId>();
        State<TestEntity, StateId> stateA = new TestState(StateId.A);
        tree.addState(stateA);

        // when
        List<State<TestEntity, StateId>> res = tree.getPathToAncestor(stateA, stateA, false);

        // then
        assertTrue(res.isEmpty());
    }


    @Test
    public void getPathToAncestorInclude() {
        // given
        MutableStateTree<TestEntity, StateId> tree = new MutableStateTree<TestEntity, StateId>();
        State<TestEntity, StateId> stateA = new TestState(StateId.A);
        State<TestEntity, StateId> stateB = new TestState(StateId.B);
        State<TestEntity, StateId> stateC = new TestState(StateId.C);
        tree.addState(stateA, stateB);
        tree.addState(stateC, stateA);

        // when
        List<State<TestEntity, StateId>> res = tree.getPathToAncestor(stateC, stateB, true);

        // then
        assertEquals(3, res.size());
        assertEquals(res.get(0), stateC);
        assertEquals(res.get(1), stateA);
        assertEquals(res.get(2), stateB);
    }

    @Test
    public void getPathToAncestorExclude() {
        // given
        MutableStateTree<TestEntity, StateId> tree = new MutableStateTree<TestEntity, StateId>();
        State<TestEntity, StateId> stateA = new TestState(StateId.A);
        State<TestEntity, StateId> stateB = new TestState(StateId.B);
        State<TestEntity, StateId> stateC = new TestState(StateId.C);
        tree.addState(stateA, stateB);
        tree.addState(stateC, stateA);

        // when
        List<State<TestEntity, StateId>> res = tree.getPathToAncestor(stateC, stateB, false);

        // then
        assertEquals(2, res.size());
        assertEquals(res.get(0), stateC);
        assertEquals(res.get(1), stateA);
    }

    @Test
    public void getPathToRootState() {
        // given
        MutableStateTree<TestEntity, StateId> tree = new MutableStateTree<TestEntity, StateId>();
        State<TestEntity, StateId> stateA = new TestState(StateId.A);
        State<TestEntity, StateId> stateB = new TestState(StateId.B);
        State<TestEntity, StateId> stateC = new TestState(StateId.C);
        tree.addState(stateA, stateB);
        tree.addState(stateC, stateA);

        // when
        List<State<TestEntity, StateId>> res = tree.getPathToAncestor(stateC, tree.getRootState(), false);

        // then
        assertEquals(3, res.size());
        assertEquals(res.get(0), stateC);
        assertEquals(res.get(1), stateA);
        assertEquals(res.get(2), stateB);
    }

}
