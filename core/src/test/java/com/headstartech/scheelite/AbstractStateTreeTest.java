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
        Optional<State<TestEntity, StateId>> res = tree.getLowestCommonAncestor(stateA, stateB);

        // then
        assertFalse(res.isPresent());
    }

    @Test
    public void getLowestCommonAncestorWhenSelf() {
        // given
        MutableStateTree<TestEntity, StateId> tree = new MutableStateTree<TestEntity, StateId>();
        TestState stateA = new TestState(StateId.A);
        tree.addState(stateA);

        // when
        Optional<State<TestEntity, StateId>> res = tree.getLowestCommonAncestor(stateA, stateA);

        // then
        assertTrue(res.isPresent());
        assertEquals(stateA, res.get());
    }

    @Test
    public void getLowestCommonAncestorWhenParentChild() {
        // given
        MutableStateTree<TestEntity, StateId> tree = new MutableStateTree<TestEntity, StateId>();
        TestState stateA = new TestState(StateId.A);
        TestState stateB = new TestState(StateId.B);
        tree.addState(stateA, stateB);

        // when
        Optional<State<TestEntity, StateId>> res = tree.getLowestCommonAncestor(stateB, stateA);

        // then
        assertTrue(res.isPresent());
        assertEquals(stateB, res.get());
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
        Optional<State<TestEntity, StateId>> res = tree.getLowestCommonAncestor(stateC, stateB);

        // then
        assertTrue(res.isPresent());
        assertEquals(stateB, res.get());
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
        tree.getPathBetween(stateA, Optional.of(stateB));

        // then
    }

    @Test
    public void getPathBetweenSelf() {
        // given
        MutableStateTree<TestEntity, StateId> tree = new MutableStateTree<TestEntity, StateId>();
        State<TestEntity, StateId> stateA = new TestState(StateId.A);
        tree.addState(stateA);

        // when
        List<State<TestEntity, StateId>> res = tree.getPathBetween(stateA, Optional.of(stateA));

        // then
        assertTrue(res.isEmpty());
    }

    @Test
    public void getPathBetween() {
        // given
        MutableStateTree<TestEntity, StateId> tree = new MutableStateTree<TestEntity, StateId>();
        State<TestEntity, StateId> stateA = new TestState(StateId.A);
        State<TestEntity, StateId> stateB = new TestState(StateId.B);
        State<TestEntity, StateId> stateC = new TestState(StateId.C);
        tree.addState(stateA, stateB);
        tree.addState(stateC, stateA);

        // when
        List<State<TestEntity, StateId>> res = tree.getPathBetween(stateC, Optional.of(stateB));

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
        List<State<TestEntity, StateId>> res = tree.getPathToRootState(stateC);

        // then
        assertEquals(3, res.size());
        assertEquals(res.get(0), stateC);
        assertEquals(res.get(1), stateA);
        assertEquals(res.get(2), stateB);
    }

}
