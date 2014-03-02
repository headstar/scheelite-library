package org.headstar.scheelite;

/**
 * Created by per on 02/03/14.
 */
public class Guards {

    public static <T> Guard<T> eventInstanceOf(Class<?> clazz) {
        return new EventInstanceOf<T>(clazz);
    }

    private static class EventInstanceOf<T> implements Guard<T> {

        private final Class<?> clazz;

        EventInstanceOf(Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName();
        }

        @Override
        public boolean apply(GuardArgs<T> input) {
            return input.getEvent().isPresent() && clazz.isInstance(input.getEvent().get());
        }
    }
}
