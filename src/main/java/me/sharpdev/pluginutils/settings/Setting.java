package me.sharpdev.pluginutils.settings;

import com.google.common.base.Objects;
import me.sharpdev.pluginutils.database.DocumentSerializer;
import org.bson.Document;

public final class Setting {
    private final String name;
    private final Object defaultValue;
    private Object value;

    static {
        DocumentSerializer.addSerializer(Setting.class, new DocumentSerializer.Serializer<Setting>() {
            @Override
            public Document serialize(Setting object) {
                return new Document("name", object.name).append("defaultValue", object.defaultValue).append("value", object.value);
            }

            @Override
            public Setting deserialize(Document document) {
                Object defaultValue = document.get("defaultValue");

                return new Setting(document.getString("name"), defaultValue, document.getOrDefault("value", defaultValue));
            }
        });
    }

    public Setting(String name, Object defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;

        this.value = defaultValue;
    }

    public Setting(String name, Object defaultValue, Object value) {
        this(name, defaultValue);

        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public String getString() {
        return (String) value;
    }

    public Integer getInteger() {
        return (Integer) value;
    }

    public Long getLong() {
        return (Long) value;
    }

    public Boolean getBoolean() {
        return (Boolean) value;
    }

    public Short getShort() {
        return (Short) value;
    }

    public Byte getByte() {
        return (Byte) value;
    }

    public Character getCharacter() {
        return (Character) value;
    }

    public Double getDouble() {
        return (Double) value;
    }

    public Float getFloat() {
        return (Float) value;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Setting)) return false;
        Setting other = (Setting) obj;
        return name.equals(other.name) && value.equals(other.value) && defaultValue.equals(other.defaultValue);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, defaultValue, value);
    }
}
