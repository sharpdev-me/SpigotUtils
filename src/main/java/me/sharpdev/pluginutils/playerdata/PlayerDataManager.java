package me.sharpdev.pluginutils.playerdata;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import me.sharpdev.pluginutils.database.DatabaseException;
import me.sharpdev.pluginutils.database.DocumentSerializer;
import me.sharpdev.pluginutils.database.MongoDatabaseManager;
import me.sharpdev.pluginutils.util.PlayerCache;
import org.bson.Document;

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

        if(databaseManager.database.getCollection("playerdata").findOneAndReplace(Filters.eq("uuid", DocumentSerializer.serializeObject(player)),
                DocumentSerializer.serializeObject(playerData), new FindOneAndReplaceOptions().upsert(true)) == null)throw new DatabaseException("error saving PlayerData for " + player.toString());
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
