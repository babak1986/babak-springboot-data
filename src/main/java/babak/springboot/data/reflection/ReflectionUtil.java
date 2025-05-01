package babak.springboot.data.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Author: Babak Behzadi
 * Email: behzadi.babak@gmail.com
 **/
public class ReflectionUtil {

    public static List<Field> getFieldsByAnnotation(Class<?> targetClass, Class<? extends Annotation> annotation) {
        List<Field> fields = new ArrayList<>();
        fields.addAll(Arrays
                .stream(targetClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(annotation))
                .toList());
        fields.addAll(Arrays
                .stream(targetClass.getSuperclass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(annotation) &&
                        !fields.stream().anyMatch(f -> f.getName().equals(field.getName())))
                .toList());
        return fields;
    }

    public static Field getField(Class<?> targetClass, String fieldName) {
        Field field = Arrays.stream(targetClass.getDeclaredFields())
                .filter(f -> f.getName().equals(fieldName)).findFirst().orElse(null);
        if (field == null) {
            field = Arrays.stream(targetClass.getSuperclass().getDeclaredFields())
                    .filter(f -> f.getName().equals(fieldName)).findFirst().orElse(null);
        }
        return field;
    }

    public static Object getFieldValue(Object target, String fieldName) {
        try {
            Field field = getField(target.getClass(), fieldName);
            if (field != null) {
                field.setAccessible(true);
                return field.get(target);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Class<?> getFieldType(Class targetClass, String fieldName) {
        try {
            Field field = getField(targetClass, fieldName);
            if (field != null) {
                return field.getType();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getSupperClassFieldValue(Object target, String fieldName) {
        try {
            Field field;
            try {
                field = target.getClass().getSuperclass().getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                field = target.getClass().getSuperclass() != null ?
                        target.getClass().getSuperclass().getSuperclass().getDeclaredField(fieldName) : null;
            }
            if (field != null) {
                field.setAccessible(true);
                return field.get(target);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean is(Class<?> childClass, Class<?> parentClass) {
        return parentClass.isAssignableFrom(childClass);
    }
}
