package app.model;

import java.io.IOException;

public interface Practisable {
    /**
     * Plays the recording of either a concatenated name or single name
     */
    void playRecording(double volume);

    /**
     * Returns true if the user can rate this object as being of bad quality,
     * otherwise false.
     * @return whether or not it can be rated
     */
    boolean isRateable();

    /**
     * Sets the object as being of bad quality
     */
    void setBadQuality() throws IOException;


    /**
     * Returns the date and time of creation of the recording
     * @return
     */
    String getDateTimeCreated();
}