package app.model;

import java.io.IOException;

public interface IPractiseListModel {

    Name nextName();

    void playCurrentName();

    void createUserRecording();

    void compareUserRecording();

    Name previousName();

    void cancelRecording();

    void setBadQuality() throws IOException;
}
