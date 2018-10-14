package app.controllers;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;

/**
 * A LoadingController holds the responsibility of receiving input events
 * of tracking the progress of the task it is given. It then translates this
 * progress to information to be displayed to the user. It has the responsibility
 * of executing the task on a new thread.
 */
public class LoadingController {

    @FXML private ProgressIndicator _loadIndicator;

    /**
     * Given a task, this method animates the progress indicator to indicate to
     * the user that the task is occurring. When the task is finished the
     * progress indicator stops. Also executes the task on a new thread to
     * unresponsive GUI issues.
     * @param loadTask
     */
    public void showTaskLoading(Task loadTask) {

        // bind task progress to indeterminate progress indicator
        _loadIndicator.progressProperty().bind(loadTask.progressProperty());

        // execute the task on a new thread
        new Thread(loadTask).start();
    }

}
