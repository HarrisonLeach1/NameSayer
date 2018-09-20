package app.model;

import java.util.List;

public interface IUserRecordingsModel {

    void loadUserRecordings();

    List<Name> getRecordingsList();

}
