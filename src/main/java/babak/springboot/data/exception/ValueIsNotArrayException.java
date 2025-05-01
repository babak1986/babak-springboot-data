package babak.springboot.data.exception;

/**
 * Author: Babak Behzadi
 * Email: behzadi.babak@gmail.com
 **/
public class ValueIsNotArrayException extends RuntimeException {

    public ValueIsNotArrayException() {
        super("Value is not an array");
    }
}
