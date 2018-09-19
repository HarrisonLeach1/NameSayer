package app.model;

import app.Main;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import java.io.*;

public class Name {
    private String _shortName, _versionName, _fileName;

    public Name(String fileName) {
        _fileName = fileName;
        parseShortName();
        parseVersionName();
    }

    public Name() {

    }

    private void parseVersionName() {
        String[] parts = _fileName.split("_");
        _versionName = _shortName + " " + parts[2] + " " + parts[1];
    }

    /**
     * Returns the name of the recording excluding the creation date, time and file extension.
     */
    private void parseShortName() {
        String dateTimeRemoved = _fileName.substring(_fileName.lastIndexOf('_') + 1);
        _shortName =  dateTimeRemoved.split("\\.")[0];
    }

    public String getShortName() {
        return _shortName;
    }

    public void setVersionName(String s) {
        _versionName = s;
    }

    @Override
    public String toString() {
        return _versionName;
    }

    public void playRecording() {
        System.out.println("playing" + _fileName);
        InputStream in = null;
        try {
            in = new FileInputStream("names/" + _fileName);
            AudioStream audioStream = new AudioStream(in);
            AudioPlayer.player.start(audioStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
