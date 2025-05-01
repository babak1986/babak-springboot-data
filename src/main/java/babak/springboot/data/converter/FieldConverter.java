package babak.springboot.data.converter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Author: Babak Behzadi
 * Email: behzadi.babak@gmail.com
 **/
public class FieldConverter {

    private FieldConverter() {
    }

    public static Object convert(FieldConverterType type, Object object) {
        if (object == null) {
            return null;
        }
        return switch (type) {
            case DATE -> new SimpleDateFormat("yyyy-MM-dd").format(object);
            case DATE_TIME -> new SimpleDateFormat("yyyy-MM-dd HH:mm").format(object);
            case DATE_TIME_SECONDS -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(object);
            case DATE_TIME_MILLISECONDS -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(object);
            case MILLISECONDS -> ((Date) object).getTime();
            default -> object;
        };
    }

}
