package babak.springboot.data.mapper;

import babak.springboot.data.converter.FieldConverter;
import babak.springboot.data.exception.MappingObjectException;
import babak.springboot.data.reflection.ReflectionUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: Babak Behzadi
 * Email: behzadi.babak@gmail.com
 **/
public class MappingObjectConverter {

    private MappingObjectConverter() {
    }

    public static Map<String, Object> convert(Object originalObject) {
        MappingFields mappingFields = originalObject.getClass().getAnnotation(MappingFields.class);
        if (mappingFields != null) {
            Map<String, Object> converted = new HashMap<>();
            Arrays.stream(mappingFields.fields()).forEach(mappingField -> {
                Object value = null;
                if (!StringUtils.isEmpty(mappingField.relation())) {
                    Object relationObject = ReflectionUtil.getFieldValue(originalObject, mappingField.relation());
                    if (relationObject != null) {
                        value = ReflectionUtil.getFieldValue(relationObject, mappingField.originalFieldName());
                    }
                } else if (mappingField.inherited()) {
                    value = ReflectionUtil.getSupperClassFieldValue(originalObject, mappingField.originalFieldName());
                } else {
                    value = ReflectionUtil.getFieldValue(originalObject, mappingField.originalFieldName());
                }
                try {
                    converted.put(mappingField.mappedFieldName().equals("") ?
                                    mappingField.originalFieldName() :
                                    mappingField.mappedFieldName(),
                            FieldConverter.convert(mappingField.converterType(), value));
                } catch (Exception e) {
                    throw new MappingObjectException(e.getMessage());
                }
            });
            return converted;
        }
        return new ObjectMapper().convertValue(originalObject, Map.class);
    }
}
