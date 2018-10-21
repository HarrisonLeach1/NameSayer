package app.model.processing;
import app.model.Practisable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A SingleName object represents a unique name that may have multiple versions
 * and is practisable by the user.
 */
public class SingleName implements Practisable {
    private final String _name;
    private List<SingleNameVersion> _nameVersions;
    private SingleNameVersion _goodVersion;

    public SingleName(String name) {
        _name = name;
        _nameVersions = new ArrayList<>();
    }

    /**
     * Given a list, if there exists a good quality version in the list of a given name, one
     * is chosen at and returned. Otherwise, a bad recording is returned.
     * @return SingleNameVersion that was selected
     */
    public SingleNameVersion selectGoodVersion() {
        // loop through _nameVersions of versions of names until a good quality version is found
        for(SingleNameVersion currentName: _nameVersions) {
            if(!currentName.isBadQuality()) { // if good (not bad) quality, return the version
                _goodVersion = currentName;
                return currentName;
            }
        }

        // if no good quality version is found return any recording
        _goodVersion = _nameVersions.get(0);
        return _nameVersions.get(0);

    }

    /**
     * Adds a SingleNameVersion to the list of versions of this name
     * @param name
     */
    public void add(SingleNameVersion name) {
        _nameVersions.add(name);
    }

    /**
     * Returns the number of different recording verisons pf this name.  If this name
     * contains more than Integer.MAX_VALUE recordings, returns Integer.MAX_VALUE.
     *
     * @return the number of recordings if the name
     */
    public int size() {
        return _nameVersions.size();
    }

    /**
     * Returns the version at the specified position in the list.
     *
     * @param i index of the element to return
     * @return the version at the specified position in the list
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public SingleNameVersion get(int i) {
        return _nameVersions.get(i);
    }

    /**
     * The current recording of the name which was said to be of good
     * quality is marked as bad quality.
     * @throws IOException
     */
    public void setBadQuality() throws IOException {
        selectGoodVersion().setBadQuality();
    }

    /**
     * Plays the audio of a good quality recording that was found of this SingleName (if one exist).
     * Note that this method is blocking call and so should be executed on a new thread.
     * @param volume 0 means silence, 1.0 means no volume reduction or amplification, 2.0 mans the original
     *               audio is amplified by double, etc.
     * @throws InterruptedException
     */
    public void playRecording(double volume) throws InterruptedException{
        _goodVersion.playRecording(volume);
    }

    /**
     * Stops the currently playing audio file from being played. This will cause an
     * InterruptedException to be thrown by the playRecording method during execution.
     */
    public void stopRecording() {
        _goodVersion.stopRecording();
    }

    @Override
    public String getDateTimeCreated() {
        return _goodVersion.getDateCreated() + " " + _goodVersion.getTimeCreated();
    }

    @Override
    public String getMissingNames() {
        return "";
    }

    @Override
    public boolean isRateable() {
        return true;
    }

    @Override
    public String toString() {
        return _name;
    }
}
