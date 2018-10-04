package app.model;

/**
 * Signals that an attempt to find a Name denoted by a specified string
 * has failed.
 */
public class NameNotFoundException extends Exception {

    public NameNotFoundException(String message) {
        super(message);
    }

    public String getMessage() {
        return super.getMessage();
    }

}
