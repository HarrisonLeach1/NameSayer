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
    private static final File DATA_FILE = new File("userData.txt");
    private static final UserModel INSTANCE = new UserModel();
    private int _streakCount = 1;
    private LocalDate _lastLogin;
    private int _userXP = 0;

    private UserModel() {
        readUserData();
        updateStreak();
    }

    public static UserModel getInstance() {
        return INSTANCE;
    }

    public int getDailyStreak() {

    }

    public int getUserXP() {

    }

    private void updateStreak() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        if (_lastLogin == yesterday) {
            _streakCount++;
        }
        _lastLogin = today;
        writeUserData();
    }

    private void writeUserData() {
        File file = DATA_FILE;

        // create userData.txt if it does not already exist
        try {
            file.createNewFile();

            FileWriter fw = new FileWriter(DATA_FILE,false);

            fw.write(_lastLogin + "\r\n");

            fw.write(_streakCount + "\r\n");

            fw.write(_userXP + "\r\n");
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
            e.printStackTrace();
        }
    }
}
