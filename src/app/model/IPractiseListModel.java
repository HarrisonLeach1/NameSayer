package app.model;

public interface IPractiseListModel {

    Name nextName();

    void playCurrentName();

    void createUserRecording();

    void compareUserRecording();

    Name previousName();

    void cancelRecording();

}
