package app.model;

import java.io.IOException;

public interface IPractiseListModel {

    Name nextName();

    void playCurrentName();

    boolean hasNext();

    void createUserRecording();

    void keepRecording();

    void compareUserRecording();

    void setBadQuality() throws IOException;
}
