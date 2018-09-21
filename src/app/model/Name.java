package app.model;

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

    @Override
    public String toString() {
        return _versionName;
    }

    public void playRecording() {
        try {
            String cmd = "ffplay " + _fileName + " -autoexit -nodisp";

            ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
            builder.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getShortName() {
        return _shortName;
    }

    public void setVersionName(String s) {
        _versionName = s;
    }
}
