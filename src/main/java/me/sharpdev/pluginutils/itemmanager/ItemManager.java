package me.sharpdev.pluginutils.itemmanager;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import me.sharpdev.pluginutils.PluginUtils;
import me.sharpdev.pluginutils.database.DocumentSerializer;
import me.sharpdev.pluginutils.database.MongoDatabaseManager;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public final class ItemManager {
    private final MongoDatabaseManager databaseManager;

    private static Class<? extends ItemData> itemDataProvider = ItemData.class;

    private final HashMap<NamespacedKey, ManagedItem> registeredItems = new HashMap<>();

    public ItemManager(MongoDatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }
    public ItemManager() {
        this(PluginUtils.getDefaultDatabaseManager());
    }

    public void loadAllItems() {
        MongoCollection<Document> collection = databaseManager.database.getCollection("items");
        for (Document document : collection.find()) {
            ManagedItem managedItem = DocumentSerializer.deserializeObject(ManagedItem.class, document);
            if(managedItem == null) continue;
            registeredItems.put(managedItem.getItemID(), managedItem);
        }
    }

    public void saveItem(NamespacedKey itemID) {
        MongoCollection<Document> collection = databaseManager.database.getCollection("items");
        if(!registeredItems.containsKey(itemID)) return;

        ManagedItem managedItem = registeredItems.get(itemID);

        collection.replaceOne(findFilter(itemID), DocumentSerializer.serializeObject(managedItem), new ReplaceOptions().upsert(true));
    }

    public void saveItem(ManagedItem item) {
        saveItem(item.getItemID());
    }

    public void saveAllItems() {
        for (NamespacedKey namespacedKey : registeredItems.keySet()) {
            saveItem(namespacedKey);
        }
    }

    public ManagedItem createItem(NamespacedKey itemID, ItemStack itemStack, ItemData itemData) {
        if(registeredItems.containsKey(itemID)) throw new ItemExistsException(itemID);
        ManagedItem item = new ManagedItem(itemID, itemStack, itemData);

        registeredItems.put(itemID, item);
        saveItem(itemID);

        return item;
    }

    public ManagedItem getItem(NamespacedKey itemID) {
        return registeredItems.get(itemID);
    }

    public HashMap<NamespacedKey, ManagedItem> getRegisteredItems() {
        return registeredItems;
    }

    public void clear() {
        registeredItems.clear();
    }

    public static void setItemDataProvider(Class<? extends ItemData> provider) {
        itemDataProvider = provider;
    }

    public static Class<? extends ItemData> getItemDataProvider() {
        return itemDataProvider;
    }

    private Bson findFilter(NamespacedKey itemID) {
        return Filters.eq("itemID", DocumentSerializer.serializeObject(itemID));
    }
}
