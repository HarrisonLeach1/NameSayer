package app.model;

import app.model.processing.SingleNameVersion;
import app.model.processing.Recording;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * A PractiseListModel is an implementation of an IPractiseListModel. It
 * represents a list of Practisable objects that are manipulated by a controller
 * to perform tasks that help the user practise their name pronunciation.
 */
public class PractiseListModel implements IPractiseListModel{

    private ArrayList<Practisable> _practiseList;
    private Recording _currentUserRecording;
    private SingleNameVersion _currentUserCreatedName;
    private int _currentIndex;
    private boolean _keepRecording;
    private Task _currentPlayTask;
    private Practisable _currentPlayingName;

    public PractiseListModel(ArrayList<Practisable> practiseList) {
        _practiseList = practiseList;
        _currentIndex = -1;
    }

    /**
     * Creates a new recording for the currently indexed SingleNameVersion object
     * and keeps it
     */
    public void createUserRecording() {
        _currentUserRecording = new Recording(_practiseList.get(_currentIndex).toString());
        _currentUserCreatedName = _currentUserRecording.startRecording();
    }

    /**
     * The created recording is stopped and saved to a file which is accessible
     * by the user.
     */
    public void finishUserRecording() {
        _currentUserRecording.finishRecording();
    }

    /**
     * Returns a task that when executed plays the audio of the current database
     * recording.
     *
     * It is returned as a Task so that it can be executed on a new
     * thread. The Task should not be cancelled by the user as it will cancel the
     * thread but not the audio process.
     *
     * @param volume 0 means silence, 1.0 means no volume reduction or amplification,
     *               2.0 mans the original audio is amplified by double, etc.
     * @return
     */
    public Task playTask(double volume) {
        _currentPlayTask = new Task() {

            @Override
            protected Object call() {
                _currentPlayingName = _practiseList.get(_currentIndex);
                // play database recording
                try {
                    _currentPlayingName.playRecording(volume);
                } catch (InterruptedException e) {

                    // if the interruption was not from a cancellation call, notify user of error
                    if(!isCancelled()) {
                        e.printStackTrace();
                    }
                }
                return true;
            }
        };

        return _currentPlayTask;

    }

    /**
     * Returns a task that when executed plays the audio of the current user created
     * production, immediately followed by the database recording. It repeats this for
     * the specified number of loops. It is returned as
     * a task so that it can be executed on a new thread.
     * @param volume
     * @param loops
     * @return a Task that plays audio of the user recording then the database recording
     */
    public Task compareUserRecordingTask(double volume, int loops) {
        return new Task() {

            @Override
            protected Object call() throws Exception {
                for(int i = 0; i < loops; i++) {
                    _currentPlayingName = _practiseList.get(_currentIndex);
                    // play user recording
                    _currentUserCreatedName.playRecording(volume);
                    // play database recording
                    _currentPlayingName.playRecording(volume);
                }
                return true;
            }
        };
    }

    /**
     * This stops the most recently returned recording task from playing audio.
     *
     * This method should be used to end the playTask because the implementation
     * cancels both the task and the play process.
     */
    public void stopPlayTask() {
        if(_currentPlayTask != null && !_currentPlayTask.isCancelled()) {
            _currentPlayTask.cancel();
            _currentPlayingName.stopRecording();
        }
    }

    /**
     * Returns the next SingleName object in the list. If there is no
     * next name, the current and returned name remains unchanged.
     */
    public Practisable nextName() {
        finaliseRecording();
        if (_currentIndex != _practiseList.size() - 1) {
            _currentIndex++;
        }

        return _practiseList.get(_currentIndex);
    }


    /**
     * Returns the previous SingleName object in the list. If there is no
     * previous name, the current and returned name remains unchanged.
     */
    public Practisable previousName() {
        finaliseRecording();
        if (_currentIndex != 0) {
            _currentIndex--;
        }

        return _practiseList.get(_currentIndex);
    }

    /**
     * Cancels the recording of the name being currently produced by the
     * user and deletes it.
     */
    public void cancelRecording(){
        _currentUserRecording.cancelRecording();
        _currentUserRecording.deleteRecording();
        _currentUserRecording = null;
    }

    /**
     * Indicates the user wishes to save their current recording of a name.
     */
    public void keepRecording() {
        _keepRecording = true;
    }

    /**
     * Indicates whether or not there exists a next element by checking the
     * current index
     */
    public boolean hasNext() {
        if (_currentIndex >= _practiseList.size() - 1) {
            return false;
        }
        return true;
    }

    /**
     * Indicates whether or not there exists a previous element by checking the
     * current index.
     */
    public boolean hasPrevious() {
        if (_currentIndex <= 0) {
            return false;
        }
        return true;
    }

    /**
     * Finalises the _currentUserRecording. Involves deleting it if it exists and has not
     * been chosen to be saved by the user, otherwise, it is kept.
     */
    private void finaliseRecording() {
        if (!_keepRecording && _currentUserRecording != null) {
            _currentUserRecording.deleteRecording();
        }
        _currentUserRecording = null;
        _keepRecording = false;
    }

    /**
     * Returns whether or not the current user recording created is not equal to null.
     * @return true if there is no Recording object for this current name.
     * Otherwise, false.
     */
    public boolean userHasRecorded() {
        return _currentUserRecording != null;
    }

    /**
     * Returns the list of Practisable names that are being practised in this
     * PractiseListModel object. Finalises the users recording if they have one.
     * @return a list of Practisable objects
     */
    public List<Practisable> getPractiseList() {
        finaliseRecording();
        return _practiseList;
    }
}
