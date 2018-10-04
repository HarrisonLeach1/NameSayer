package app.model;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Name implements Practisable{
    private final String _name;
    private List<NameVersion> _nameVersions;

    public Name(String name) {
        _name = name;
        _nameVersions = new ArrayList<>();
    }

    /**
     * Given a list, if there exists a good quality version in the list of a given name, one
     * is chosen at and returned. Otherwise, a bad recording is returned.
     * @return NameVersion that was selected
     */
    public NameVersion selectGoodVersion() {
        // loop through _nameVersions of versions of names until a good quality version is found
        for(NameVersion currentName: _nameVersions) {
            if(!currentName.isBadQuality()) { // if good (not bad) quality, return the version
                return currentName;
            }
        }
        // if no good quality version is found return any recording
        return _nameVersions.get(0);

    }

    /**
     * Adds a NameVersion to the list of versions of this name
     * @param name
     */
    public void add(NameVersion name) {
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
    public NameVersion get(int i) {
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

    public void playRecording() {
        selectGoodVersion().playRecording();
    }

    @Override
    public boolean isRateable() {
        return true;
    }

    @Override
    public String toString() {
        return _name;
    }

    public String getDateCreated() {
        return selectGoodVersion().getDateCreated();
    }

    public String getTimeCreated() {
        return selectGoodVersion().getTimeCreated();
    }
}
