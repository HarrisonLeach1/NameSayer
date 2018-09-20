package app.model;

import javafx.collections.ObservableList;

import java.util.ListIterator;

public class PractiseListModel implements IPractiseListModel{

    private ObservableList<Name> _practiseList;
    private ListIterator<Name> _listIterator;
    private Name _currentName;
    private boolean _keepRecording;
    private Recording _currentUserRecording;
    private Name _currentUserCreatedName;

    public PractiseListModel(ObservableList<Name> practiseList) {
        _practiseList = practiseList;
        _listIterator = _practiseList.listIterator();
        _currentName = _practiseList.get(0);
    }

    public Name nextName() {
        if (_currentUserRecording != null && !_keepRecording) {
            _currentUserRecording.deleteRecording();
        }
        _keepRecording = false;

        _currentName = _listIterator.next();
        return _currentName;
    }

    public void playCurrentName() {
        if (_currentName != null) {
            _currentName.playRecording();
        }
    }

    public boolean hasNext() {
        return _listIterator.hasNext();
    }

    public void keepRecording(){
        _keepRecording = true;
    }

    public void createUserRecording() {
        _currentUserRecording = new Recording(_currentName.getShortName());
        _currentUserCreatedName = _currentUserRecording.createRecording();
    }

    public void compareUserRecording() {
        if (_currentUserCreatedName == null) { return; }

        _currentUserCreatedName.playRecording();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        _currentName.playRecording();
    }

}
