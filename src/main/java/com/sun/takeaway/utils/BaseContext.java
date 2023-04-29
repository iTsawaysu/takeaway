package com.sun.takeaway.utils;

/**
 * @author sun
 */
public class BaseContext {
    public static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void set(Long id) {
        threadLocal.set(id);
    }

    public static Long get() {
        return threadLocal.get();
    }

    public static void remove() {
        threadLocal.remove();
    }
}
