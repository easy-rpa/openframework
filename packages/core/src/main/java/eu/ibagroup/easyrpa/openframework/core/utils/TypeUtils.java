package eu.ibagroup.easyrpa.openframework.core.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static java.util.Locale.ENGLISH;

public class TypeUtils {

    public static <T> T getFieldValue(Object obj, String fieldName) {
        return getFieldValue(obj, fieldName, true);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Object obj, String fieldName, boolean useGetMethod) {
        try {
            Class<?> objClass = obj.getClass();
            if (useGetMethod) {
                try {
                    return (T) objClass.getMethod("get" + capitalize(fieldName)).invoke(obj);
                } catch (Exception e) {
                    //do nothing
                }
            }
            while (true) {
                try {
                    Field field = objClass.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    return (T) field.get(obj);
                } catch (NoSuchFieldException e) {
                    objClass = objClass.getSuperclass();
                    if (objClass == Object.class) {
                        throw e;
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void setFieldValue(Object obj, String fieldName, Object value) {
        setFieldValue(obj, fieldName, value, true);
    }

    public static void setFieldValue(Object obj, String fieldName, Object value, boolean useSetMethod) {
        try {
            Class<?> objClass = obj.getClass();
            if (useSetMethod) {
                try {
                    objClass.getMethod("set" + capitalize(fieldName), value.getClass()).invoke(obj, value);
                    return;
                } catch (Exception e) {
                    //do nothing
                }
            }
            while (true) {
                try {
                    Field field = objClass.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    field.set(obj, castIfPossible(field.getType(), value));
                    break;
                } catch (NoSuchFieldException e) {
                    objClass = objClass.getSuperclass();
                    if (objClass == Object.class) {
                        throw e;
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void callMethod(Object obj, String methodName, Object... args) {
        try {
            Class<?> objClass = obj.getClass();
            Class<?>[] paramTypes = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (arg == null) {
                    throw new IllegalArgumentException("Method argument cannot be null. Use class 'TypeUtils.NullValue' to pass nulls.");
                }
                if (arg instanceof NullValue) {
                    paramTypes[i] = ((NullValue) arg).type;
                } else {
                    paramTypes[i] = arg.getClass();
                }
            }
            while (true) {
                try {
                    Method method = objClass.getDeclaredMethod(methodName, paramTypes);
                    method.setAccessible(true);
                    method.invoke(obj, args);
                    break;
                } catch (NoSuchMethodException e) {
                    objClass = objClass.getSuperclass();
                    if (objClass == Object.class) {
                        throw e;
                    }
                }
            }
        } catch (RuntimeException e) {
            throw e;
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

    public class NullValue {
        private Class<?> type;

        public NullValue(Class<?> type) {
            this.type = type;
        }
    }
}
