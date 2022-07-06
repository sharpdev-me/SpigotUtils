package me.sharpdev.pluginutils.playerdata;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReplaceOptions;
import me.sharpdev.pluginutils.database.DatabaseException;
import me.sharpdev.pluginutils.database.DocumentSerializer;
import me.sharpdev.pluginutils.database.MongoDatabaseManager;
import me.sharpdev.pluginutils.util.PlayerCache;
import org.bson.Document;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public final class PlayerDataManager {
    private final MongoDatabaseManager databaseManager;
    private final PlayerCache<PlayerData> dataCache = new PlayerCache<>();

    private static Class<? extends PlayerData> playerDataProvider = PlayerData.class;

    public PlayerDataManager(MongoDatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void savePlayerData(UUID player) {
        PlayerData playerData = dataCache.get(player);

        databaseManager.database.getCollection("playerdata").replaceOne(Filters.eq("uuid", DocumentSerializer.serializeObject(player)),
                DocumentSerializer.serializeObject(playerData), new ReplaceOptions().upsert(true));
    }

    public void loadPlayerData(UUID player) {
        for(Document document : databaseManager.database.getCollection("playerdata").find(Filters.eq("uuid", DocumentSerializer.serializeObject(player)))) {
            dataCache.put(player, DocumentSerializer.deserializeObject(playerDataProvider, document));
        }
    }

    public void saveAllPlayers() {
        for (UUID uuid : dataCache.keySet()) {
            savePlayerData(uuid);
        }
    }

    public PlayerData getPlayerData(UUID player) {
        if(dataCache.containsKey(player)) return dataCache.get(player);
        loadPlayerData(player);
        if(!dataCache.containsKey(player)) {
            try {
                PlayerData playerData = getPlayerDataProvider().getConstructor().newInstance();
                dataCache.put(player, playerData);
                return playerData;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        return dataCache.get(player);
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
