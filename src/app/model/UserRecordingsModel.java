package app.model;

import javafx.collections.ObservableList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UserRecordingsModel implements IUserRecordingsModel{
    public static final String USER_DATABASE = "./userRecordings/";
    private List<Name> _recordingsList;


    public void loadUserRecordings() {
        _recordingsList = new ArrayList<Name>();

        File[] files = new File(USER_DATABASE).listFiles();

        // loop through files to add recordings to table
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {

                    // retrieve full file name and create a name object from it
                    Name name = new Name(USER_DATABASE + file.getName());

                    _recordingsList.add(name);

                }
            }
        }
    }

    public List<Name> getRecordingsList() {
        return _recordingsList;
    }
}
