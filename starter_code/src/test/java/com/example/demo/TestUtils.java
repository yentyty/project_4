package com.example.demo;

import java.lang.reflect.Field;

public class TestUtils {
    // This class helps to inject objects
    public static void injectObjects(Object target, String fieldName, Object toInject) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, toInject);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
