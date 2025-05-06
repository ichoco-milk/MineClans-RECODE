package com.arkflame.mineclans.models;

import java.util.Objects;
import java.util.UUID;

public class ChunkCoordinate {
    private final UUID factionId;
    private final int x;
    private final int z;

    public ChunkCoordinate(UUID factionId, int x, int z) {
        this.factionId = factionId;
        this.x = x;
        this.z = z;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChunkCoordinate that = (ChunkCoordinate) o;
        return x == that.x && z == that.z && Objects.equals(factionId, that.factionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(factionId, x, z);
    }
}