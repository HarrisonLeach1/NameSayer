package app.model;

/**
 * A UserModelListener represents a class that needs to know when
 * the experience level of the user changes.
 */
public interface UserModelListener {

    /**
     * Notifies the UserModelListener of the current level of the user
     * and the progress they have towards the next level.
     * @param currentUserLevel
     * @param currentLevelProgress
     */
    void notifyProgress(int currentUserLevel, double currentLevelProgress);
}
