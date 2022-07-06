package me.sharpdev.pluginutils;

import com.google.common.base.Objects;
import me.sharpdev.pluginutils.database.DocumentSerializer;
import me.sharpdev.pluginutils.itemmanager.ItemData;
import me.sharpdev.pluginutils.itemmanager.ItemManager;
import me.sharpdev.pluginutils.itemmanager.ManagedItem;
import me.sharpdev.pluginutils.playerdata.PlayerData;
import me.sharpdev.pluginutils.playerdata.PlayerDataManager;
import me.sharpdev.pluginutils.settings.Setting;
import org.bson.Document;
import org.bukkit.NamespacedKey;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static me.sharpdev.pluginutils.TestDataTypes.*;
import static org.mockito.Mockito.*;

public class SerializationTest {
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

    @Test
    public void serializeItem() {
        // must use null for the ItemStack since we can't mock it without Bukkit
        ManagedItem testItem = new ManagedItem(new NamespacedKey("test", generateRandomAlphanumericString(12).toLowerCase()), null, TestItemData.randomData());

        Document serialized = DocumentSerializer.serializeObject(testItem);

        ManagedItem result = DocumentSerializer.deserializeObject(ManagedItem.class, serialized);

        assertEquals(testItem, result);
    }

    @Test
    public void serializePlayerData() {
        PlayerData playerData = TestPlayerData.randomData();

        Document serialized = DocumentSerializer.serializeObject(playerData);

        PlayerData result = DocumentSerializer.deserializeObject(PlayerDataManager.getPlayerDataProvider(), serialized);

        assertEquals(playerData, result);
    }

    @Test
    public void serializeSetting() {
        Setting mockSetting = new Setting(generateRandomAlphanumericString(36), generateRandomString(13));

        Document serialized = DocumentSerializer.serializeObject(mockSetting);

        Setting result = DocumentSerializer.deserializeObject(Setting.class, serialized);

        assertEquals(mockSetting, result);
    }
}
