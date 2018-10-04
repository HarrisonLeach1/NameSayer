package app.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class UserModel {
    private static final String DATA_FILE = "userData.txt"
    private static final UserModel INSTANCE = new UserModel();
    private int _streakCount;
    private LocalDate _lastLogin;
    private int _userXP;

    private UserModel() throws FileNotFoundException {
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

        }
        _lastLogin = today;
        //writeUserData();
    }

    private void readUserData() throws FileNotFoundException {
        Scanner input = new Scanner(new File(DATA_FILE));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        _lastLogin = LocalDate.parse(input.nextLine(), formatter);

        _streakCount = Integer.parseInt(input.nextLine());

        _userXP = Integer.parseInt(input.nextLine());
    }
}
