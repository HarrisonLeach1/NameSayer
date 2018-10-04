package app.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class PlaylistLoader {

    private final List<ConcatenatedName> _names;

    public PlaylistLoader(File playlistFile) throws FileNotFoundException, NameNotFoundException {
        _names = loadFile(playlistFile);
    }

    public PlaylistLoader(String singleName) throws NameNotFoundException {
        _names = loadSingleName(singleName);
    }

    /**
     * Given text file of names, returns a list of names where each name corresponds
     * to a line of names in the given text file.
     * @param playlistFile
     * @return list of names
     * @throws FileNotFoundException
     */
    private List<ConcatenatedName> loadFile(File playlistFile) throws FileNotFoundException, NameNotFoundException {
        Scanner input = new Scanner(playlistFile);
        List<ConcatenatedName> nameList = new ArrayList<>();

        // load in each line of the text file, and use each string to create a new Name object
        while (input.hasNextLine()) {
            nameList.add(new ConcatenatedName(input.nextLine()));
        }

        return nameList;
    }

    /**
     * Given a single name string, returns a Name object of the associated name.
     * @param singleName
     * @return a list containing a single name
     */
    private List<ConcatenatedName> loadSingleName(String singleName) throws NameNotFoundException {
        return Arrays.asList(new ConcatenatedName(singleName));
    }

    /**
     * Returns the list of Name objects created by this playlist loader object
     * @return list of names
     */
    public List<ConcatenatedName> getList() {
        return _names;
    }
}
