package app.model;

import javafx.concurrent.Task;
import javax.sound.sampled.*;

/**
 * A MicTestTask represents a Task object that can be attached to
 * some display to indicate the users microphone level.
 *
 * The Task should be executed on a new Thread to avoid GUI
 * unresponsiveness issues.
 */
public class MicTestTask extends Task {
    private static final AudioFormat AUDIO_FORMAT = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);
    private static final DataLine.Info INFO = new DataLine.Info(TargetDataLine.class, AUDIO_FORMAT);
    private static final double MAX_PROGRESS = 100;
    private static final int BUFFER_DIVISION = 5;
    private TargetDataLine _targetLine;


    public MicTestTask() {
        try {
            _targetLine = (TargetDataLine) AudioSystem.getLine(INFO);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * Updates the progress of this Task object based on the volume
     * level of the input received from the user's mic.
     * @return
     */
    @Override
    protected Object call() {
        try {
            // initialise mic input
            _targetLine.open();
            _targetLine.start();

            byte[] data = new byte[_targetLine.getBufferSize() / BUFFER_DIVISION];

            // while this task is alive keep reading the users mic level and updating this Task appropriately
            while (true) {
                _targetLine.read(data, 0, data.length);
                int level = calculateRMSLevel(data);
                updateProgress(level,MAX_PROGRESS);
            }
        } catch (LineUnavailableException lue) {
            lue.printStackTrace();
        }
        return null;
    }

    /**
     * The task is cancelled, closing the line to the users microphone
     * input and ending volume updates.
     */
    @Override
    protected void cancelled() {
        _targetLine.stop();
        _targetLine.close();
        super.cancelled();
    }

    /**
     * Updates the progress of this task which actually represents the volume
     * level of the users input to the mic.
     * @param workDone
     * @param max
     */
    @Override
    protected void updateProgress(double workDone, double max) {
        super.updateProgress(workDone, max);
    }

    /**
     * Calculates the microphone input RMS Level and turns it into an integer
     * that can be used to display mic volume information to the user.
     * Reference: https://stackoverflow.com/questions/3899585/microphone-level-in-java
     * 
     */
    private int calculateRMSLevel(byte[] audioData) {
        long lSum = 0;
        for (int i = 0; i < audioData.length; i++)
            lSum = lSum + audioData[i];

        double dAvg = lSum / audioData.length;
        double sumMeanSquare = 0d;

        for (int j = 0; j < audioData.length; j++)
            sumMeanSquare += Math.pow(audioData[j] - dAvg, 2d);

        double averageMeanSquare = sumMeanSquare / audioData.length;

        return (int) (Math.pow(averageMeanSquare, 0.5d) + 0.5);
    }
}