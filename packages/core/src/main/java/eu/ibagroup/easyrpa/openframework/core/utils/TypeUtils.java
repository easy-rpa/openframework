package eu.ibagroup.easyrpa.openframework.core.utils;

import java.lang.reflect.Constructor;
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

    @SuppressWarnings("unchecked")
    public static <T> T callMethod(Object obj, String methodName, Object... args) {
        try {
            Class<?>[] argTypes = getTypesOf(args);
            Method method = findMethod(obj.getClass(), methodName, argTypes);
            if (method == null) {
                throw new NoSuchMethodException();
            }
            return (T) method.invoke(obj, args);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T newInstance(Class<T> instClass, Object... args) {
        try {
            Class<?>[] types = getTypesOf(args);
            Constructor<T> constructor = findConstructor(instClass, types);
            if (constructor == null) {
                throw new NoSuchMethodException();
            }
            return constructor.newInstance(args);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Method findMethod(Class<?> objClass, String methodName, Class<?>... argTypes) {
        try {
            Method method = objClass.getDeclaredMethod(methodName, argTypes);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            if (objClass.getSuperclass() != null && objClass.getSuperclass() != Object.class) {
                Method method = findMethod(objClass.getSuperclass(), methodName, argTypes);
                if (method != null) {
                    return method;
                }
            }
            for (int i = 0; i < argTypes.length; i++) {
                Class<?> origType = argTypes[i];
                if (origType.getSuperclass() != null && origType.getSuperclass() != Object.class) {
                    argTypes[i] = origType.getSuperclass();
                    Method method = findMethod(objClass, methodName, argTypes);
                    if (method != null) {
                        return method;
                    }
                }

                for (Class<?> iFace : origType.getInterfaces()) {
                    argTypes[i] = iFace;
                    Method method = findMethod(objClass, methodName, argTypes);
                    if (method != null) {
                        return method;
                    }
                }
                argTypes[i] = origType;
            }
            return null;
        }
    }

    public static <T> Constructor<T> findConstructor(Class<T> instClass, Class<?>... argTypes) {
        try {
            Constructor<T> constructor = instClass.getDeclaredConstructor(argTypes);
            constructor.setAccessible(true);
            return constructor;
        } catch (NoSuchMethodException e) {
            for (int i = 0; i < argTypes.length; i++) {
                Class<?> origType = argTypes[i];
                if (origType.getSuperclass() != null && origType.getSuperclass() != Object.class) {
                    argTypes[i] = origType.getSuperclass();
                    Constructor<T> constructor = findConstructor(instClass, argTypes);
                    if (constructor != null) {
                        return constructor;
                    }
                }

                for (Class<?> iFace : origType.getInterfaces()) {
                    argTypes[i] = iFace;
                    Constructor<T> constructor = findConstructor(instClass, argTypes);
                    if (constructor != null) {
                        return constructor;
                    }
                }
                argTypes[i] = origType;
            }
            return null;
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
        if (Number.class.isAssignableFrom(type) && obj instanceof Number) {
            if (Byte.class.isAssignableFrom(type) || Byte.TYPE == type) {
                if (obj instanceof Short) return ((Short) obj).byteValue();
                if (obj instanceof Integer) return ((Integer) obj).byteValue();
                if (obj instanceof Long) return ((Long) obj).byteValue();
                if (obj instanceof Float) return ((Float) obj).byteValue();
                if (obj instanceof Double) return ((Double) obj).byteValue();
            }
            if (Short.class.isAssignableFrom(type) || Short.TYPE == type) {
                if (obj instanceof Byte) return ((Byte) obj).shortValue();
                if (obj instanceof Integer) return ((Integer) obj).shortValue();
                if (obj instanceof Long) return ((Long) obj).shortValue();
                if (obj instanceof Float) return ((Float) obj).shortValue();
                if (obj instanceof Double) return ((Double) obj).shortValue();
            }
            if (Integer.class.isAssignableFrom(type) || Integer.TYPE == type) {
                if (obj instanceof Byte) return ((Byte) obj).intValue();
                if (obj instanceof Short) return ((Short) obj).intValue();
                if (obj instanceof Long) return ((Long) obj).intValue();
                if (obj instanceof Float) return ((Float) obj).intValue();
                if (obj instanceof Double) return ((Double) obj).intValue();
            }
            if (Long.class.isAssignableFrom(type) || Long.TYPE == type) {
                if (obj instanceof Byte) return ((Byte) obj).longValue();
                if (obj instanceof Short) return ((Short) obj).longValue();
                if (obj instanceof Integer) return ((Integer) obj).longValue();
                if (obj instanceof Float) return ((Float) obj).longValue();
                if (obj instanceof Double) return ((Double) obj).longValue();
            }
            if (Float.class.isAssignableFrom(type) || Float.TYPE == type) {
                if (obj instanceof Byte) return ((Byte) obj).floatValue();
                if (obj instanceof Short) return ((Short) obj).floatValue();
                if (obj instanceof Integer) return ((Integer) obj).floatValue();
                if (obj instanceof Long) return ((Long) obj).floatValue();
                if (obj instanceof Double) return ((Double) obj).floatValue();
            }
            if (Double.class.isAssignableFrom(type) || Double.TYPE == type) {
                if (obj instanceof Byte) return ((Byte) obj).doubleValue();
                if (obj instanceof Short) return ((Short) obj).doubleValue();
                if (obj instanceof Integer) return ((Integer) obj).doubleValue();
                if (obj instanceof Long) return ((Long) obj).doubleValue();
                if (obj instanceof Float) return ((Float) obj).doubleValue();
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

    private static Class<?>[] getTypesOf(Object... args) {
        Class<?>[] types = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg == null) {
                throw new IllegalArgumentException("Argument cannot be null. Use class 'TypeUtils.NullValue' to pass nulls.");
            }
            if (arg instanceof NullValue) {
                types[i] = ((NullValue) arg).type;
            } else {
                types[i] = arg.getClass();
            }
        }
        return types;
    }

    public static class NullValue {
        private Class<?> type;

        public NullValue(Class<?> type) {
            this.type = type;
        }
    }
}
