package app.model;

import javafx.collections.ObservableList;

import java.util.ListIterator;

public class PractiseListModel implements IPractiseListModel{

    private ObservableList<Name> _practiseList;
    private ListIterator<Name> _listIterator;
    private Name _currentName;

    public PractiseListModel(ObservableList<Name> practiseList) {
        _practiseList = practiseList;
        _listIterator = _practiseList.listIterator();
        _currentName = _practiseList.get(0);
    }

    public Name nextName() {
        _currentName = _listIterator.next();
        return _currentName;
    }

    public Name getCurrentName() {
        return _currentName;
    }
}
