package me.sharpdev.pluginutils.util;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public interface ItemMetaRunnable {
    void run(ItemMeta itemMeta);

    static void doItemMeta(ItemStack itemStack, ItemMetaRunnable runnable) {
        ItemMeta meta = itemStack.getItemMeta();
        runnable.run(meta);
        itemStack.setItemMeta(meta);
    }
}
