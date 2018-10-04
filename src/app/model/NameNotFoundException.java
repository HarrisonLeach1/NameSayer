package app.model;

/**
 * Signals that an attempt to find a Name denoted by a specified string
 * has failed.
 */
public class NameNotFoundException extends Exception {

    private final String _missingNames;

    public NameNotFoundException(String missingNames) {
        super("The following Name(s) cannot be found: " + missingNames);
        _missingNames = missingNames;
    }

    public String getMessage() {
        return super.getMessage();
    }

    public String getMissingNames() {
        return _missingNames;
    }

}
