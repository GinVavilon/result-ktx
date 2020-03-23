package com.github.ginvavilon.utils;

public interface ResultFunction<V, E extends Throwable> {

    V invoke() throws E;

}