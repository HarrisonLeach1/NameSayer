package app.model;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static app.model.DatabaseModel.USER_DATABASE;

/**
 * A Recording object represents a production of a name created by a user.
 */
public class Recording {

    public static final int RECORD_TIME = 15;
    private final String _fileName;
    private Process _recordingProcess;

    /**
     * A recording is created by associated it with a name parameter. The user
     * recording version is differentiated by it's creation time. If the name contains
     * spaces they are replaced with underscores in the file name.
     * @param name
     */
    public Recording(String name) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
        Date date = new Date();
        String dateTime = formatter.format(date);

        _fileName = USER_DATABASE.getName()  + "/se206_" + dateTime + "_" + name.replaceAll(" ","_") + ".wav";
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
     * Records the users voice for a maximum of the specified recording time and
     * creates a new file for the recording.
     * @return the NameVersion object pointing to the file of the newly created user recording.
     */
    public NameVersion startRecording() {
        try {
            String cmd = "mkdir -p " + USER_DATABASE.getName() + "; ffmpeg -y -f alsa -t "+ RECORD_TIME +" -i default "+ _fileName;
            System.out.println(cmd);

            ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
            _recordingProcess = builder.start();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // since we have specified the naming format correctly, the exception will not be thrown
        NameVersion name = null;
        try {
            name = new NameVersion(_fileName);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return name;
    }

    /**
     * Stops the recording process gracefully which allows the recorded audio
     * to be saved to a wav file.
     */
    public void finishRecording() {
        OutputStream in = _recordingProcess.getOutputStream();
        PrintWriter stdin = new PrintWriter(in);
        stdin.print("q");
        stdin.close();
    }


    /**
     * Cancels the recording currently being produced by the user.
     */
    public void cancelRecording() {
        _recordingProcess.destroy();
    }
}
