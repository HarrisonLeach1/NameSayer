package app.model;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * A Name object represents a name in a database that can be interacted with
 * by the user. A Name object is usually created by providing a file name
 * in the appropirate format:
 *
 * se206_dd-MM-yyyy_HH-mm-ss_Name.wav
 *
 *  e.g. se206_2-5-2018_15-23-50_Mason.wav
 *
 *  This Name points to this file and stores information of the file such that
 *  it can be efficiently played, rated and displayed to the user.
 */
public class Name {
    private String _shortName, _versionName, _fileName, _dateCreated, _timeCreated;

    public Name(String fileName) {
        _fileName = fileName;
        parseShortName();
        parseVersionName();
    }

    public Name() {
    }

    /**
     * Parses the file name, turning the date and time format into a more presentable
     * format to the user. The name must be in the appropriate format.
     */
    private void parseVersionName() {
        try {
            String[] parts = _fileName.split("_");
            String originalDate = parts[1] + "_" + parts[2];

            DateFormat originalFormat = new SimpleDateFormat("dd-MM-yyyy_hh-mm-ss");

            Date date = originalFormat.parse(originalDate);

            DateFormat newDateFormat = new SimpleDateFormat("EEE, d MMM yyyy");
            DateFormat newTimeFormat = new SimpleDateFormat("hh:mm:ss a");

            _dateCreated = newDateFormat.format(date);
            _timeCreated = newTimeFormat.format(date);

            _versionName = _shortName + " " + _dateCreated + " " + _timeCreated;

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves just the name of the recording excluding the creation date, time and file extension.
     */
    private void parseShortName() {
        String dateTimeRemoved = _fileName.substring(_fileName.lastIndexOf('_') + 1);
        _shortName =  dateTimeRemoved.split("\\.")[0];
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

    /**
     * The invoked Name object is given a bad quality rating by the user.
     * This information is stored in a text file on the users machine.
     * @throws IOException
     */
    public void setBadQuality() throws IOException {
        File file = new File("bad.txt");
        file.createNewFile();
        FileWriter fw = new FileWriter("bad.txt",true); //the true will append the new data
        Scanner scanner = new Scanner(file);
        boolean found = false;

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if(line.equals(_versionName)) {
                found=true;
            }
        }
        if (!found) {
            fw.write(_versionName + "\r\n");
        }
        fw.close();
    }

    public String getShortName() {
        return _shortName;
    }

    public String getDateCreated() {
        return _dateCreated;
    }

    public String getTimeCreated() {
        return _timeCreated;
    }

    @Override
    public String toString() {
        return _versionName;
    }

    public void setVersionName(String s) {
        _versionName = s;
    }
}
