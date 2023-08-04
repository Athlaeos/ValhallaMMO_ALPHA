package me.athlaeos.valhallammo.network;

import org.bukkit.entity.Player;

public interface IPacketHandler {
    default void readAfter(Player player, Object packet) {}
    default void writeAfter(Player player, Object packet) {}
    default boolean readBefore(Player player, Object packet) { return true; }
    default boolean writeBefore(Player player, Object packet) { return true; }
}
