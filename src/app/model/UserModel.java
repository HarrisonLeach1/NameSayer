package app.model;

import java.awt.color.ICC_Profile;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class UserModel {
    private static final File DATA_FILE = new File(".userData.txt");
    private static final UserModel INSTANCE = new UserModel();
    private int _streakCount = 1;
    private LocalDate _lastLogin = LocalDate.now();
    private int _userXP = 0;

    private UserModel() {
        readUserData();
        updateStreak();
    }

    public static UserModel getInstance() {
        return INSTANCE;
    }

    public int getDailyStreak() {
        return _streakCount;
    }

    public int updateUserXP() {
        _userXP += 10;
        writeUserData();
        return _userXP;
    }

    private void updateStreak() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        if (_lastLogin.equals(yesterday)) {
            _streakCount++;
        } else if (!_lastLogin.equals(today)) {
            _streakCount = 1;
        }
        _lastLogin = today;
        writeUserData();
    }

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

    private void readUserData() {
        try {
            Scanner input = new Scanner(DATA_FILE);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            _lastLogin = LocalDate.parse(input.nextLine(), formatter);

            _streakCount = Integer.parseInt(input.nextLine());

            _userXP = Integer.parseInt(input.nextLine());
        } catch (FileNotFoundException e) {
            writeUserData();
        }
    }
}
