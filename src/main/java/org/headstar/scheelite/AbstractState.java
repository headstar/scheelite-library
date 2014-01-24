package org.headstar.scheelite;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractState<T, U> implements State<T, U> {

    private ImmutableList<AbstractState<T, U>> subStates;
    private Optional<AbstractState<T, U>> superState;

    void setSubStates(ImmutableList<AbstractState<T, U>> subStates) {
        this.subStates = checkNotNull(subStates);
        for(AbstractState<T, U> state : subStates) {
            state.setSuperState(Optional.of(this));
        }
    }

    void setSuperState(Optional<AbstractState<T, U>> superState) {
        this.superState = checkNotNull(superState);
    }

    @Override
    public Optional<? extends State<T, U>> getSuperState() {
        return superState;
    }

    @Override
    public ImmutableCollection<? extends  State<T, U>> getSubStates() {
        return subStates;
    }
}
