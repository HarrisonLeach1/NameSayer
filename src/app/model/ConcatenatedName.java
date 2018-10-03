package app.model;

import java.io.*;
import java.util.List;

public class ConcatenatedName {
    public static final String FILE_NAME = "merged.wav";
    private List<Name> _names;
    private String _stringOfNames;

    public ConcatenatedName(List<Name> names) {
        _names = names;
        concatenateFileNames();
        normaliseAudio();
        concatenateAudio();
    }

    /**
     * Modifies the audio files of the associated names such that they are
     * of similar volume.
     */
    private void normaliseAudio() {
        double sum = 0;

        for(Name name : _names) {
            try {
                // define process for returning the mean volume of the recording
                String cmd = "ffmpeg -i " + name.selectGoodVersion().getFileName() + " -filter:a volumedetect -f null /dev/null |& grep 'mean_volume:' ";

                // start process
                ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
                Process process = builder.start();

                // read in standard out to be parsed
                InputStream stdout = process.getInputStream();
                BufferedReader stdoutBuffered =new BufferedReader(new InputStreamReader(stdout));
                String lineOut = stdoutBuffered.readLine();

                // Parse the mean volume number from the output, the 2-7 indices from the right of the colon
                int colonIndex = lineOut.lastIndexOf(':');
                String volumeString = lineOut.substring(colonIndex + 2, colonIndex + 7);
                Double volume = Double.parseDouble(volumeString);

                sum += volume;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Modifies the audio files of the associated names such that they do
     * not contain any unnecessary  silence.
     */
    private void cutSilence() {

    }

    /**
     * All file names of the good quality versions of each given name are
     * concatenated into a single string which contains input flags for
     * an ffmpeg bash process
     */
    private void concatenateFileNames() {
        _stringOfNames = "";
        for (Name n : _names) {
            _stringOfNames += " -i " + n.selectGoodVersion().getFileName();
        }
    }

    /**
     * All modified temporary audio recordings of the names are concatenated
     * into a single recording in a temporary audio file.
     * ffmpeg -normalize ./names/se206_18-5-2018_11-8-55_Lee.wav -o output1.wav -c:a aac -b:a 192k
     * ffmpeg -normalize ./names/se206_21-5-2018_14-9-29_Antony.wav -o output2.wav -c:a aac -b:a 192k
     * ffmpeg -i ./names/se206_18-5-2018_11-8-55_Lee.wav -filter:a loudnorm output1.wav
     *      * ffmpeg -i ./names/se206_21-5-2018_14-9-29_Antony.wav -filter:a loudnorm output2.wav
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
            String cmd = "ffmpeg -y" + _stringOfNames +
                    " -filter_complex '"+ bashFilter + "' " +
                    "-map '[out]' " + FILE_NAME;
            System.out.println(cmd);

            ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
            builder.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playRecording() {
        try {
            String cmd = "ffplay " + FILE_NAME + " -autoexit -nodisp";

            ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
            builder.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
