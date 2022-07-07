package me.sharpdev.pluginutils.itemmanager;

import me.sharpdev.pluginutils.database.DocumentSerializer;
import org.bson.Document;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

import static me.sharpdev.pluginutils.database.DocumentSerializer.Serializer;

public final class ManagedItem {
    public static final Serializer<ManagedItem> ITEM_SERIALIZER = new Serializer<ManagedItem>() {
        @Override
        public Document serialize(ManagedItem object) {
            Document document = new Document();

            document.append("itemID", DocumentSerializer.serializeObject(object.itemID));
            document.append("itemStack", DocumentSerializer.serializeObject(object.itemStack));
            document.append("itemData", DocumentSerializer.serializeObject(object.itemData));

            return document;
        }

        @Override
        public ManagedItem deserialize(Document document) {
            ManagedItem item = new ManagedItem();

            item.itemID = DocumentSerializer.deserializeObject(NamespacedKey.class, (Document) document.get("itemID"));
            item.itemStack = DocumentSerializer.deserializeObject(ItemStack.class, (Document) document.get("itemStack"));
            item.itemData = DocumentSerializer.deserializeObject(ItemManager.getItemDataProvider(), (Document) document.get("itemData"));

            return item;
        }
    };

    static {
        DocumentSerializer.addSerializer(ManagedItem.class, ITEM_SERIALIZER);
    }

    private NamespacedKey itemID;
    private ItemStack itemStack;
    private ItemData itemData;

    private ManagedItem() { }

    public ManagedItem(NamespacedKey itemID, ItemStack itemStack, ItemData itemData) {
        this.itemID = itemID;
        this.itemStack = itemStack;
        this.itemData = itemData;
    }

    public ItemStack give(Inventory inventory) {
        ItemStack clone = itemStack.clone();

        inventory.addItem(clone);

        return clone;
    }

    public ItemStack give(Player player) {
        return give(player.getInventory());
    }

    public NamespacedKey getItemID() {
        return itemID;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public ItemData getItemData() {
        return itemData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ManagedItem that = (ManagedItem) o;

        if (!Objects.equals(itemID, that.itemID)) return false;
        if (!Objects.equals(itemStack, that.itemStack)) return false;
        return Objects.equals(itemData, that.itemData);
    }

    @Override
    public int hashCode() {
        int result = itemID != null ? itemID.hashCode() : 0;
        result = 31 * result + (itemStack != null ? itemStack.hashCode() : 0);
        result = 31 * result + (itemData != null ? itemData.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ManagedItem{" +
                "itemID=" + itemID +
                ", itemStack=" + itemStack +
                ", itemData=" + itemData +
                '}';
    }
}
