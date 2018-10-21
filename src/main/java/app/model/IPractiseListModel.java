package app.model;

import javafx.concurrent.Task;

import java.util.List;

/**
 * An IPractiseListModel object represents the list of recordings that the
 * user has selected to practise from the database.
 *
 * The user must be able to perform various actions on this IPractiseListModel
 * to practise their name pronunciation.
 */
public interface IPractiseListModel {
    /**
     * Returns the Practisable object that is next in the list of selected
     * recordings to be practised.
     */
    Practisable nextName();

    /**
     * Returns the Practisable object that is previous in the list of selected
     * recordings to be practised.
     */
    Practisable previousName();

    /**
     * Returns a task that when executed, allows the user to listen to the
     * database recording.
     *
     * This method returns a task such that it can be executed on a new
     * thread to avoid concurrency issues. The task cannot be cancelled using
     * the Task cancel method as this task is a blocking call.
     */
    Task playTask(double volume);

    /**
     * Used to stop the currently playing audio from playing. This should
     * be used instead of the playTask's cancel method.
     */
    void stopPlayTask();

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
     * Returns a task that when executed, allows the user to compare
     * their production of a name to the original recording. This method
     * returns a task such that it can be executed on a new thread to
     * avoid concurrency issues.
     */
    Task compareUserRecordingTask(double volume, int loops);

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
     * Returns true if there exists a SingleNameVersion object after the current SingleNameVersion
     * object in the list of selected recordings to be practised, otherwise
     * returns false.
     */
    boolean hasNext();

    /**
     * Returns true if there exists a SingleNameVersion object before the current SingleNameVersion
     * object in the list of selected recordings to be practised, otherwise
     * returns false.
     */
    boolean hasPrevious();

    /**
     * Indicates whether or not the user has created a recording for the current name they
     * are practising.
     * @return
     */
    boolean userHasRecorded();

    /**
     * Returns a list of Practisable objects that are being practised in this
     * IPractiseListModel.
     * @return a list of Practisable objects
     */
    List<Practisable> getPractiseList();
}
