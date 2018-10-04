package app.model;

import javax.xml.crypto.Data;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ConcatenatedName implements Practisable {
    public static final String EXTENSION = "_temp.wav";
    public static final String FOLDER = "temp/";
    public static final double VOLUME_LEVEL = -20.0;

    private final String _displayName;
    private List<Name> _names;
    private String _stringOfPaths;

    public ConcatenatedName(String names) throws NameNotFoundException {
        _names = stringsToList(names);
        _displayName = names;

        makeTempDirectory();
        cutSilence();
        concatenateFileNames();
        normaliseAudio();
        concatenateAudio();
    }

    public void playRecording() {
        try {
            String cmd = "ffplay " + FOLDER + _displayName.replaceAll(" ","_") + EXTENSION + " -autoexit -nodisp";

            ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
            builder.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isRateable() {
        return false;
    }

    @Override
    public void setBadQuality() {
        // A concatenated Name cannot as of yet be rated
    }

    @Override
    public String getDateTimeCreated() {
        return "";
    }


    /**
     *  Converts a string of names into a list of name objects found the DataModel search table
     * @param names the string of names
     * @return list of Name objects
     * @throws NameNotFoundException
     */
    private List<Name> stringsToList(String names) throws NameNotFoundException {
        // replace all hyphens with spaces
        names = names.replaceAll("-"," ");

        // parse strings into a list of strings
        List<String> stringList = new ArrayList<>(Arrays.asList(names.split(" ")));

        List<Name> nameList = new ArrayList<>();

        // get the DataModel table which references the names with their associated strings
        HashMap<String, Name> searchTable = DataModel.getInstance().getDatabaseTable();

        // initialise the variable to store names that are not found
        String missingNames = "";

        // for each string, retrieve the Name object associated with the specific string key
        for (String str : stringList) {
            if (searchTable.containsKey(str.toLowerCase())) {
                nameList.add(searchTable.get(str.toLowerCase()));
            } else {
                missingNames += str + " ";
            }
        }

        // if there are missing names in the string, notify by throwing an exception
        if (!missingNames.equals("")) {
            throw new NameNotFoundException(missingNames);
        }

        return nameList;
    }

    /**
     * Modifies the audio files of the associated names such that they are
     * of similar volume.
     */
    private void normaliseAudio() {
        try {
            for(Name name : _names) {
                // define process for returning the mean volume of the recording
                String cmd = "ffmpeg -y -i " + FOLDER + name.toString() + EXTENSION + " -filter:a volumedetect " +
                        "-f null /dev/null |& grep 'mean_volume:' ";

                // start process
                ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
                Process process = builder.start();

                process.waitFor();

                // read in standard out to be parsed
                InputStream stdout = process.getInputStream();
                BufferedReader stdoutBuffered =new BufferedReader(new InputStreamReader(stdout));
                String lineOut = stdoutBuffered.readLine();

                // Parse the mean volume number from the output, the volume is 2-7 indices from the right of the colon
                int colonIndex = lineOut.lastIndexOf(':');
                String volumeString = lineOut.substring(colonIndex + 2, colonIndex + 7);
                Double meanVolume = Double.parseDouble(volumeString);

                // calculate the adjustment needed to get the audio to the standard volume
                double adjustment = VOLUME_LEVEL - meanVolume;

                // define bash process to create new audio file with the mean volume
                String cmd2 = "ffmpeg -y -i " + FOLDER + name.toString() + EXTENSION + " -filter:a \"volume=" +
                        String.format( "%.1f", adjustment) + " dB\" " + FOLDER + name.toString() + EXTENSION;

                // start process
                ProcessBuilder builder2 = new ProcessBuilder("/bin/bash", "-c", cmd2);
                Process process2 = builder2.start();

                process2.waitFor();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Modifies the audio files of the associated names such that they do
     * not contain any unnecessary silence.
     */
    private void cutSilence() {
        for(Name name : _names) {
            try {
                String cmd = "ffmpeg -y -hide_banner -i " + name.selectGoodVersion().getFileName() +
                        " -af silenceremove=1:0:-50dB:1:5:-70dB:0:peak " + FOLDER + name.toString() + EXTENSION;

                ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
                Process process = builder.start();

                process.waitFor();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * All file names of the good quality versions of each given name are
     * concatenated into a single string which contains input flags for
     * an ffmpeg bash process
     */
    private void concatenateFileNames() {
        _stringOfPaths = "";
        for (Name name : _names) {
            _stringOfPaths += " -i " + FOLDER + name.toString() + EXTENSION;
        }
    }

    /**
     * All modified temporary audio recordings of the names are concatenated
     * into a single recording in a temporary audio file.
     */
    private void concatenateAudio() {
        //  determins the bash process option: -filter_complex '[0:0]...[<N>:0]concat=n=<N>:v=0:a=1[out]'
        //  where <N> is the number of recordings to be concatenated
        String bashFilter = "";
        for(int i = 0; i < _names.size(); i++) {
            bashFilter += "[" + i + ":0]";
        }
        bashFilter += "concat=n=" + _names.size() + ":v=0:a=1[out]";

        // execute the bash process
        try {
            String cmd = "ffmpeg -y" + _stringOfPaths + " -filter_complex '"+ bashFilter + "' " +
                    "-map '[out]' " + FOLDER + _displayName.replaceAll(" ","_") + EXTENSION;

            ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
            Process process = builder.start();

            process.waitFor();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a temporary directory for storing modified audio files.
     */
    private void makeTempDirectory() {
        try {
            String cmd = "mkdir -p " + FOLDER;

            ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
            builder.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return _displayName;
    }
}
