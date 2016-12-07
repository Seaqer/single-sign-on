package exceptions;



public class SSOException extends RuntimeException {

    public SSOException(String message) {
        super(message);
    }

    public SSOException(Exception exception) {
        super(exception);
    }

    public SSOException(String message, Exception exception) {
        super(message, exception);
    }
}
