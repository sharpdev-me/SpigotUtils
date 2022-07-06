package me.sharpdev.pluginutils.database;

public class SerializationException extends RuntimeException {
    private final Class<?> clazz;
    public SerializationException(Class<?> clazz, Throwable cause) {
        super("there was an error (de)serializing " + clazz.getName(), cause);
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }
}
