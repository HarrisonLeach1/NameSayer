package app.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class PlaylistLoader {

    private final List<String> _stringList;

    public PlaylistLoader(File playlistFile) throws FileNotFoundException {
        _stringList = loadFileToStrings(playlistFile);
    }

    public PlaylistLoader(String singleName) {
        _stringList = loadSingleNameString(singleName);
    }

    /**
     * Returns the list of Name objects from the the strings inputted to this object.
     * This can be a long running task because new audio files need to be generated.
     * If a name cannot be found in the database a NameNotFoundException will be thrown.
     * @return list of names
     * @throws NameNotFoundException
     */
    public List<ConcatenatedName> getNameList() throws NameNotFoundException {
        List<ConcatenatedName> nameList = new ArrayList<>();
        String missingNames = "";

        // for each string in the list of input strings, create a new name object
        for (String nameString : _stringList) {
            try {
                ConcatenatedName name = new ConcatenatedName(nameString);
                nameList.add(name);

            } catch (NameNotFoundException e) {
                missingNames += e.getMissingNames() +"\n";
            }
        }

        // if some names were missing throw a new exception with the appropriate message
        if (!missingNames.equals("")) {
            throw new NameNotFoundException(missingNames);
        }

        return nameList;
    }


    /**
     * Given text file of names, returns a list of strings where each string corresponds
     * to a line of names in the given text file.
     * @param playlistFile
     * @return list of strings of the names
     * @throws FileNotFoundException
     */
    private List<String> loadFileToStrings(File playlistFile) throws FileNotFoundException {
        Scanner input = new Scanner(playlistFile);
        List<String> stringList = new ArrayList<>();

        // load in each line of the text file, and use each string to create a new Name object
        while (input.hasNextLine()) {
            stringList.add(input.nextLine());
        }

        return stringList;
    }

    /**
     * Given a single name string, returns a list object containing the name string.
     * @param singleName
     * @return a list containing the input string
     */
    private List<String> loadSingleNameString(String singleName) {
        return Arrays.asList(singleName);
    }

    /**
     * Returns a list of strings corresponding to the inputs to the file loader object
     * @return
     */
    public List<String> getStringList() {
        return _stringList;
    }
}
