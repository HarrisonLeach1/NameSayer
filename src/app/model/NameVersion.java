package app.model;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * A NameVersion object represents a name in a database that can be interacted with
 * by the user. A NameVersion object is usually created by providing a file name
 * in the appropirate format:
 *
 * se206_dd-MM-yyyy_HH-mm-ss_Name.wav
 *
 *  e.g. se206_2-5-2018_15-23-50_Mason.wav
 *
 *  This NameVersion points to this file and stores information of the file such that
 *  it can be efficiently played, rated and displayed to the user.
 */
public class NameVersion {
    private String _shortName, _displayName, _fileName, _dateCreated, _timeCreated;
    private boolean _isBadQuality;

    public NameVersion(String fileName) {
        _fileName = fileName;
        parseShortName();
        parseVersionName();
        _isBadQuality = findQuality();
    }

    public NameVersion() {
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

            _displayName = _shortName + " " + _dateCreated + " " + _timeCreated;

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves just the name of the recording excluding the creation date, time and file extension.
     */
    private void parseShortName() {
        // get the last part of the file name
        String dateTimeRemoved = _fileName.substring(_fileName.lastIndexOf('_') + 1);

        // remove the file extension
        String uncapitalisedName =  dateTimeRemoved.split("\\.")[0];

        // capitalise the name
        _shortName = uncapitalisedName.substring(0, 1).toUpperCase() + uncapitalisedName.substring(1);
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
     * The invoked NameVersion object is given a bad quality rating by the user.
     * This information is stored in a text file on the users machine.
     * @throws IOException
     */
    public void setBadQuality() throws IOException {
        FileWriter fw = new FileWriter("bad.txt", true); //the true will append the new data

        if (!_isBadQuality) { // if it is not already bad quality, mark as bad quality
            fw.write(_fileName + "\r\n");
            _isBadQuality = true;
        }
        fw.close();
    }

    /**
     * Returns true if the specific recording related to this file name
     * has been marked as bad quality by the user. Otherwise, returns false.
     * @throws IOException
     */
    private boolean findQuality() {
        File file = new File("bad.txt");
        Scanner scanner = null;

        // create bad.txt if it does not already exist
        try {
            file.createNewFile();
            scanner = new Scanner(file);
        } catch (IOException e) {
            e.printStackTrace(); // incorrect filename
        }

        // loop through all lines of the file until the version is found, otherwise it is not bad
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if(line.equals(_fileName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Indicates if this recording version has previously been marked as being of
     * bad quality
     * @return
     */
    public boolean isBadQuality() {
        return _isBadQuality;
    }
    
    public String getShortName() {
        return _shortName;
    }

    public String getFileName() {
        return _fileName;
    }

    public String getDateCreated() {
        return _dateCreated;
    }

    public String getTimeCreated() {
        return _timeCreated;
    }

    public void setDisplayName(String s) {
        _displayName = s;
    }

    @Override
    public String toString() {
        return _displayName;
    }
}
