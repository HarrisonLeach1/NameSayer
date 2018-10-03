package app.model;

import java.util.List;

public class ConcatenatedName {
    private List<Name> _names;

    public ConcatenatedName(List<Name> names) {
        _names = names;
    }

    /**
     * Modifies the audio files of the associated names such that they are
     * of similar volume.
     */
    private void equalizeNames() {

    }

    /**
     * Modifies the audio files of the associated names such that they do
     * not contain any unnecessary  silence.
     */
    private void cutSilence() {

    }

    /**
     * All modified temporary recordings of the names are concatenated into
     * recording in a temporary audio file.
     */
    private void concatenateNames() {

    }
}
