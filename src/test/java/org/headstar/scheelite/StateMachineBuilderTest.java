package org.headstar.scheelite;

import org.testng.annotations.Test;

public class StateMachineBuilderTest {

    @Test(expectedExceptions = IllegalStateException.class, expectedExceptionsMessageRegExp = "no start state.*")
    public void test() {

        StateMachineBuilder builder = new StateMachineBuilder();
        builder.build();
    }


}
