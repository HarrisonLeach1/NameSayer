package app.model;

import java.io.IOException;
import java.util.Collection;

public interface Practisable {
    /**
     * Plays the recording of a practisable object which allows the user
     * to hear a name, which they can record and compare themselves to.
     */
    void playRecording(double volume);

    /**
     * Returns true if the user can rate this object as being of bad quality,
     * otherwise false.
     * @return whether or not it can be rated
     */
    boolean isRateable();

    /**
     * Sets the object as being of bad quality such that it will now be
     * played less often when the associated name is requested.
     */
    void setBadQuality() throws IOException;


    /**
     * Returns the date and time of creation of the recording in an
     * appropriate format that can be displayed to the user. Otherwise
     * it should return nothing, indicating it was not created at a single
     * point in time.
     * @return the date/time the recording was created if it has one
     */
    String getDateTimeCreated();

    /**
     * Returns a string of missing names which indicates the a displayable
     * string of names that this Practisable object is associated with, but
     * cannot find the recording files of.
     * @return
     */
    String getMissingNames();
}
