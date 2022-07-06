package me.sharpdev.pluginutils.database;

public class DatabaseException extends RuntimeException {
    public DatabaseException() { super(); }
    public DatabaseException(String message) {
        super(message);
    }
}
