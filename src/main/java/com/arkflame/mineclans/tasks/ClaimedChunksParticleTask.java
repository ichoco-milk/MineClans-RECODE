package com.arkflame.mineclans.tasks;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.MineClansAPI;
import com.arkflame.mineclans.models.ChunkCoordinate;
import com.arkflame.mineclans.utils.ParticleUtil;

public class ClaimedChunksParticleTask extends BukkitRunnable {
    private final MineClansAPI api;
    private final String particleType;
    private final int particleCount;

    public ClaimedChunksParticleTask(String particleType, int particleCount) {
        this.api = MineClans.getInstance().getAPI();
        this.particleType = particleType;
        this.particleCount = particleCount;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
        if (player == null || !player.isOnline()) {
            this.cancel();
            return;
        }

        // Get player's current chunk and location
        Location playerLoc = player.getLocation();
        Chunk centerChunk = playerLoc.getChunk();
        int centerX = centerChunk.getX();
        int centerZ = centerChunk.getZ();
        double y = playerLoc.getY() + 1; // Player's Y position +1

        // Check all 9 chunks in 3x3 grid around player
        for (int xOffset = -1; xOffset <= 1; xOffset++) {
            for (int zOffset = -1; zOffset <= 1; zOffset++) {
                int chunkX = centerX + xOffset;
                int chunkZ = centerZ + zOffset;

                // Check if chunk is claimed
                if (api.getClaimedChunks().isChunkClaimed(chunkX, chunkZ)) {
                    ChunkCoordinate claim = api.getClaimedChunks().getChunkAt(chunkX, chunkZ);
                    if (claim != null) {
                        // TODO: RELATION BASED PARTICLES!!!!
                        String factionParticle = this.particleType;
                        
                        // Show particles around chunk borders
                        for (int i = -1; i <= 1; i++) {
                            showChunkBorders(player, chunkX, chunkZ, y + i, factionParticle);
                        }
                    }
                }
            }
        }
    }
    }

    private void showChunkBorders(Player player, int chunkX, int chunkZ, double y, String particleType) {
        if (particleType == null || particleType.isEmpty()) return;

        Location worldLoc = player.getWorld().getBlockAt(chunkX << 4, (int)y, chunkZ << 4).getLocation();
        
        // Calculate chunk corners
        double minX = worldLoc.getX();
        double maxX = minX + 16;
        double minZ = worldLoc.getZ();
        double maxZ = minZ + 16;

        // Generate particles along the borders
        int pointsPerSide = 8; // Particles per side of the chunk
        double step = 16.0 / pointsPerSide;

        // North edge (minZ)
        for (int i = 0; i <= pointsPerSide; i++) {
            Location loc = new Location(worldLoc.getWorld(), minX + (step * i), y, minZ);
            ParticleUtil.spawnParticle(player, loc, particleType, particleCount, 0, 0, 0, 0);
        }

        // East edge (maxX)
        for (int i = 0; i <= pointsPerSide; i++) {
            Location loc = new Location(worldLoc.getWorld(), maxX, y, minZ + (step * i));
            ParticleUtil.spawnParticle(player, loc, particleType, particleCount, 0, 0, 0, 0);
        }

        // South edge (maxZ)
        for (int i = 0; i <= pointsPerSide; i++) {
            Location loc = new Location(worldLoc.getWorld(), maxX - (step * i), y, maxZ);
            ParticleUtil.spawnParticle(player, loc, particleType, particleCount, 0, 0, 0, 0);
        }

        // West edge (minX)
        for (int i = 0; i <= pointsPerSide; i++) {
            Location loc = new Location(worldLoc.getWorld(), minX, y, maxZ - (step * i));
            ParticleUtil.spawnParticle(player, loc, particleType, particleCount, 0, 0, 0, 0);
        }
    }

    public static void start(String particleType, int particleCount, long interval) {
        new ClaimedChunksParticleTask(particleType, particleCount)
            .runTaskTimerAsynchronously(MineClans.getInstance(), 0L, interval);
    }
}