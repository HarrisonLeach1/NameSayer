package app.controllers;

import app.model.ConcatenatedName;
import app.model.DataModel;
import impl.org.controlsfx.autocompletion.SuggestionProvider;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import org.controlsfx.control.textfield.TextFields;
import java.util.ArrayList;
import java.util.List;

/**
 * A HighlightedList represents a functionality wrapper for a ListView class which
 * allows ListViews to highlight names that have missing names in them.
 *
 * Note that this is not an FXML component itself, this is not necessary as only
 * simple functionality is introduced an the extra coupling is uneccessary.
 */
public class HighlightedList {
    private static final String MISSING_MSG = "Missing Name(s): ";
    private static final String FOUND_MSG = "All Names Found!";

    private final ListView<ConcatenatedName> _listView;

    public HighlightedList(ListView<ConcatenatedName> listView) {
        _listView = listView;

        initialiseHighlighting();
    }

    /**
     * Initialise the ListView's cell factory such that ConcatenatedNames that
     * have missing names are highlighted and have a useful tooltip indicating
     * which names are missing.
     */
    private void initialiseHighlighting() {
        _listView.setCellFactory(lv -> new ListCell<ConcatenatedName>() {
            @Override
            protected void updateItem(ConcatenatedName c, boolean empty) {
                super.updateItem(c, empty);
                // if empty, ignore
                if (empty) {
                    setText(null);
                    setStyle("");

                } else {
                    setText(c.toString());

                    // If the ConcatenatedName object contains missing names, update the cell accordingly.
                    if (!c.getMissingNames().equals("")) {
                        setStyle("-fx-background-color: rgba(255,0,0,0.5)");
                        setTooltip( new Tooltip( MISSING_MSG + c.getMissingNames()));
                    } else {
                        setStyle("");
                        setTooltip( new Tooltip(FOUND_MSG));
                    }
                }
            }
        });
    }
}
