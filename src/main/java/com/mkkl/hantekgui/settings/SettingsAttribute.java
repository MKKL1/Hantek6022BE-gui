package com.mkkl.hantekgui.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SettingsAttribute<T> {
    private T value;
    private final List<ValueChangeListener<T>> listeners = new ArrayList<>();

    public SettingsAttribute() {
        this.value = null;
    }

    public SettingsAttribute(T value) {
        this.value = value;
    }

    public void addValueChangeListener(ValueChangeListener<T> listener) {
        listeners.add(listener);
    }

    public void addAndActiveListener(ValueChangeListener<T> listener) {
        listeners.add(listener);
        listener.valueChanged(null, value);
    }

    public void removeValueChangeListener(ValueChangeListener<T> listener) {
        listeners.remove(listener);
    }

    public T getValue() {
        return value;
    }

    public void setValue(T newValue) {
        T oldV = this.value;
        this.value = newValue;
        listeners.forEach(x -> x.valueChanged(oldV, newValue));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SettingsAttribute<?> that = (SettingsAttribute<?>) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
