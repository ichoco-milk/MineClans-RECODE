package com.arkflame.mineclans.api.data;



import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;


public record LocationData(
    String worldName,
    double x,
    double y,
    double z,
    float pitch,
    float yaw,
    String serverName
) {

    public LocationData(Location location, String serverName) {
        this(location.getWorld().getName(), location.getX(), location.getY(), location.getZ(), location.getPitch(),
            location.getYaw(), serverName);
    }

    /**
     * Converts this LocationData to a Bukkit Location, if possible.
     * Returns null if the world does not exist on the current server.
     */
    public Location getLocation() {
        World world = Bukkit.getWorld(worldName);
        return (world != null) ? new Location(world, x, y, z, yaw, pitch) : null;
    }

    public int getBlockX() {
        return (int) Math.floor(x);
    }

    public int getBlockY() {
        return (int) Math.floor(y);
    }

    public int getBlockZ() {
        return (int) Math.floor(z);
    }
}
