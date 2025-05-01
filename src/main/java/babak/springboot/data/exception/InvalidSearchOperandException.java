package babak.springboot.data.exception;

/**
 * Author: Babak Behzadi
 * Email: behzadi.babak@gmail.com
 **/
public class InvalidSearchOperandException extends RuntimeException {

    public InvalidSearchOperandException() {
        super("Invalid search operand");
    }
}
