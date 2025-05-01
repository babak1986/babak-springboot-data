package babak.springboot.data.mapper;


import babak.springboot.data.converter.FieldConverterType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author: Babak Behzadi
 * Email: behzadi.babak@gmail.com
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MappingField {

    String originalFieldName();

    String mappedFieldName() default "";

    boolean inherited() default false;

    String relation() default "";

    FieldConverterType converterType() default FieldConverterType.NOOP;

}
