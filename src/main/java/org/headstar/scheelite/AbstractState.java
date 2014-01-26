package org.headstar.scheelite;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractState<T, U> extends StateAdapter<T, U> {

    private final Optional<U> superState;

    protected AbstractState(Optional<U> superState) {
        this.superState = checkNotNull(superState);
    }

    @Override
    public Optional<U> getSuperState() {
        return superState;
    }
}
