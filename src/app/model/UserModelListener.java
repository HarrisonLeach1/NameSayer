package app.model;

/**
 * A UserModelListener represents a class that needs to know when
 * the experience level of the user changes.
 */
public interface UserModelListener {

    /**
     * Notifies the UserModelListener of the current experience level of
     * the user.
     * @param experience
     */
    void notifyProgress(int experience);
}
