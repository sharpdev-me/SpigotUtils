package me.sharpdev.pluginutils.util;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerCache<K> extends HashMap<UUID, K> {
    public K get(UUID key) {
        return super.get(key);
    }

    public K get(Player key) {
        return get(key.getUniqueId());
    }

    public boolean containsKey(UUID key) {
        return super.containsKey(key);
    }

    public boolean containsKey(Player key) {
        return containsKey(key.getUniqueId());
    }

    public K remove(UUID key) {
        return super.remove(key);
    }

    public K remove(Player key) {
        return remove(key.getUniqueId());
    }
}
