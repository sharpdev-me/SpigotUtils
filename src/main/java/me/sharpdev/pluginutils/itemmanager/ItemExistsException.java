package me.sharpdev.pluginutils.itemmanager;

import org.bukkit.NamespacedKey;

public class ItemExistsException extends RuntimeException {
    public ItemExistsException(NamespacedKey key) {
        super("item " + key.toString() + " already exists");
    }
}
