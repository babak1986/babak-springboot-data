package babak.springboot.data.mapper;

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
public @interface MappingFields {

    MappingField[] fields() default {};
}
