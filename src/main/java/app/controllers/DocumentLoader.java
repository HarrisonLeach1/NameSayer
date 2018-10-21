package app.controllers;

import java.io.File;
import java.io.IOException;

/**
 * A DocumentLoader represents an object that loads, opens and displays documents to the
 * user.
 *
 * The document to be opened should be supplied to the object upon construction. When
 * the object is supplied with invalid document file names, error messages are displayed
 * to the user.
 */
public class DocumentLoader {
    private static final String OPEN_ERROR_MSG = "ERROR: Could not open ";
    private static final String FIND_ERROR_MSG = "ERROR: Could not find ";

    private final String _documentName;

    public DocumentLoader(String documentName) {
        _documentName = documentName;
    }

    /**
     * Opens a the document that was given to this DocumentLoader upon construction. If
     * the document does not exist, an error message is displayed.
     */
    public void loadDocument() {
            File documentFile = new File(_documentName);
            if (documentFile.exists()) {
                runOpenProcess();
            } else{
                new SceneLoader().loadErrorMessage(FIND_ERROR_MSG + _documentName);
            }
    }

    /**
     * An implementation of how the DocumentLoader opens a document to the user. If the
     * document cannot open, an error message is displayed to the user.
     */
    private void runOpenProcess() {
        try {
            String cmd = "xdg-open " + _documentName;

            ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
            builder.start();
        } catch (IOException e) {
            new SceneLoader().loadErrorMessage(OPEN_ERROR_MSG + _documentName);
        }
    }
}

