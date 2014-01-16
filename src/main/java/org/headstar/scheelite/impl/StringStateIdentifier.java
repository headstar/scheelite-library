package org.headstar.scheelite.impl;

import com.google.common.base.Preconditions;
import org.headstar.scheelite.StateIdentifier;

public class StringStateIdentifier implements StateIdentifier {

    private final String id;

    public StringStateIdentifier(String id) {
        Preconditions.checkNotNull(id);
        Preconditions.checkArgument(!id.isEmpty());
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StringStateIdentifier that = (StringStateIdentifier) o;

        if (!id.equals(that.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("StringStateIdentifier [");
        sb.append("id='").append(id).append('\'');
        sb.append(']');
        return sb.toString();
    }
}
