package org.headstar.scheelite;

/**
 * Created by Per on 2014-01-25.
 */
public interface InitialAction<T> {

    String getName();

    void execute(T entity);
}
