package com.innowise.migration.model;

/**
 * Represents metadata for a single database migration script.
 * Holds the script file path and version information.
 * Used to identify and track individual scripts during the migration process.
 */

public class DatabaseChangeLogScript {
    private String file;
    private String version;

    public DatabaseChangeLogScript(String file, String version) {
        this.file = file;
        this.version = version;
    }
    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
