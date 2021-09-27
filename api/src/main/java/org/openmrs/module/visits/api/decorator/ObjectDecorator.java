package org.openmrs.module.visits.api.decorator;

public abstract class ObjectDecorator<T> {
    private T object;

    public ObjectDecorator(T object) {
        if (object == null) {
            throw new IllegalArgumentException("Passed object cannot be null");
        }
        this.object = object;
    }

    public T getObject() {
        return object;
    }
}
