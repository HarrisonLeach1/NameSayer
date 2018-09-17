package app;

public class Name {
    private String _shortName, _versionName, _fileName;

    public Name(String fileName) {
        _fileName = fileName;
        _versionName = fileName;
        _shortName = parseShortName(fileName);
    }

    /**
     * Returns the name of the recording excluding the creation date, time and file extension.
     * @param fileName
     */
    private String parseShortName(String fileName) {
        String dateTimeRemoved = fileName.substring(fileName.lastIndexOf('_') + 1);
        return dateTimeRemoved.split("\\.")[0];
    }

    public String getShortName() {
        return _shortName;
    }

    public String getVersionName() {
        return _versionName;
    }

    public String getFileName() {
        return _fileName;
    }
}
