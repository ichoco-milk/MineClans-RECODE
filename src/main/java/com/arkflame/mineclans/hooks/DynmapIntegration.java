package com.arkflame.mineclans.hooks;

import org.dynmap.DynmapAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.models.ChunkCoordinate;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.logging.Level;

public class DynmapIntegration implements Listener {
    private final static int MARKER_FILL_COLOR = 0x00FF00;
    private final static String MARKER_KEY = "mineclans.claims";
    private final JavaPlugin plugin;
    private DynmapAPI dynmap;
    private MarkerAPI markerAPI;
    private MarkerSet factionMarkers;

    public DynmapIntegration(JavaPlugin plugin) {
        this.plugin = plugin;
        setupDynmap();
    }

    private boolean isPluginEnabled() {
        return plugin.getServer().getPluginManager().isPluginEnabled("dynmap");
    }

    private void setupDynmap() {
        try {
            Plugin dynmapPlugin = plugin.getServer().getPluginManager().getPlugin("dynmap");
            if (dynmapPlugin != null && dynmapPlugin.isEnabled()) {
                dynmap = (DynmapAPI) dynmapPlugin;
                markerAPI = dynmap.getMarkerAPI();
                if (markerAPI != null) {
                    // Remove existing marker set if present
                    MarkerSet existing = markerAPI.getMarkerSet(MARKER_KEY);
                    if (existing != null)
                        existing.deleteMarkerSet();

                    factionMarkers = markerAPI.createMarkerSet(MARKER_KEY, "Faction Claims", null, false);
                    if (factionMarkers != null) {
                        plugin.getLogger().info("Dynmap integration enabled");
                        return;
                    }
                }
            }
            plugin.getLogger().warning("Dynmap integration failed during setup");
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Failed to initialize Dynmap integration", e);
        }
        // Reset if setup fails
        dynmap = null;
        markerAPI = null;
        factionMarkers = null;
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        if (event.getPlugin().getName().equalsIgnoreCase("dynmap")) {
            plugin.getLogger().info("Detected Dynmap enable, attempting setup...");
            setupDynmap();
        }
    }

    public void updateFactionClaim(int x, int z, String worldName, UUID factionId) {
        if (!isDynmapEnabled())
            return;

        String factionName = MineClans.getInstance().getAPI().getFactionName(factionId);

        String markerId = "faction_" + x + "_" + z + "_" + worldName;
        AreaMarker marker = factionMarkers.findAreaMarker(markerId);

        if (marker == null) {
            marker = createMarker(x, z, worldName, markerId, factionName);
        }

        if (marker != null) {
            marker.setFillStyle(0.5, MARKER_FILL_COLOR);
            marker.setLabel(factionName);
        }
    }

    public void updateFactionClaim(ChunkCoordinate chunk, UUID factionId) {
        updateFactionClaim(chunk.getX(), chunk.getZ(), chunk.getWorldName(), factionId);   
    }

    private AreaMarker createMarker(int chunkX, int chunkZ, String worldName, String markerId, String factionName) {
        double[] x = new double[4];
        double[] z = new double[4];
        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;

        x[0] = baseX;
        z[0] = baseZ;
        x[1] = baseX + 16;
        z[1] = baseZ;
        x[2] = baseX + 16;
        z[2] = baseZ + 16;
        x[3] = baseX;
        z[3] = baseZ + 16;

        AreaMarker marker = factionMarkers.createAreaMarker(
                markerId,
                factionName,
                false,
                worldName,
                x, z,
                false);
        marker.setCornerLocations(x, z);
        return marker;
    }

    public void removeFactionClaim(int x, int z, String worldName) {
        if (!isDynmapEnabled())
            return;

        String markerId = "faction_" + x + "_" + z + "_" + worldName;
        AreaMarker marker = factionMarkers.findAreaMarker(markerId);

        if (marker != null) {
            marker.deleteMarker();
        }
    }

    private boolean isDynmapEnabled() {
        if (dynmap == null || !isPluginEnabled() || markerAPI == null || factionMarkers == null) {
            return false;
        }
        return true;
    }

    public void cleanup() {
        if (factionMarkers != null) {
            factionMarkers.deleteMarkerSet();
        }
    }
}