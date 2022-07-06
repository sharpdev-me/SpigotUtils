package me.sharpdev.pluginutils.playerdata;

import me.sharpdev.pluginutils.database.UpdateSerializable;

import java.util.UUID;

public class PlayerData implements UpdateSerializable {
    public UUID uuid = UUID.randomUUID();
}
