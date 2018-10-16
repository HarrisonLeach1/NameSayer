package app.model;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * A NameVersion object represents a name in a database that can be interacted with
 * by the user. A NameVersion object is usually created by providing a path to file
 * with the following naming format:
 *
 * se206_dd-MM-yyyy_HH-mm-ss_Name.wav
 * e.g. se206_2-5-2018_15-23-50_Mason.wav
 *
 * This NameVersion points to this file and stores information of the file such that
 * it can be efficiently played, rated and displayed to the user.
 */
public class NameVersion {
    private String _shortName, _displayName, _filePath, _dateCreated, _timeCreated;
    private boolean _isBadQuality;

    /**
     * Given the path to a valid recording file, a NameVersion object is created as playable
     * and displayable reference to this file.
     * If the file name is not in the valid format an IndexOutOfBoundsException or ParseException
     * is thrown and the NameVerison object cannot be created.
     * @param filePath
     * @throws IndexOutOfBoundsException
     * @throws ParseException
     */
    public NameVersion(String filePath) throws IndexOutOfBoundsException, ParseException {
        _filePath = filePath;
        parseShortName();
        parseVersionName();
        _isBadQuality = findQuality();
    }

    public NameVersion() {
    }

    /**
     * Parses the file name, turning the date and time format into a more presentable
     * format to the user. Throws an IndexOutOfBoundsException or ParseException if the
     * file name is not in the valid format.
     * @throws IndexOutOfBoundsException
     * @throws ParseException
     */
    private void parseVersionName() throws IndexOutOfBoundsException, ParseException{
            // get file name without the database folder
            String fileName = _filePath.substring(_filePath.indexOf("/"));

            // get original date and time and parse into date object
            String[] parts = fileName.split("_");
            String originalDate = parts[1] + "_" + parts[2];
            DateFormat originalFormat = new SimpleDateFormat("dd-MM-yyyy_hh-mm-ss");
            Date date = originalFormat.parse(originalDate);

            // get date and time in more displayable format
            DateFormat newDateFormat = new SimpleDateFormat("EEE, d MMM yyyy");
            DateFormat newTimeFormat = new SimpleDateFormat("hh:mm:ss a");
            _dateCreated = newDateFormat.format(date);
            _timeCreated = newTimeFormat.format(date);

            _displayName = _shortName + " " + _dateCreated + " " + _timeCreated;
    }

    /**
     * Retrieves just the name of the recording excluding the creation date, time and file extension.
     * @throws IndexOutOfBoundsException
     */
    private void parseShortName() throws IndexOutOfBoundsException{
        String fileName = _filePath.substring(_filePath.indexOf("/"));

        // find the index of the third underscore (the start of the name)
        int startIndex = fileName.indexOf("_", fileName.indexOf("_", fileName.indexOf("_") + 1) + 1);

        // find the index of the .wav extension (the end of the name)
        int lastIndex = fileName.lastIndexOf(".wav");

        // parse the name and replace underscores with spaces
        String nameString = fileName.substring(startIndex + 1, lastIndex).replaceAll("_", " ");

        // capitalise the name
        _shortName = capitalise(nameString);
    }

    public void playRecording(double volume) {
        try {
            String cmd = "ffplay -af volume=" + String.format( "%.1f", volume) + " " + _filePath + " -autoexit -nodisp";

            ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
            Process process = builder.start();

            process.waitFor();

        } catch (IOException | InterruptedException e) {
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
            fw.write(_filePath + "\r\n");
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
            if(line.equals(_filePath)) {
                return true;
            }
        }
        return false;
    }

    private String capitalise(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
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

    public String getFilePath() {
        return _filePath;
    }

    public String getDateCreated() {
        return _dateCreated;
    }

    public String getTimeCreated() {
        return _timeCreated;
    }

    public void setDisplayName(String s) {
        _displayName = capitalise(s);
    }

    @Override
    public String toString() {
        return _displayName;
    }
}
