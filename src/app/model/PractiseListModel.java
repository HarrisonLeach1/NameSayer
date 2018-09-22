package app.model;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ListIterator;
import java.util.Scanner;

public class PractiseListModel implements IPractiseListModel{

    private ObservableList<Name> _practiseList;
    private Recording _currentUserRecording;
    private Name _currentUserCreatedName;
    private int _currentIndex;
    private boolean _keepRecording;


    private Task compareWorker;



    public PractiseListModel(ObservableList<Name> practiseList) {
        _practiseList = practiseList;
        _currentIndex = -1;
    }

    public void playCurrentName() {
            _practiseList.get(_currentIndex).playRecording();
    }

    public void createUserRecording() {
        _currentUserRecording = new Recording(_practiseList.get(_currentIndex).getShortName());
        _currentUserCreatedName = _currentUserRecording.createRecording();
    }

    public void compareUserRecording() {
        if (_currentUserCreatedName == null) { return; }

        compareWorker = compareWorker();
        new Thread(compareWorker).start();

    }

    public Name nextName() {
        finaliseRecording();
        if (_currentIndex != _practiseList.size() - 1) {
            _currentIndex++;
        }

        return _practiseList.get(_currentIndex);
    }


    public Name previousName() {
        finaliseRecording();
        if (_currentIndex != 0) {
            _currentIndex--;
        }

        return _practiseList.get(_currentIndex);
    }

    public void cancelRecording(){
        _currentUserRecording.cancelRecording();
        _currentUserRecording.deleteRecording();
    }

    private Task compareWorker() {
        return new Task() {

            @Override
            protected Object call() throws Exception {
                _currentUserCreatedName.playRecording();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                _practiseList.get(_currentIndex).playRecording();
                return true;
            }
        };

    }

    public void keepRecording() {
        _keepRecording = true;
    }

    private void finaliseRecording() {
        if (!_keepRecording && _currentUserRecording != null) {
            _currentUserRecording.deleteRecording();
        }
        _currentUserRecording = null;
        _keepRecording = false;
    }


    public void setBadQuality() throws IOException {
        File file = new File("bad.txt");
        file.createNewFile();
        FileWriter fw = new FileWriter("bad.txt",true); //the true will append the new data
        Scanner scanner = new Scanner(file);
        boolean found = false;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if(line.equals(_practiseList.get(_currentIndex).getVersionName())) {
                found=true;
            }
        }
        if (!found) {
            fw.write(_practiseList.get(_currentIndex).getShortName() + "\r\n");
        }
        fw.close();
    }

}
