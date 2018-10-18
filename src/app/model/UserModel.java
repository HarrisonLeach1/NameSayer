package app.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * A UserModel object represents the NameSayer progress information of
 * the user.
 *
 * The information it stores currently are the users daily streak
 * and level. On start-up it reads the user's progress information
 * from a data file. When the object receives updates on the users
 * progress it updates it's information and writes it to the file.
 */
public class UserModel {
    private static final File DATA_FILE = new File(".userData.txt");
    private static final int XP_GAIN = 20;
    private static final int XP_PER_LEVEL = 100;
    private static UserModel _instance;

    private List<UserModelListener> _listeners;
    private int _streakCount = 1;
    private LocalDate _lastLogin = LocalDate.now();
    private int _userXP = 100;
    private int _currentLevel;
    private double _currentLevelProgress;

    private UserModel() {
        _listeners = new ArrayList<>();

        readUserData();
        updateStreak();
        calculateLevelProgress();
    }

    /**
     * Returns the singleton instance of the UserModel, used for loading
     * and saving user data.
     * @return instance of DataModel
     */
    public static UserModel getInstance() {
        if (_instance == null) {
            _instance = new UserModel();
        }
        return _instance;
    }

    /**
     * Registers a listener with this data model object. The registered
     * listener is notified of any events in which the users progress is
     * changed.
     * @param listener
     */
    public void addListener(UserModelListener listener) {
        _listeners.add(listener);
        listener.notifyProgress(_currentLevel,_currentLevelProgress);
    }

    /**
     * Increases the amount of XP the user has and writes this
     * information to a file to be saved for the user upon their
     * next start-up. Notifies any registered listeners.
     */
    public void updateUserXP() {
        _userXP += XP_GAIN;
        writeUserData();
        calculateLevelProgress();

        for(UserModelListener l : _listeners) {
            l.notifyProgress(_currentLevel, _currentLevelProgress);
        }
    }

    private void calculateLevelProgress() {
        _currentLevel = _userXP / XP_PER_LEVEL;
        _currentLevelProgress = (_userXP % XP_PER_LEVEL) / (double) XP_PER_LEVEL;
    }

    /**
     * Updates the users daily streak. A daily streak is the number of
     * successive days the user has used the application.
     */
    private void updateStreak() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        // if they logged in yesterday then it is a streak
        if (_lastLogin.equals(yesterday)) {
            _streakCount++;

        // if their last login was not today they have lost their streak
        } else if (!_lastLogin.equals(today)) {
            _streakCount = 1;
        }

        _lastLogin = today;
        writeUserData();
    }

    /**
     * Writes the current progress information of the user to a file.
     * This ensures the users progress is saved upon their next start-up
     * of the application.
     */
    private void writeUserData() {
        // create .userData.txt if it does not already exist
        try {
            DATA_FILE.createNewFile();

            FileWriter fw = new FileWriter(DATA_FILE,false);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String loginString = _lastLogin.format(formatter);

            fw.write(loginString + "\r\n");

            fw.write(_streakCount + "\r\n");

            fw.write(_userXP + "\r\n");

            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * Reads the existing progress the user has made on the application
     * and updates the fields of this object so that this progress can be
     * displayed back to the user.
     */
    private void readUserData() {
        try {
            Scanner input = new Scanner(DATA_FILE);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            _lastLogin = LocalDate.parse(input.nextLine(), formatter);

            _streakCount = Integer.parseInt(input.nextLine());

            _userXP = Integer.parseInt(input.nextLine());

            // if the file does not exist, write it to a new file
        } catch (FileNotFoundException e) {
            writeUserData();
        }
    }

    public int getDailyStreak() {
        return _streakCount;
    }
}
