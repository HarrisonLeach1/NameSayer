package app.model;

public interface IPractiseListModel {

    Name nextName();

    void createUserRecording();

    void compareUserRecording();

    Name previousName();

    void cancelRecording();

    void keepRecording();

    boolean hasNext();

    boolean hasPrevious();
}
