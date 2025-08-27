package com.arkflame.mineclans.hooks;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import com.arkflame.mineclans.MineClans;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * A reflection-based WorldGuard utility that automatically selects the integration
 * based on the version of WorldGuard (WG7 or WG6) present on the server.
 *
 * It offers the same functionality as your previous WG6 and WG7 integration classes,
 * namely checking whether a given world chunk overlaps any WorldGuard region.
 */
public class WorldGuardReflectionUtil {

    private static final int CHUNK_SIZE = 16;
    private final boolean enabled;
    private final WGIntegration integration;

    /**
     * Initializes the integration. It first checks if the WorldGuard plugin is enabled
     * in the server. Then it detects the version available (WG7 is preferred) and sets
     * up the proper reflective methods.
     */
    public WorldGuardReflectionUtil() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
        if (plugin == null || !plugin.isEnabled()) {
            integration = null;
            enabled = false;
            MineClans.getInstance().getLogger().info("WorldGuard integration failed during setup");
        } else {
            WGVersion version = detectWGVersion();
            WGIntegration tempIntegration = null;
            boolean tempEnabled = false;
            try {
                switch (version) {
                    case WG7:
                        tempIntegration = new WG7Integration();
                        tempEnabled = true;
                        MineClans.getInstance().getLogger().info("WorldGuard 7 integration enabled");
                        break;
                    case WG6:
                        tempIntegration = new WG6Integration();
                        tempEnabled = true;
                        MineClans.getInstance().getLogger().info("WorldGuard 6 integration enabled");
                        break;
                    case NONE:
                    default:
                        tempEnabled = false;
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                tempEnabled = false;
            }
            integration = tempIntegration;
            enabled = tempEnabled;
        }
    }

    /**
     * @return true if WorldGuard is present and the integration is enabled.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Checks whether the chunk given by its coordinates in worldName overlaps any WorldGuard region.
     *
     * @param x         chunk X coordinate
     * @param z         chunk Z coordinate
     * @param worldName the name of the world
     * @return true if the chunk overlaps with any region, false otherwise.
     */
    public boolean chunkOverlapsRegion(int x, int z, String worldName) {
        if (!enabled || integration == null)
            return false;
        return integration.chunkOverlapsRegion(x, z, worldName);
    }

    /**
     * Overloaded version to check a bukkit Chunk.
     *
     * @param chunk the chunk to check.
     * @return true if the chunk overlaps with any region, false otherwise.
     */
    public boolean chunkOverlapsRegion(Chunk chunk) {
        if (chunk == null) {
            return false;
        }
        return chunkOverlapsRegion(chunk.getX(), chunk.getZ(), chunk.getWorld().getName());
    }

    // --- Internals: version detection and reflective integration ---

    /**
     * Enumeration for the two possible WorldGuard versions we support.
     */
    private enum WGVersion {
        WG6, WG7, NONE
    }

    /**
     * Checks for the existence of a class unique to WG7 and falls back to WG6 otherwise.
     *
     * @return WGVersion.WG7 if found, WG6 if not, or NONE if neither is available.
     */
    private WGVersion detectWGVersion() {
        try {
            Class.forName("com.sk89q.worldguard.internal.platform.WorldGuardPlatform");
            return WGVersion.WG7;
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("com.sk89q.worldguard.bukkit.WorldGuardPlugin");
                return WGVersion.WG6;
            } catch (ClassNotFoundException ex) {
                return WGVersion.NONE;
            }
        }
    }

    /**
     * Defines the basic operations we need from WorldGuard.
     */
    private interface WGIntegration {
        boolean chunkOverlapsRegion(int x, int z, String worldName);
    }

    /**
     * WG7 integration using reflection.
     *
     * This branch uses the WG7 API calls. It calls:
     *  - WorldGuard.getInstance().getPlatform() to get the platform instance.
     *  - platform.getRegionContainer().get(BukkitAdapter.adapt(world)) to get the region manager.
     *  - And creates the temporary region instance using com.sk89q.worldedit.math.BlockVector3.
     */
    private class WG7Integration implements WGIntegration {
        private final Object worldGuardPlatform;
        private final Method getRegionContainerMethod;
        private final Method regionContainerGetMethod;
        private final Method adaptMethod;
        private final Constructor<?> protectedCuboidRegionConstructor;
        private final Method blockVector3AtMethod;

        public WG7Integration() throws Exception {
            // Get WorldGuard instance and its platform.
            Class<?> worldGuardClass = Class.forName("com.sk89q.worldguard.WorldGuard");
            Method getInstanceMethod = worldGuardClass.getMethod("getInstance");
            Object worldGuardInstance = getInstanceMethod.invoke(null);
            Method getPlatformMethod = worldGuardClass.getMethod("getPlatform");
            worldGuardPlatform = getPlatformMethod.invoke(worldGuardInstance);

            // Obtain region container and its "get" method.
            getRegionContainerMethod = worldGuardPlatform.getClass().getMethod("getRegionContainer");
            Object regionContainer = getRegionContainerMethod.invoke(worldGuardPlatform);
            Class<?> regionContainerClass = regionContainer.getClass();
            // The container expects a com.sk89q.worldedit.world.World instance.
            Class<?> worldWEClass = Class.forName("com.sk89q.worldedit.world.World");
            regionContainerGetMethod = regionContainerClass.getMethod("get", worldWEClass);

            // Obtain the BukkitAdapter.adapt(World) method.
            Class<?> bukkitAdapterClass = Class.forName("com.sk89q.worldedit.bukkit.BukkitAdapter");
            adaptMethod = bukkitAdapterClass.getMethod("adapt", World.class);

            // For vector construction: use BlockVector3.at(int, int, int)
            Class<?> blockVector3Class = Class.forName("com.sk89q.worldedit.math.BlockVector3");
            blockVector3AtMethod = blockVector3Class.getMethod("at", int.class, int.class, int.class);

            // Get the constructor of ProtectedCuboidRegion(String, BlockVector3, BlockVector3)
            Class<?> protectedCuboidRegionClass = Class.forName("com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion");
            protectedCuboidRegionConstructor = protectedCuboidRegionClass.getConstructor(String.class, blockVector3Class, blockVector3Class);
        }

        @Override
        public boolean chunkOverlapsRegion(int x, int z, String worldName) {
            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                return false;
            }
            try {
                // Get the region manager
                Object regionContainer = getRegionContainerMethod.invoke(worldGuardPlatform);
                Object adaptedWorld = adaptMethod.invoke(null, world);
                Object regionManager = regionContainerGetMethod.invoke(regionContainer, adaptedWorld);
                if (regionManager == null) {
                    return false;
                }

                // Convert chunk coordinates to block boundaries.
                int minX = x * CHUNK_SIZE;
                int minZ = z * CHUNK_SIZE;
                int maxX = minX + CHUNK_SIZE - 1;
                int maxZ = minZ + CHUNK_SIZE - 1;

                // Create BlockVector3 objects using the static "at" method.
                Object minVec = blockVector3AtMethod.invoke(null, minX, 0, minZ);
                Object maxVec = blockVector3AtMethod.invoke(null, maxX, world.getMaxHeight(), maxZ);

                // Build a temporary ProtectedCuboidRegion instance.
                Object chunkRegion = protectedCuboidRegionConstructor.newInstance("temp-chunk-check", minVec, maxVec);

                // Call regionManager.getApplicableRegions(chunkRegion)
                Method getApplicableRegionsMethod = regionManager.getClass().getMethod("getApplicableRegions", chunkRegion.getClass());
                Object applicableRegions = getApplicableRegionsMethod.invoke(regionManager, chunkRegion);

                // Check the size of the returned ApplicableRegionSet.
                Method sizeMethod = applicableRegions.getClass().getMethod("size");
                int size = (Integer) sizeMethod.invoke(applicableRegions);

                return size > 0;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    /**
     * WG6 integration using reflection.
     *
     * This branch uses the legacy WG6 code paths. It calls:
     *  - WorldGuardPlugin.inst() to retrieve the instance.
     *  - instance.getRegionManager(world) to get the region manager.
     *  - And creates the temporary region using com.sk89q.worldedit.BlockVector.
     */
    private class WG6Integration implements WGIntegration {
        private final Object worldGuardPlugin;
        private final Method getRegionManagerMethod;
        private final Constructor<?> protectedCuboidRegionConstructor;
        private final Constructor<?> blockVectorConstructor;
        private final Class<?> protectedRegionClass;

        public WG6Integration() throws Exception {
            Class<?> wgPluginClazz = Class.forName("com.sk89q.worldguard.bukkit.WorldGuardPlugin");
            Method instMethod = wgPluginClazz.getMethod("inst");
            worldGuardPlugin = instMethod.invoke(null);

            getRegionManagerMethod = wgPluginClazz.getMethod("getRegionManager", World.class);

            // For WG6, load the BlockVector class.
            Class<?> blockVectorClass = Class.forName("com.sk89q.worldedit.BlockVector");
            // And load the ProtectedCuboidRegion which expects BlockVector parameters.
            Class<?> protectedCuboidRegionClass = Class.forName("com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion");
            protectedCuboidRegionConstructor = protectedCuboidRegionClass.getConstructor(String.class, blockVectorClass, blockVectorClass);
            // Get the BlockVector constructor: new BlockVector(int, int, int)
            blockVectorConstructor = blockVectorClass.getConstructor(int.class, int.class, int.class);
            protectedRegionClass = Class.forName("com.sk89q.worldguard.protection.regions.ProtectedRegion");
        }

        @Override
        public boolean chunkOverlapsRegion(int x, int z, String worldName) {
            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                return false;
            }
            try {
                // Get the region manager via WorldGuardPlugin.
                Object regionManager = getRegionManagerMethod.invoke(worldGuardPlugin, world);
                if (regionManager == null) {
                    return false;
                }

                // Compute chunk boundaries.
                int minX = x * CHUNK_SIZE;
                int minZ = z * CHUNK_SIZE;
                int maxX = minX + CHUNK_SIZE - 1;
                int maxZ = minZ + CHUNK_SIZE - 1;

                // Create BlockVector objects for the minimum and maximum points.
                Object minVec = blockVectorConstructor.newInstance(minX, 0, minZ);
                Object maxVec = blockVectorConstructor.newInstance(maxX, world.getMaxHeight(), maxZ);

                // Build a temporary ProtectedCuboidRegion.
                Object chunkRegion = protectedCuboidRegionConstructor.newInstance("temp-chunk-check", minVec, maxVec);

                // Call regionManager.getApplicableRegions(chunkRegion)
                Method getApplicableRegionsMethod = regionManager.getClass().getMethod("getApplicableRegions", protectedRegionClass);
                Object applicableRegions = getApplicableRegionsMethod.invoke(regionManager, chunkRegion);

                // Determine if any regions overlap.
                Method sizeMethod = applicableRegions.getClass().getMethod("size");
                int size = (Integer) sizeMethod.invoke(applicableRegions);

                return size > 0;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }
}
