package exceptions;



public class SSOException extends Exception {

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
