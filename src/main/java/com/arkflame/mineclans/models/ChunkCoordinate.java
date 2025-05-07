package com.arkflame.mineclans.models;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class ChunkCoordinate {
    private final UUID factionId;
    private final int x;
    private final int z;
    private final String serverName;
    private final String worldName;
    private final Date claimDate;

    // New constructor with all fields
    public ChunkCoordinate(UUID factionId, int x, int z, String worldName, String serverName, Date claimDate) {
        this.factionId = factionId;
        this.x = x;
        this.z = z;
        this.serverName = serverName;
        this.worldName = worldName;
        this.claimDate = claimDate;
    }

    public UUID getFactionId() {
        return factionId;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public String getServerName() {
        return serverName;
    }

    public String getWorldName() {
        return worldName;
    }

    public Date getClaimDate() {
        return claimDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChunkCoordinate that = (ChunkCoordinate) o;
        return x == that.x && 
               z == that.z && 
               Objects.equals(factionId, that.factionId) &&
               Objects.equals(serverName, that.serverName) &&
               Objects.equals(worldName, that.worldName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(factionId, x, z, worldName, serverName);
    }
}