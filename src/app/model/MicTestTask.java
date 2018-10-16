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
    private TargetDataLine targetLine;


    public MicTestTask() {
        try {
            targetLine = (TargetDataLine) AudioSystem.getLine(INFO);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Object call() {
        try {
            //Microphone
            targetLine.open();
            targetLine.start();

            byte[] data = new byte[targetLine.getBufferSize() / 5];

            while (true) {
                targetLine.read(data, 0, data.length);
                int level = calculateRMSLevel(data);
                updateProgress(level,100);
            }
        } catch (LineUnavailableException lue) {
            lue.printStackTrace();
        }
        return null;
    }

    @Override
    protected void cancelled() {
        targetLine.stop();
        targetLine.close();
        super.cancelled();
    }


    @Override
    protected void updateProgress(double workDone, double max) {
        super.updateProgress(workDone, max);
    }

    /**
     * Calculates the microphone input and turns it into an integer
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