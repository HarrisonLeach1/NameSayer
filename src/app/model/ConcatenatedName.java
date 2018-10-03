package app.model;

import java.io.*;
import java.util.List;

public class ConcatenatedName {
    public static final String TEMP_EXTENSION = "temp/normalised";
    public static final String MERGED_PATH = "temp/merged.wav";
    public static final double VOLUME_LEVEL = -20.0;
    private final String _displayName;
    private List<Name> _names;
    private String _stringOfPaths;

    public ConcatenatedName(List<Name> names) {
        _names = names;
        _displayName = createDisplayName();
        makeTempDirectory();
        concatenateFileNames();
        normaliseAudio();
        concatenateAudio();
    }

    public void playRecording() {
        try {
            String cmd = "ffplay " + MERGED_PATH + " -autoexit -nodisp";

            ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
            builder.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Modifies the audio files of the associated names such that they are
     * of similar volume.
     */
    private void normaliseAudio() {
        double sum = 0;
        double[] meanVolumes = new double[_names.size()];

        try {
            for(int i = 0; i < _names.size(); i++) {
                // define process for returning the mean volume of the recording
                String cmd = "ffmpeg -i " + _names.get(i).selectGoodVersion().getFileName() + " -filter:a volumedetect -f null /dev/null |& grep 'mean_volume:' ";

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
                Double volume = Double.parseDouble(volumeString);

                // store mean volume of the recording and add it to the sum
                meanVolumes[i] = volume;
            }

            // generate normalised version of all recordings
            for(int i = 0; i < _names.size(); i++) {
                // calculate the adjustment needed to get the audio to the standard volume
                double adjustment = VOLUME_LEVEL - meanVolumes[i];

                // define bash process to create new audio file with the mean volume
                String cmd = "ffmpeg -y -i " + _names.get(i).selectGoodVersion().getFileName() + " -filter:a \"volume=" +  String.format( "%.1f", adjustment) + " dB\" " + NORMALISED_PATH + i + ".wav";

                // start process
                ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
                Process process = builder.start();

                process.waitFor();
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
        try {
            String cmd = "ffmpeg -y -hide_banner -i " catherine.wav "-af silenceremove=1:0:-50dB:1:5:-70dB:0:peak " + ;

            ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
            builder.start();

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
        for (int i = 0; i < _names.size(); i++) {
            _stringOfPaths += " -i " + NORMALISED_PATH + i + ".wav";
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

        // executes the bash process
        try {
            String cmd = "ffmpeg -y" + _stringOfPaths +
                    " -filter_complex '"+ bashFilter + "' " +
                    "-map '[out]' " + MERGED_PATH;

            ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
            Process process = builder.start();

            process.waitFor();
            System.out.println("DONE");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a temporary directory for storing modified audio files.
     */
    private void makeTempDirectory() {
        try {
            String cmd = "mkdir -p temp/";

            ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
            builder.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return String to be displayed
     */
    private String createDisplayName() {
        String displayName = "";
        for(Name name : _names) {
            displayName += name.toString() + " ";
        }
        return displayName.trim();
    }

    @Override
    public String toString() {
        return _displayName;
    }
}
