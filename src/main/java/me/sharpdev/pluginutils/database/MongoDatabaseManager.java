package me.sharpdev.pluginutils.database;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDatabaseManager {
    public final MongoClient client;
    public final MongoDatabase database;

    public MongoDatabaseManager(String uri, String database) {
        client = MongoClients.create(uri);

        this.database = client.getDatabase(database);
    }
}
