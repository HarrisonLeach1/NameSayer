package app.model;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static app.model.DataModel.USER_DATABASE;

/**
 * A Recording object represents a production of a name created by a user.
 */
public class Recording {

    public static final int RECORD_TIME = 5;
    private final String _fileName;
    private Process process;

    /**
     * A recording is created by associated it with a name parameter. The user
     * recording version is differentiated by it's creation time.
     * @param name
     */
    public Recording(String name) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
        Date date = new Date();
        String dateTime = formatter.format(date);

        _fileName = USER_DATABASE + "se206_" + dateTime + "_" + name + ".wav";
    }

    /**
     * Deletes the file of the recording created by the user.
     */
    public void deleteRecording() {
        try {
            String cmd = "rm -rf " + _fileName;

            ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
            builder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Records the users voice for the specified recording time and creates a new
     * file for the recording.
     * @return the Name object pointing to the file of the newly created user recording.
     */
    public Name createRecording() {
        try {
            String cmd = "mkdir -p " + USER_DATABASE+ "; ffmpeg -y -f alsa -t "+ RECORD_TIME +" -i default "+ _fileName;

            ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
            process = builder.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Name(_fileName);
    }


    /**
     * Cancels the recording currently being produced by the user.
     */
    public void cancelRecording() {
        process.destroy();
    }
}
