package app.model;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Recording {

    private static final int RECORD_TIME = 5;
    private final String _fileName;

    public Recording(String name) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
        Date date = new Date();
        String dateTime = formatter.format(date);

        _fileName = "userRecordings/"+ dateTime + "_" + name + ".wav";
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
            String cmd = "ffmpeg -y -f alsa -t "+ RECORD_TIME +" -i default "+ _fileName;

            ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
            builder.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Name(_fileName);
    }
}
