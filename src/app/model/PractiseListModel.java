package app.model;

import javafx.concurrent.Task;

import java.util.ArrayList;

public class PractiseListModel implements IPractiseListModel{

    private ArrayList<Practisable> _practiseList;
    private Recording _currentUserRecording;
    private NameVersion _currentUserCreatedName;
    private int _currentIndex;
    private boolean _keepRecording;
    private Task compareWorker;

    public PractiseListModel(ArrayList<Practisable> practiseList) {
        _practiseList = practiseList;
        _currentIndex = -1;
    }

    /**
     * Creates a new recording for the currently indexed NameVersion object
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
     * Compares the users production of a name to the database name.
     */
    public void compareUserRecording(double volume) {
        if (_currentUserCreatedName == null) { return; }
        _currentUserCreatedName.playRecording(volume);
        // play database recording
        _practiseList.get(_currentIndex).playRecording(volume);
    }

    /**
     * Returns the next Name object in the list. If there is no
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
     * Returns the previous Name object in the list. If there is no
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

}
