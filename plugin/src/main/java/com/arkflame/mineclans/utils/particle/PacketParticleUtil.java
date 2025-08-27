package com.arkflame.mineclans.utils.particle;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * A utility class to send particles directly via packets in Bukkit 1.8
 */
public class PacketParticleUtil {

    // Reflection cache
    private static final Map<String, Class<?>> classCache = new HashMap<>();
    private static final Map<String, Constructor<?>> constructorCache = new HashMap<>();
    private static final Map<String, Method> methodCache = new HashMap<>();
    private static final Map<String, Field> fieldCache = new HashMap<>();

    // NMS version
    private static final String NMS_VERSION;

    // Common reflection objects
    private static Method getHandleMethod;
    private static Field playerConnectionField;
    private static Method sendPacketMethod;

    // Initialize reflection objects
    static {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        NMS_VERSION = packageName.substring(packageName.lastIndexOf('.') + 1);

        try {
            Class<?> craftPlayerClass = getClass("org.bukkit.craftbukkit." + NMS_VERSION + ".entity.CraftPlayer");
            Class<?> entityPlayerClass = getClass("net.minecraft.server." + NMS_VERSION + ".EntityPlayer");
            Class<?> playerConnectionClass = getClass("net.minecraft.server." + NMS_VERSION + ".PlayerConnection");
            Class<?> packetClass = getClass("net.minecraft.server." + NMS_VERSION + ".Packet");

            getHandleMethod = craftPlayerClass.getMethod("getHandle");
            playerConnectionField = entityPlayerClass.getDeclaredField("playerConnection");
            sendPacketMethod = playerConnectionClass.getMethod("sendPacket", packetClass);
        } catch (Exception e) {
        }
    }

    /**
     * Spawn a particle effect for a single player
     * 
     * @param player   The player who should see the particle
     * @param location The location where the particle should appear
     * @param name     The particle effect name
     * @param count    The number of particles to spawn
     * @param offsetX  The X offset/spread
     * @param offsetY  The Y offset/spread
     * @param offsetZ  The Z offset/spread
     * @param extra    The extra data value (speed in most cases)
     * @return true if the particle was sent successfully, false otherwise
     */
    public static boolean spawnParticle(Player player, Location location, String name, int count,
            double offsetX, double offsetY, double offsetZ, int extra) {
        if (player == null || location == null || name == null) {
            return false;
        }

        try {
            // Create the particle packet
            Object packet = createParticlePacket(name, location,
                    (float) offsetX, (float) offsetY, (float) offsetZ,
                    extra, count);
            if (packet == null) {
                return false;
            }
            // Send the packet to the player
            sendPacket(player, packet);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Creates a particle packet for modern versions (1.9+)
     */
    private static Object createParticlePacket(String name, Location location,
            float offsetX, float offsetY, float offsetZ,
            float speed, int count) throws Exception {
        // Get the packet class
        Class<?> packetClass = getClass("net.minecraft.server." + NMS_VERSION + ".PacketPlayOutWorldParticles");
        if (packetClass == null) {
            return null;
        }

        // Get required enum classes
        Class<?> enumParticleClass = getClass("net.minecraft.server." + NMS_VERSION + ".EnumParticle");

        // Get or create the constructor
        String constructorKey = "WorldParticles#Modern";
        Constructor<?> constructor = constructorCache.get(constructorKey);

        if (constructor == null) {
            constructor = packetClass.getConstructor(
                    enumParticleClass, // Particle type
                    boolean.class, // Long distance
                    float.class, // X
                    float.class, // Y
                    float.class, // Z
                    float.class, // Offset X
                    float.class, // Offset Y
                    float.class, // Offset Z
                    float.class, // Speed
                    int.class, // Count
                    int[].class // Extra data
            );
            constructorCache.put(constructorKey, constructor);
        }

        // Get the particle enum value
        Object particleEnum = enumParticleClass.getField(name).get(null);

        // Create the packet instance
        return constructor.newInstance(
                particleEnum, // Particle type
                true, // Long distance
                (float) location.getX(),
                (float) location.getY(),
                (float) location.getZ(),
                offsetX,
                offsetY,
                offsetZ,
                speed,
                count,
                new int[0] // Extra data
        );
    }

    /**
     * Send a packet to a player
     */
    private static void sendPacket(Player player, Object packet) throws Exception {
        if (player == null || packet == null) {
            return;
        }
        // Get the player's connection
        Object entityPlayer = getHandleMethod.invoke(player);
        if (entityPlayer == null) {
            return;
        }
        Object playerConnection = playerConnectionField.get(entityPlayer);
        if (playerConnection == null) {
            return;
        }

        // Send the packet
        sendPacketMethod.invoke(playerConnection, packet);
    }

    /**
     * Get a class by name with caching
     */
    private static Class<?> getClass(String name) throws ClassNotFoundException {
        if (name == null) {
            return null;
        }
        Class<?> clazz = null;
        if (classCache.containsKey(name)) {
            clazz = classCache.get(name);
        } else {
            classCache.put(name, clazz = Class.forName(name));
        }
        return clazz;
    }

    /**
     * Utility method to get an enum value by name
     */
    private static <T extends Enum<T>> T getEnum(Class<T> enumClass, String name) {
        for (T constant : enumClass.getEnumConstants()) {
            if (constant.name().equalsIgnoreCase(name)) {
                return constant;
            }
        }
        return null;
    }

    /**
     * Particle effect names for reference
     */
    public static class ParticleEffect {
        // Standard enum names used in 1.8
        public static final String EXPLOSION_NORMAL = "EXPLOSION_NORMAL";
        public static final String EXPLOSION_LARGE = "EXPLOSION_LARGE";
        public static final String EXPLOSION_HUGE = "EXPLOSION_HUGE";
        public static final String FIREWORKS_SPARK = "FIREWORKS_SPARK";
        public static final String WATER_BUBBLE = "WATER_BUBBLE";
        public static final String WATER_SPLASH = "WATER_SPLASH";
        public static final String WATER_WAKE = "WATER_WAKE";
        public static final String SUSPENDED = "SUSPENDED";
        public static final String SUSPENDED_DEPTH = "SUSPENDED_DEPTH";
        public static final String CRIT = "CRIT";
        public static final String CRIT_MAGIC = "CRIT_MAGIC";
        public static final String SMOKE_NORMAL = "SMOKE_NORMAL";
        public static final String SMOKE_LARGE = "SMOKE_LARGE";
        public static final String SPELL = "SPELL";
        public static final String SPELL_INSTANT = "SPELL_INSTANT";
        public static final String SPELL_MOB = "SPELL_MOB";
        public static final String SPELL_MOB_AMBIENT = "SPELL_MOB_AMBIENT";
        public static final String SPELL_WITCH = "SPELL_WITCH";
        public static final String DRIP_WATER = "DRIP_WATER";
        public static final String DRIP_LAVA = "DRIP_LAVA";
        public static final String VILLAGER_ANGRY = "VILLAGER_ANGRY";
        public static final String VILLAGER_HAPPY = "VILLAGER_HAPPY";
        public static final String TOWN_AURA = "TOWN_AURA";
        public static final String NOTE = "NOTE";
        public static final String PORTAL = "PORTAL";
        public static final String ENCHANTMENT_TABLE = "ENCHANTMENT_TABLE";
        public static final String FLAME = "FLAME";
        public static final String LAVA = "LAVA";
        public static final String FOOTSTEP = "FOOTSTEP";
        public static final String CLOUD = "CLOUD";
        public static final String REDSTONE = "REDSTONE";
        public static final String SNOWBALL = "SNOWBALL";
        public static final String SNOW_SHOVEL = "SNOW_SHOVEL";
        public static final String SLIME = "SLIME";
        public static final String HEART = "HEART";
        public static final String BARRIER = "BARRIER";
        public static final String ITEM_CRACK = "ITEM_CRACK";
        public static final String BLOCK_CRACK = "BLOCK_CRACK";
        public static final String BLOCK_DUST = "BLOCK_DUST";
        public static final String WATER_DROP = "WATER_DROP";
        public static final String ITEM_TAKE = "ITEM_TAKE";
        public static final String MOB_APPEARANCE = "MOB_APPEARANCE";
    }
}