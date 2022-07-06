package me.sharpdev.pluginutils;

import me.sharpdev.pluginutils.database.MongoDatabaseManager;
import org.bukkit.plugin.Plugin;

public final class PluginUtils {
    private static MongoDatabaseManager defaultDatabaseManager;
    private static Plugin defaultPlugin;

    public static void setDefaultDatabaseManager(MongoDatabaseManager defaultDatabaseManager) {
        PluginUtils.defaultDatabaseManager = defaultDatabaseManager;
    }

    public static MongoDatabaseManager getDefaultDatabaseManager() {
        return defaultDatabaseManager;
    }

    public static void setDefaultPlugin(Plugin defaultPlugin) {
        PluginUtils.defaultPlugin = defaultPlugin;
    }

    public static Plugin getDefaultPlugin() {
        return defaultPlugin;
    }
}
