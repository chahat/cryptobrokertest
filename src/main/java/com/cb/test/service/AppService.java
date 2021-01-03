package com.cb.test.service;

import java.util.Set;

public interface AppService<T> {

    public T getId(Long id);

    public void set(T item);
}
