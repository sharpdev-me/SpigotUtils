package me.sharpdev.pluginutils.itemmanager;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import me.sharpdev.pluginutils.PluginUtils;
import me.sharpdev.pluginutils.database.DocumentSerializer;
import me.sharpdev.pluginutils.database.MongoDatabaseManager;
import me.sharpdev.pluginutils.nbt.KeyDataType;
import me.sharpdev.pluginutils.util.ItemMetaRunnable;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.HashMap;

public final class ItemManager {
    private final MongoDatabaseManager databaseManager;

    private static Class<? extends ItemData> itemDataProvider = ItemData.class;

    private final HashMap<NamespacedKey, ManagedItem> registeredItems = new HashMap<>();

    private static final NamespacedKey ID_KEY = new NamespacedKey("sharpdev", "ManagedItem_ID");

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

        if(itemStack != null) ItemMetaRunnable.doItemMeta(itemStack, (meta) -> {
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(ID_KEY, new KeyDataType(), itemID);
        });

        registeredItems.put(itemID, item);
        saveItem(itemID);

        return item;
    }

    public ManagedItem getItem(NamespacedKey itemID) {
        return registeredItems.get(itemID);
    }

    public ManagedItem getItem(ItemStack itemStack) {
        return getItem(getItemID(itemStack));
    }

    public boolean isManagedItem(ItemStack itemStack) {
        return isManagedItem(getItemID(itemStack));
    }

    public boolean isManagedItem(NamespacedKey itemID) {
        return registeredItems.containsKey(itemID);
    }

    public NamespacedKey getItemID(ItemStack itemStack) {
        return itemStack.getItemMeta().getPersistentDataContainer().get(ID_KEY, new KeyDataType());
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
