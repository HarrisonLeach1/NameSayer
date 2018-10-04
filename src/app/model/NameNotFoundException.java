package app.model;

/**
 * Signals that an attempt to find a Name denoted by a specified string
 * has failed.
 */
public class NameNotFoundException extends Exception {

    public NameNotFoundException(String missingNames) {
        super("The Name(s) " + missingNames + "cannot be found");
    }

    public String getMessage() {
        return super.getMessage();
    }

}
