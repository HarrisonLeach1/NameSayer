package app.model;

import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;

import java.io.File;
import java.util.List;

/**
 * An IDatabaseModel object represents the database in which database name
 * recordings and user recordings are to be stored and saved to.
 *
 * Having the IDataModel interface allows for dependency injection, such that
 * the users are dependent on an abstraction rather than a concrete
 * implementation. This allows for increased flexibility if the database
 * implementation is changed. For example, in the future a SQL database
 * could be used to handle storage and creation of Names.
 */
public interface IDatabaseModel {

    /**
     * Loads the database of recordings as a tree. The root is returned,
     * all user recordings are descendants of the root.
     *
     * All names are children of the root. If there is more than one version of
     * a name, those version will be children of that name.
     * @return the TreeItem root of the TreeView
     */
    TreeItem<NameVersion> loadDatabaseTree();

    /**
     * Loads the database of user recordings as a tree. The root is returned,
     * all user recordings are descendants of the root.
     *
     * All names are children of the root. If there is more than one version of
     * a name, those version will be children of that name.
     * @return the TreeItem root of the TreeView
     */
    TreeItem<NameVersion> loadUserDatabaseTree();

    /**
     * Loads the database of recordings as a list. Each name only appears in
     * the list once and is of good quality (if possible).
     * @return List<Name>
     */
    List<Name> loadDatabaseList();

    /**
     * Returns a Task which which returns a List containing a single
     * ConcatenatedName object corresponding to the given name.
     *
     * A Task object is returned to allow for progress tracking of the Task
     * progress and easy execution on a new Thread to avoid GUI concurrency
     * issues.
     * @param name
     * @return A Task which returns a List<ConcatenatedName>
     */
    Task loadSingleNameTask(String name);

    /**
     * Returns a Task which which returns a List containing a single
     * ConcatenatedName object corresponding to the names in the given
     * file.
     *
     * The provided file format should recognise that each line of the
     * file will be turned into a ConcatenatedName object.  A Task object
     * is returned to allow for progress tracking of the Task progress and
     * easy execution on a new Thread to avoid GUI concurrency issues.
     * @param playlistFile
     * @return
     */
    Task loadFileTask(File playlistFile);

    /**
     * Returns a displayable string which contains all missing names
     * for every ConcatenatedName in the given List.
     * @param list
     * @return A String containing all the missing names in the given list
     */
    String compileMissingNames(List<ConcatenatedName> list);

    /**
     * Saves the string representations of each ConcatenatedName object
     * in the given List to a text file with the given file name.
     *
     * The file format should be the same used in the loadFileTask
     * method such that a user can reuse and load this list in again.
     * @param list
     * @param fileName
     */
    void savePlaylist(List<ConcatenatedName> list, String fileName);

    /**
     * This sets the database directory to which this IDatabaseModel object
     * refers to. The recording files inside this directory must have names
     * that are compliant to the format specified by the concrete implementation
     * of this class.
     *
     * In future the parameter could be changed to other database types.
     * @param database
     */
    void setDatabase(File database);

    /**
     * Returns a displayable string representation of the name of the database
     * that this IDatabaseModel object refers to.
     * @return name of the database
     */
    String getDatabaseName();

    /**
     * Returns the number of valid unique names that have been found in the
     * database that is currently referred to. A valid name should be defined
     * by the concrete implementation of this class.
     * @return the number of valid names in the database
     */
    int getDatabaseNameCount();

    /**
     * Returns a List containing the displayable String representations of all
     * valid names in the database to which this object refers to.
     * @return
     */
    List<String> getNameStrings();

    /**
     * Deletes the recordings that have been temporarily stored for playback
     * by the user.
     */
    void deleteTempRecordings();
}
