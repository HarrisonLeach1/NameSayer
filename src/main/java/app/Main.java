package app;

import app.controllers.MainMenuController;
import app.model.DatabaseModel;
import app.model.UserModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private static final String MAIN_MENU_SCENE = "/views/NameSayer.fxml";
    private static final String APPLICATION_TITLE = "NameSayer";


    @Override
    public void start(Stage primaryStage) throws Exception{
        // load the main menu scene
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(MAIN_MENU_SCENE));
        Parent root = loader.load();

        // pass selected items to the next controller
        MainMenuController controller = loader.getController();

        // injection site of the IDatabaseModel and IUserModel types to be used
        controller.setModel(DatabaseModel.getInstance(), UserModel.getInstance());

        // show application
        primaryStage.setTitle(APPLICATION_TITLE);
        primaryStage.setScene(new Scene(root, 1200, 800));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
