package app.model.processing;

import app.model.Practisable;

import java.io.*;
import java.util.*;
import java.util.Iterator;

/**
 * A ConcatenatedName object represents a SingleName that is composed of multiple name
 * recordings that is practisable by the user.
 *
 * Within the Concatenated SingleName class SingleName objects are normalised with silence cut
 * and concatenated.
 */
public class ConcatenatedName implements Practisable {
    private static final String EXTENSION = "_temp.wav";
    private static final String BASH_LOCATION = "/bin/bash";
    public static final String TEMP_FOLDER = "temp/";

    // indicates the levels of silence which are cut from the start and the end
    private static final int START_THRESHOLD = -35;
    private static final int END_THRESHOLD = -50;
    private static final double VOLUME_LEVEL = -20.0;

    private final String _displayName;
    private List<SingleName> _nameList;
    private String _stringOfPaths;
    private String _missingNames = "";
    private Process _playingProcess;

    public ConcatenatedName(String name, HashMap<String, SingleName> databaseTable) throws InterruptedException {
        _displayName = name;

        parseNameList(name, databaseTable);
        createAudio();
    }

    /**
     * Creates a new bash process which plays the audio file associated with this ConcatenatedName object
     * at the given volume. Note that this method is a blocking call and as such should be executed on a
     * new thread.
     * @param volume 0 means silence, 1.0 means no volume reduction or amplification, 2.0 mans the original
     *               audio is amplified by double, etc.
     * @throws InterruptedException
     */
    public void playRecording(double volume) throws InterruptedException {
        // replace all spaces with underscores
        String file = TEMP_FOLDER + _displayName.replaceAll(" ","_") + EXTENSION;
        try {
            String cmd = "ffplay -af volume=" + String.format( "%.1f", volume) + " " + file + " -autoexit -nodisp";

            ProcessBuilder builder = new ProcessBuilder(BASH_LOCATION, "-c", cmd);
            _playingProcess  = builder.start();

            _playingProcess.waitFor();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Ends the bash process which is playing the audio file of this ConcatenatedName object.
     * This will cause an InterruptedException to be thrown by the playRecording method
     * during execution.
     */
    public void stopRecording() {
        if(_playingProcess != null) {
            _playingProcess.destroy();
        }
    }

    /**
     * Creates the audio file that is associated with this ConcatenatedName object.
     * The audio file is built from the List of Names of this ConcatenatedName object.
     * The resulting audio file is all the SingleName recordings concatenated with silence
     * cut and audio normalised between the recordings.
     * @throws InterruptedException
     */
    private void createAudio() throws InterruptedException {
        makeTempDirectory();
        cutSilence();
        normaliseAudio();
        concatenateFileNames();
        concatenateAudio();
    }

    /**
     * Creates a temporary directory for storing modified audio files.
     */
    private void makeTempDirectory() {
        try {
            String cmd = "mkdir -p " + TEMP_FOLDER;

            ProcessBuilder builder = new ProcessBuilder(BASH_LOCATION, "-c", cmd);
            builder.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Modifies the audio files of the associated names such that they do
     * not contain any unnecessary silence.
     */
    private void cutSilence() {
        for(SingleName name : _nameList) {
            try {
                // 1:0 -50dB indicates that anything below -50dB is cut off from the start
                // 1:5 -50dB indicates that anything below -70dB is cut off from the end
                String cmd = "ffmpeg -y -i " + name.selectGoodVersion().getFilePath() +
                        " -af silenceremove=1:0:"+ START_THRESHOLD +"dB:1:5:"+ END_THRESHOLD +"dB " + TEMP_FOLDER + name.toString() + EXTENSION;

                ProcessBuilder builder = new ProcessBuilder(BASH_LOCATION, "-c", cmd);
                Process process = builder.start();

                process.waitFor();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Modifies the audio files of the associated names such that they are
     * of similar volume.
     */
    private void normaliseAudio() throws InterruptedException {
        try { // use iterator to allow for deletion while iterating
            for(Iterator<SingleName> it = _nameList.iterator(); it.hasNext();) {
                SingleName name = it.next();

                // define process for returning the mean volume of the recording
                String cmd = "ffmpeg -y -i " + TEMP_FOLDER + name.toString() + EXTENSION + " -filter:a volumedetect " +
                        "-f null /dev/null |& grep 'mean_volume:' ";

                // start process
                ProcessBuilder builder = new ProcessBuilder(BASH_LOCATION, "-c", cmd);
                Process process = builder.start();

                process.waitFor();

                // read in standard out to be parsed
                InputStream stdout = process.getInputStream();
                BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
                String lineOut = stdoutBuffered.readLine();

                // if the audio cannot be detected, remove it from the name from the recording list
                if(lineOut == null) {
                    it.remove();
                    continue;
                }

                // Parse the mean volume number from the output, the volume is 2-7 indices from the right of the colon
                int colonIndex = lineOut.lastIndexOf(':');
                String volumeString = lineOut.substring(colonIndex + 2, colonIndex + 7);
                Double meanVolume = Double.parseDouble(volumeString);

                // calculate the adjustment needed to get the audio to the standard volume
                double adjustment = VOLUME_LEVEL - meanVolume;

                // define bash process to create new audio file with the mean volume
                String cmd2 = "ffmpeg -y -i " + TEMP_FOLDER + name.toString() + EXTENSION + " -filter:a \"volume=" +
                        String.format( "%.1f", adjustment) + " dB\" " + TEMP_FOLDER + name.toString() + EXTENSION;

                // start process
                ProcessBuilder builder2 = new ProcessBuilder(BASH_LOCATION, "-c", cmd2);
                Process process2 = builder2.start();

                process2.waitFor();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * All file names of the good quality versions of each given name are
     * concatenated into a single string which contains input flags for
     * an ffmpeg bash process
     */
    private void concatenateFileNames() {
        _stringOfPaths = "";
        for (SingleName name : _nameList) {
            _stringOfPaths += " -i " + TEMP_FOLDER + name.toString() + EXTENSION;
        }
    }

    /**
     * All modified temporary audio recordings of the names are concatenated
     * into a single recording in a temporary audio file.
     */
    private void concatenateAudio() throws InterruptedException {
        //  determines the bash process option: -filter_complex '[0:0]...[<N>-1:0]concat=n=<N>:v=0:a=1[out]'
        //  where <N> is the number of recordings to be concatenated
        String bashFilter = "";
        for(int i = 0; i < _nameList.size(); i++) {
            bashFilter += "[" + i + ":0]";
        }
        bashFilter += "concat=n=" + _nameList.size() + ":v=0:a=1[out]";

        // execute the bash process
        try {
            String cmd = "ffmpeg -y" + _stringOfPaths + " -filter_complex '"+ bashFilter + "' " +
                    "-map '[out]' " + TEMP_FOLDER + _displayName.replaceAll(" ","_") + EXTENSION;

            ProcessBuilder builder = new ProcessBuilder(BASH_LOCATION, "-c", cmd);
            Process process = builder.start();

            process.waitFor();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Given an input string, creates a List of Names from the string that are present
     * in the given database table of Names.
     * Different names should be separated by a space or hyphen in the input string.
     * @param inputString
     * @return a ConcatenatedName corresponding to the input string
     * @throws InterruptedException
     */
    private void parseNameList(String inputString, HashMap<String, SingleName> databaseTable) throws InterruptedException {
        _nameList = new ArrayList<>();

        List<String> stringList = formatStringList(inputString);

        // If the name is empty, then the entire name is invalid
        if(stringList.size() < 1) {
            _missingNames = inputString;
        }

        stringListToNameList(stringList, databaseTable);
    }

    /**
     * Given an input string, formats each name in the string into its own string in the
     * output List. Different names should be separated by a space or hyphen in the input string.
     * @param inputString
     * @return
     */
    private List<String> formatStringList(String inputString) {
        // remove leading and trailing spaces
        inputString = inputString.trim();

        // replace all hyphens with spaces
        String splitString = inputString.replaceAll("-", " ");

        // parse strings into a list of strings
        return new ArrayList<>(Arrays.asList(splitString.split(" ")));
    }

    /**
     * Given a List of strings, creates a List of Names from the strings taht are present
     * in the given database table of Names.
     * @param stringList
     * @param databaseTable
     */
    private void stringListToNameList(List<String> stringList, HashMap<String, SingleName> databaseTable) {
        for (String str : stringList) {
            if (databaseTable.containsKey(str.toLowerCase())) {
                _nameList.add(databaseTable.get(str.toLowerCase()));
            } else {
                _missingNames += str + " ";
            }
        }
    }

    @Override
    public String toString() {
        return _displayName;
    }

    @Override
    public boolean isRateable() {
        return false;
    }

    @Override
    public void setBadQuality() {
        // A ConcatenatedName cannot yet be rated
    }

    @Override
    public String getDateTimeCreated() {
        // A ConcatenatedName does not have a single date of creation
        return "";
    }

    public String getMissingNames() {
        return _missingNames;
    }
}
