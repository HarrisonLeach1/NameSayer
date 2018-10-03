package app.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PlaylistLoader {

    private final List<ConcatenatedName> _names;

    public PlaylistLoader(File playlistFile) throws FileNotFoundException {
        _names = loadFile(playlistFile);
    }

    public PlaylistLoader(String singleName) {
        _names = loadSingleName(singleName);
    }

    /**
     * Given text file of names, returns a list of names where each name corresponds
     * to a line of names in the given text file.
     * @param playlistFile
     * @return list of names
     * @throws FileNotFoundException
     */
    private List<ConcatenatedName> loadFile(File playlistFile) throws FileNotFoundException {
        Scanner input = new Scanner(playlistFile);
        List<String> list = new ArrayList<String>();

        // load in each line of the text file as a string
        while (input.hasNextLine()) {
            list.add(input.nextLine());
        }

        return stringsToNames(list);
    }

    private List<ConcatenatedName> loadSingleName(String singleName) {

    }


}
