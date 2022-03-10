package eu.ibagroup.easyrpa.openframework.core.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static java.util.Locale.ENGLISH;

/**
 * Provides set of convenient methods to work with Java Reflection API.
 */
public class TypeUtils {

    /**
     * Gets the value of field {@code fieldName} of given Java object {@code obj}.
     * <p>
     * At first this method is tried to find corresponding to necessary field getter. If such getter is exist calls it
     * and returns its result. Otherwise looks up the necessary field in class of given {@code obj} or it's superclasses.
     *
     * @param obj       the source object with necessary field value.
     * @param fieldName the name of necessary field.
     * @param <T>       expected type of the field.
     * @return the value of specified field.
     */
    public static <T> T getFieldValue(Object obj, String fieldName) {
        return getFieldValue(obj, fieldName, true);
    }

    /**
     * Gets the value of field {@code fieldName} of given Java object {@code obj}.
     * <p>
     * At first this method is tried to find corresponding to necessary field getter if flag {@code useGetMethod} is
     * {@code true}. If such getter is exist calls it and returns its result. Otherwise looks up the necessary field
     * in class of given {@code obj} or it's superclasses.
     *
     * @param obj          the source object with necessary field value.
     * @param fieldName    the name of necessary field.
     * @param useGetMethod whether corresponding getter method should be looked up and used first.
     * @param <T>          expected type of the field.
     * @return the value of specified field.
     */
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

    /**
     * Sets the value {@code value} of field {@code fieldName} for given Java object {@code obj}.
     * <p>
     * At first this method is tried to find corresponding to necessary field setter if flag {@code useSetMethod} is
     * {@code true}. If such setter is exist uses it to set the field value. Otherwise looks up the necessary field
     * in class of given {@code obj} or it's superclasses.
     * <p>
     * Tries to automatically cast given value {@code value} to the type of field {@code fieldName}.
     *
     * @param obj       the target object where the value of necessary field should be set.
     * @param fieldName the name of necessary field.
     * @param value     the value to set.
     */
    public static void setFieldValue(Object obj, String fieldName, Object value) {
        setFieldValue(obj, fieldName, value, true);
    }

    /**
     * Sets the value {@code value} of field {@code fieldName} for given Java object {@code obj}.
     * <p>
     * At first this method is tried to find corresponding to necessary field setter if flag {@code useSetMethod} is
     * {@code true}. If such setter is exist uses it to set the field value. Otherwise looks up the necessary field
     * in class of given {@code obj} or it's superclasses.
     * <p>
     * Tries to automatically cast given value {@code value} to the type of field {@code fieldName}.
     *
     * @param obj          the target object where the value of necessary field should be set.
     * @param fieldName    the name of necessary field.
     * @param value        the value to set.
     * @param useSetMethod whether corresponding setter method should be looked up and used first.
     */
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

    /**
     * Searches method with name {@code methodName} and argument types the same as types of {@code args} and calls it
     * for object {@code obj} with arguments {@code args}.
     * <p>
     * Searches the method in class/interfaces of {@code obj} and it's superclasses/ superinterfaces.
     * <p>
     * <b>IMPORTANT:</b> If it's necessary to pass the value of method argument as {@code null} use the instance of
     * {@link NullValue} class instead to let properly detect the type of argument.
     *
     * @param obj        the target object where the method should be called.
     * @param methodName the name of necessary method.
     * @param args       the sequence of arguments that should be passed to the method. Also they determine signature of
     *                   necessary method.
     * @param <T>        the type of value returned by the method.
     * @return result of method calling.
     * @throws RuntimeException if the method is not found or other errors.
     */
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

    /**
     * Instantiate the class {@code instClass} using constructor with argument types the same as types of {@code args}.
     * <p>
     * <b>IMPORTANT:</b> If it's necessary to pass the value of constructor argument as {@code null} use the instance of
     * {@link NullValue} class instead to let properly detect the type of argument.
     *
     * @param instClass the class to instantiate.
     * @param args      the sequence of arguments that should be passed to constructor. Also they determine signature of
     *                  necessary constructor.
     * @param <T>       the type of class to instantiate.
     * @return the new instance of specified class.
     * @throws RuntimeException if the method is not found or other errors.
     */
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

    /**
     * Searches method with name {@code methodName} and argument types {@code argTypes} for object {@code obj}.
     * <p>
     * Searches the method in class/interfaces of {@code obj} and it's superclasses/superinterfaces.
     *
     * @param objClass   the target class to search.
     * @param methodName the name of method to search.
     * @param argTypes   the sequence of argument types that determine signature of necessary method.
     * @return the {@link Method} object representing found method or {@code null} is such method is not found.
     */
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

    /**
     * Searches constructor of class {@code instClass} with argument types {@code argTypes}.
     *
     * @param instClass the target class.
     * @param argTypes  the sequence of argument types that determine signature of necessary constructor.
     * @param <T>       the type of target class.
     * @return the {@link Constructor} object representing found constructor or {@code null} is such constructor
     * is not found.
     */
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
                return ((Number) obj).byteValue();
            }
            if (Short.class.isAssignableFrom(type) || Short.TYPE == type) {
                return ((Number) obj).shortValue();
            }
            if (Integer.class.isAssignableFrom(type) || Integer.TYPE == type) {
                return ((Number) obj).intValue();
            }
            if (Long.class.isAssignableFrom(type) || Long.TYPE == type) {
                return ((Number) obj).longValue();
            }
            if (Float.class.isAssignableFrom(type) || Float.TYPE == type) {
                return ((Number) obj).floatValue();
            }
            if (Double.class.isAssignableFrom(type) || Double.TYPE == type) {
                return ((Number) obj).doubleValue();
            }
        }
        if (String.class.isAssignableFrom(type) && !(obj instanceof String)) {
            return obj != null ? obj.toString() : null;
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

    /**
     * Helps properly specify the type of method argument in case of passing its value as {@code null}.
     */
    public static class NullValue {
        private Class<?> type;

        public NullValue(Class<?> type) {
            this.type = type;
        }
    }
}
