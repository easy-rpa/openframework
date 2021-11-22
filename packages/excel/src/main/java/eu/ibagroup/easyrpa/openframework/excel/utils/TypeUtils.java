package eu.ibagroup.easyrpa.openframework.excel.utils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static java.util.Locale.ENGLISH;

public class TypeUtils {

    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Object obj, String fieldName) {
        try {
            Class<?> objClass = obj.getClass();
            try {
                return (T) objClass.getMethod("get" + capitalize(fieldName)).invoke(obj);
            } catch (Exception e) {
                //do nothing
            }
            Field field = objClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void setFieldValue(Object obj, String fieldName, Object value) {
        try {
            Class<?> objClass = obj.getClass();
            try {
                objClass.getMethod("set" + capitalize(fieldName), value.getClass()).invoke(obj, value);
                return;
            } catch (Exception e) {
                //do nothing
            }
            Field field = objClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, castIfPossible(field.getType(), value));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Object castIfPossible(Class<?> type, Object obj) {
        if (!String.class.isAssignableFrom(type) && obj instanceof String) {
            if (((String) obj).trim().isEmpty()) {
                return null;
            }
            if (Byte.class.isAssignableFrom(type) || Byte.TYPE == type) {
                return Byte.valueOf((String) obj);
            }
            if (Short.class.isAssignableFrom(type) || Short.TYPE == type) {
                return Short.valueOf((String) obj);
            }
            if (Integer.class.isAssignableFrom(type) || Integer.TYPE == type) {
                return Integer.valueOf((String) obj);
            }
            if (Long.class.isAssignableFrom(type) || Long.TYPE == type) {
                return Long.valueOf((String) obj);
            }
            if (Float.class.isAssignableFrom(type) || Float.TYPE == type) {
                return Float.valueOf((String) obj);
            }
            if (Double.class.isAssignableFrom(type) || Double.TYPE == type) {
                return Double.valueOf((String) obj);
            }
            if (Boolean.class.isAssignableFrom(type) || Boolean.TYPE == type) {
                return Boolean.valueOf((String) obj);
            }
        }
        return obj;
    }

    private static String capitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        return name.substring(0, 1).toUpperCase(ENGLISH) + name.substring(1);
    }
}
