package app.model;

import java.io.File;
import java.util.List;

public class PlaylistLoader {

    private final List<ConcatenatedName> _names;

    public PlaylistLoader(File playlistFile) {
        _names = loadFile(playlistFile);
    }

    public PlaylistLoader(String singleName) {
        _names = loadSingleName(singleName);
    }

    private List<ConcatenatedName> loadFile(File playlistFile) {

    }

    private List<ConcatenatedName> loadSingleName(String singleName) {

    }


}
