package app.model;

/**
 * An IPractiseListModel object represents the list of recordings that the
 * user has selected to practise from the database.
 *
 * The user must be able to perform various actions on this IPractiseListModel
 * to practise their name Pronunciation.
 */
public interface IPractiseListModel {

    /**
     * Allows the user to create their own productions of a name.
     */
    void createUserRecording();

    /**
     * Allows the user to specify when they have finished recording
     * their own production of a name.
     */
    void finishUserRecording();

    /**
     * Allows the user to compare their production of a name to the
     * original recording.
     */
    void compareUserRecording(double volume);

    /**
     * Allows the user to cancel while they are recording their own
     * production of a name.
     */
    void cancelRecording();

    /**
     * Allows the user to keep their own productions of a name such
     * that they are able to access these saved past attempts of a name.
     */
    void keepRecording();

    /**
     * Returns the NameVersion object that is next in the list of selected
     * recordings to be practised.
     */
    Practisable nextName();

    /**
     * Returns the NameVersion object that is previous in the list of selected
     * recordings to be practised.
     */
    Practisable previousName();

    /**
     * Returns true if there exists a NameVersion object after the current NameVersion
     * object in the list of selected recordings to be practised, otherwise
     * returns false.
     */
    boolean hasNext();

    /**
     * Returns true if there exists a NameVersion object before the current NameVersion
     * object in the list of selected recordings to be practised, otherwise
     * returns false.
     */
    boolean hasPrevious();
}
