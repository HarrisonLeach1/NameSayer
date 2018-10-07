package app.model;

import java.io.*;
import java.util.List;

/**
 * Represents a Name that is composed of multiple name recordings that can
 * be played.
 *
 * Within the Concatenated Name class Name objects are normalised with silence cut
 * and concatenated.
 */
public class ConcatenatedName implements Practisable {
    public static final String EXTENSION = "_temp.wav";
    public static final String TEMP_FOLDER = "temp/";
    public static final double VOLUME_LEVEL = -20.0;

    private final String _displayName;
    private List<Name> _names;
    private String _stringOfPaths;
    private String _missingNames = "";

    public ConcatenatedName(List<Name> names, String displayName) {
        _displayName = displayName;
        _names= names;

        makeTempDirectory();
        cutSilence();
        concatenateFileNames();
        normaliseAudio();
        concatenateAudio();
    }

    /**
     * Plays the recording via ffmpeg at the specified volume
     * @param volume
     */
    public void playRecording(double volume) {
        // replace all spaces with underscores
        String file = TEMP_FOLDER + _displayName.replaceAll(" ","_") + EXTENSION;
        try {
            String cmd = "ffplay -af volume=" + String.format( "%.1f", volume) + " " + file + " -autoexit -nodisp";
            System.out.println(cmd);

            ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
            Process process  = builder.start();

            process.waitFor();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Modifies the audio files of the associated names such that they are
     * of similar volume.
     */
    private void normaliseAudio() {
        try {
            for(Name name : _names) {
                // define process for returning the mean volume of the recording
                String cmd = "ffmpeg -y -i " + TEMP_FOLDER + name.toString() + EXTENSION + " -filter:a volumedetect " +
                        "-f null /dev/null |& grep 'mean_volume:' ";
                System.out.println(cmd);

                // start process
                ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
                Process process = builder.start();

                process.waitFor();

                // read in standard out to be parsed
                InputStream stdout = process.getInputStream();
                BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
                String lineOut = stdoutBuffered.readLine();

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
                // 1:0 -50dB indicates that anything below -50dB is cut off from the start
                // 1:% -50dB indicates that anything below -70dB is cut off from the end
                String cmd = "ffmpeg -y -hide_banner -i " + name.selectGoodVersion().getFileName() +
                        " -af silenceremove=1:0:-50dB:1:5:-70dB " + TEMP_FOLDER + name.toString() + EXTENSION;
                System.out.println(cmd);


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
            _stringOfPaths += " -i " + TEMP_FOLDER + name.toString() + EXTENSION;
        }
    }

    /**
     * All modified temporary audio recordings of the names are concatenated
     * into a single recording in a temporary audio file.
     */
    private void concatenateAudio() {
        //  determines the bash process option: -filter_complex '[0:0]...[<N>-1:0]concat=n=<N>:v=0:a=1[out]'
        //  where <N> is the number of recordings to be concatenated
        String bashFilter = "";
        for(int i = 0; i < _names.size(); i++) {
            bashFilter += "[" + i + ":0]";
        }
        bashFilter += "concat=n=" + _names.size() + ":v=0:a=1[out]";

        // execute the bash process
        try {
            String cmd = "ffmpeg -y" + _stringOfPaths + " -filter_complex '"+ bashFilter + "' " +
                    "-map '[out]' " + TEMP_FOLDER + _displayName.replaceAll(" ","_") + EXTENSION;

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
            String cmd = "mkdir -p " + TEMP_FOLDER;

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

    @Override
    public boolean isRateable() {
        return false;
    }

    @Override
    public void setBadQuality() {
        // A concatenated Name cannot yet be rated
    }

    @Override
    public String getDateTimeCreated() {
        return "";
    }

    public void setMissingNames(String missingNames) {
        _missingNames = missingNames;
    }

    public String getMissingNames() {
        return _missingNames;
    }
}
