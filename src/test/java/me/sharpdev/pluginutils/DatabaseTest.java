package me.sharpdev.pluginutils;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import me.sharpdev.pluginutils.database.MongoDatabaseManager;
import me.sharpdev.pluginutils.itemmanager.ItemData;
import me.sharpdev.pluginutils.itemmanager.ItemManager;
import me.sharpdev.pluginutils.itemmanager.ManagedItem;
import me.sharpdev.pluginutils.playerdata.PlayerData;
import me.sharpdev.pluginutils.playerdata.PlayerDataManager;
import me.sharpdev.pluginutils.settings.Setting;
import me.sharpdev.pluginutils.settings.SettingsManager;
import org.bson.Document;
import org.bukkit.NamespacedKey;
import org.junit.jupiter.api.*;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static me.sharpdev.pluginutils.TestDataTypes.*;
import static org.mockito.Mockito.*;

public class DatabaseTest {
    @BeforeEach
    public void setTestData() {
        ItemManager.setItemDataProvider(TestItemData.class);
        PlayerDataManager.setPlayerDataProvider(TestPlayerData.class);
    }

    @AfterEach
    public void unsetTestData() {
        ItemManager.setItemDataProvider(ItemData.class);
        PlayerDataManager.setPlayerDataProvider(PlayerData.class);
    }

    @BeforeAll
    public static void createDatabaseConnection() {
        PluginUtils.setDefaultDatabaseManager(new MongoDatabaseManager(System.getenv("MONGO_URI"), "pluginutils_test"));
    }

    @AfterAll
    public static void removeDatabaseConnection() {
        MongoDatabaseManager databaseManager = PluginUtils.getDefaultDatabaseManager();
        // clear all test data from database
        for (String listCollectionName : databaseManager.database.listCollectionNames()) {
            MongoCollection<Document> collection = databaseManager.database.getCollection(listCollectionName);
            collection.deleteMany(Filters.empty());
        }
        databaseManager.client.close();
        PluginUtils.setDefaultDatabaseManager(null);
    }

    @Test
    public void storeItem() {
        ItemManager itemManager = new ItemManager();

        NamespacedKey key = new NamespacedKey(generateRandomAlphanumericString(12).toLowerCase(), generateRandomAlphanumericString(12).toLowerCase());
        ManagedItem managedItem = itemManager.createItem(key, null, TestItemData.randomData());

        // clear out the cache of items
        itemManager.saveAllItems();
        itemManager.clear();
        itemManager.loadAllItems();

        assertEquals(managedItem, itemManager.getItem(key));
    }

    @Test
    public void storePlayerData() {
        PlayerDataManager playerDataManager = new PlayerDataManager(PluginUtils.getDefaultDatabaseManager());

        UUID playerUUID = UUID.randomUUID();
        PlayerData playerData = playerDataManager.getPlayerData(playerUUID);

        // make sure that the manager is properly setting the data class
        assertTrue(playerData instanceof TestPlayerData);

        playerDataManager.saveAllPlayers();
        playerDataManager.clear();

        PlayerData result = playerDataManager.getPlayerData(playerUUID);

        // make sure that the manager is properly setting the data class
        assertTrue(result instanceof TestPlayerData);

        assertEquals(playerData, result);
    }

    @Test
    public void storeSetting() {
        SettingsManager settingsManager = new SettingsManager(PluginUtils.getDefaultDatabaseManager());

        String key = generateRandomAlphanumericString(17);

        Setting setting = settingsManager.getSetting(key, generateRandomString(13));

        settingsManager.saveSetting(setting.getName());
        settingsManager.clear();

        Setting result = settingsManager.getSetting(key);

        assertEquals(setting, result);
    }
}
