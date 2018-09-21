package app.model;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static app.model.DataModel.USER_DATABASE;

public class Recording {

    public static final int RECORD_TIME = 5;
    private final String _fileName;
    private Process process;

    public Recording(String name) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
        Date date = new Date();
        String dateTime = formatter.format(date);

        _fileName = USER_DATABASE + "se206_" + dateTime + "_" + name + ".wav";
    }

    public void deleteRecording() {
        try {
            String cmd = "rm -rf " + _fileName;

            ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
            builder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    public void cancelRecording() {
        process.destroy();
    }
}
