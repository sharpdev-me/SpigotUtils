package me.sharpdev.pluginutils.settings;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import me.sharpdev.pluginutils.database.DocumentSerializer;
import me.sharpdev.pluginutils.database.MongoDatabaseManager;
import org.bson.Document;

import java.util.HashMap;

public final class SettingsManager {
    private final MongoDatabaseManager databaseManager;

    private final HashMap<String, Setting> settings = new HashMap<>();

    public SettingsManager(MongoDatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public Setting getSetting(String key) {
        if(settings.containsKey(key)) return settings.get(key);
        Setting setting = loadSettingFromDatabase(key);
        if(setting != null) settings.put(key, setting);
        return setting;
    }

    public Setting getSetting(String key, Object defaultValue) {
        Setting setting = getSetting(key);
        if(setting != null) return setting;
        setting = new Setting(key, defaultValue);

        settings.put(key, setting);

        return setting;
    }

    public void saveSetting(String key) {
        if(!settings.containsKey(key)) return;
        MongoCollection<Document> collection = databaseManager.database.getCollection("settings");

        collection.replaceOne(Filters.eq("name", key), DocumentSerializer.serializeObject(settings.get(key)), new ReplaceOptions().upsert(true));
    }

    public void saveAllSettings() {
        for (String s : settings.keySet()) {
            saveSetting(s);
        }
    }

    public void clear() {
        settings.clear();
    }

    private Setting loadSettingFromDatabase(String key) {
        MongoCollection<Document> collection = databaseManager.database.getCollection("settings");

        for (Document foundSetting : collection.find(Filters.eq("name", key))) {
            return DocumentSerializer.deserializeObject(Setting.class, foundSetting);
        }

        return null;
    }
}
