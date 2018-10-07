package app.model;

/**
 * A DataModelListener represents a class that needs to know when
 * the experience level of the user changes.
 */
public interface DataModelListener {

    /**
     * Notifies the DataModelListener of the current experience level of
     * the user.
     * @param experience
     */
    void notifyProgress(int experience);
}
