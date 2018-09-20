package app.controllers;

import app.model.IPractiseListModel;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class RecordingSceneController implements Initializable {

    @FXML private Button start_btn, cancel_btn;
    @FXML private ProgressBar progressBar = new ProgressBar(0);

    Task recordWorker;
    IPractiseListModel _practiseListModel;


    public void initModel(IPractiseListModel model) {
        _practiseListModel = model;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cancel_btn.setDisable(true);
    }

    public void handleReturnAction(ActionEvent event){
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.close();
    }

    public void startButtonPressed() {
        _practiseListModel.createUserRecording();

        start_btn.setDisable(true);
        progressBar.setProgress(0);
        cancel_btn.setDisable(false);
        recordWorker = startWorker();

        progressBar.progressProperty().unbind();
        progressBar.progressProperty().bind(recordWorker.progressProperty());

        new Thread(recordWorker).start();
    }

    public void cancelButtonPressed() {
        start_btn.setDisable(false);
        cancel_btn.setDisable(true);

        recordWorker.cancel(true);
        progressBar.progressProperty().unbind();
        progressBar.setProgress(0);

        _practiseListModel.cancelRecording();
    }

    public Task startWorker() {
        return new Task() {

            @Override
            protected Object call() throws Exception {
                for (int i = 0; i < 50; i++) {
                    Thread.sleep(100);
                    updateProgress(i+1,50);
                }
                return true;
            }
        };

    }


}
