package me.sharpdev.pluginutils.playerdata;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReplaceOptions;
import me.sharpdev.pluginutils.database.DatabaseException;
import me.sharpdev.pluginutils.database.DocumentSerializer;
import me.sharpdev.pluginutils.database.MongoDatabaseManager;
import me.sharpdev.pluginutils.util.PlayerCache;
import org.bson.Document;
import org.bukkit.OfflinePlayer;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public final class PlayerDataManager {
    private final MongoDatabaseManager databaseManager;
    private final PlayerCache<PlayerData> dataCache = new PlayerCache<>();

    private static Class<? extends PlayerData> playerDataProvider = PlayerData.class;

    public PlayerDataManager(MongoDatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void savePlayerData(UUID uuid) {
        PlayerData playerData = dataCache.get(uuid);

        databaseManager.database.getCollection("playerdata").replaceOne(Filters.eq("uuid", DocumentSerializer.serializeObject(uuid)),
                DocumentSerializer.serializeObject(playerData), new ReplaceOptions().upsert(true));
    }

    public void loadPlayerData(UUID uuid) {
        for(Document document : databaseManager.database.getCollection("playerdata").find(Filters.eq("uuid", DocumentSerializer.serializeObject(uuid)))) {
            dataCache.put(uuid, DocumentSerializer.deserializeObject(playerDataProvider, document));
        }
    }

    public void saveAllPlayers() {
        for (UUID uuid : dataCache.keySet()) {
            savePlayerData(uuid);
        }
    }

    public PlayerData getPlayerData(UUID uuid) {
        if(dataCache.containsKey(uuid)) return dataCache.get(uuid);
        loadPlayerData(uuid);
        if(!dataCache.containsKey(uuid)) {
            try {
                PlayerData playerData = getPlayerDataProvider().getConstructor().newInstance();
                dataCache.put(uuid, playerData);
                return playerData;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        return dataCache.get(uuid);
    }

    public void removePlayerData(UUID uuid) {
        dataCache.remove(uuid);
    }

    public PlayerData getPlayerData(OfflinePlayer player) {
        return getPlayerData(player.getUniqueId());
    }

    public void removePlayerData(OfflinePlayer player) {
        removePlayerData(player.getUniqueId());
    }

    public void clear() {
        dataCache.clear();
    }

    public static Class<? extends PlayerData> getPlayerDataProvider() {
        return playerDataProvider;
    }

    public static void setPlayerDataProvider(Class<? extends PlayerData> playerDataProvider) {
        try {
            playerDataProvider.getDeclaredField("uuid");
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("playerDataProvider must have a declared UUID field", e);
        }
        PlayerDataManager.playerDataProvider = playerDataProvider;
    }
}
