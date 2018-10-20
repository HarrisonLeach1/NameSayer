package app.controllers;

import app.model.IDatabaseModel;
import impl.org.controlsfx.autocompletion.SuggestionProvider;
import javafx.scene.control.TextField;
import org.controlsfx.control.textfield.TextFields;
import java.util.ArrayList;
import java.util.List;

/**
 * A SearchField represents a functionality wrapper for a TextField class which
 * allows TextFields to have auto-suggestions of database names when the user is
 * typing.
 *
 * The suggestions given are dependent on the database provided upon construction.
 * Note that this is not an FXML component itself, this is not necessary as only
 * simple functionality is introduced an the extra coupling is uneccessary.
 */
public class SearchField {

    private final IDatabaseModel _databaseModel;
    private TextField _textField;
    private final SuggestionProvider<String> _suggestionProvider;


    public SearchField(TextField textField, IDatabaseModel databaseModel) {
        _textField = textField;
        _databaseModel = databaseModel;

        // initially suggestions only consist of all the database names
        _suggestionProvider = SuggestionProvider.create(_databaseModel.getNameStrings());

        // bind autocompletion and set width
        TextFields.bindAutoCompletion(_textField, _suggestionProvider).setPrefWidth(_textField.getPrefWidth());

        initialiseAutoCompletion();
    }

    /**
     * Initialises the SearchField to allow for autocompletion when the user types
     * a name.
     *
     * Initially the autocomplete contains all database names, then changes based
     * on the users search. When the user adds a space or hyphen, all database names
     * are suggested again. When the user deletes a space or hyphen, all database
     * names are suggested again for the previous name.
     */
    private void initialiseAutoCompletion() {
        _textField.textProperty().addListener((observable, oldValue, newValue) -> {

            // initially autocomplete contains all names
            if(oldValue.isEmpty()) {
                _suggestionProvider.clearSuggestions();
                _suggestionProvider.addPossibleSuggestions(_databaseModel.getNameStrings());
            }

            // if the user adds a space or a hyphen
            if ((newValue.length()) > oldValue.length() && (newValue.endsWith(" ") || newValue.endsWith("-"))
                    // or if the user deletes a space or a hyphen
                    || newValue.length() < oldValue.length() && (oldValue.endsWith(" ") || oldValue.endsWith("-"))) {

                List<String> autoCompleteList = new ArrayList<>();

               int index = getLastIndex(newValue);

                // append all database names as suggestions to the new name
                for(String name : _databaseModel.getNameStrings()) {
                    autoCompleteList.add(newValue.substring(0, index + 1) + name);
                }

                // update suggestions
                _suggestionProvider.clearSuggestions();
                _suggestionProvider.addPossibleSuggestions(autoCompleteList);
            }
        });
    }

    /**
     * Given a string, returns the greatest index between the last hyphen and space
     * in the character. Returns -1 if neither are found.
     * @param string
     */
    private int getLastIndex(String string) {
        // find the last index of a space and hyphen
        int spaceIndex = string.lastIndexOf(" ");
        int hyphenIndex = string.lastIndexOf("-");

        // return the last of these two (furthest right / greatest index)
        return spaceIndex > hyphenIndex ? spaceIndex : hyphenIndex;
    }
}
