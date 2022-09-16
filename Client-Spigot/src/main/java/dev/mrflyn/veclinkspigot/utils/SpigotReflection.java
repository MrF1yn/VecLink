package dev.mrflyn.veclinkspigot.utils;


import dev.mrflyn.veclinkspigot.VecLinkMainSpigot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;

import static dev.mrflyn.veclinkspigot.utils.Crafty.*;
import static org.bukkit.Bukkit.getServer;

public final class SpigotReflection {
    private static SpigotReflection INSTANCE;

    public static SpigotReflection spigotReflection() {
        if(INSTANCE==null)
            INSTANCE = new SpigotReflection();
        return INSTANCE;
    }

    private static final Class<?> MinecraftServer_class = needNMSClassOrElse(
            "MinecraftServer",
            "net.minecraft.server.MinecraftServer"
    );
    private static final Class<?> CraftPlayer_class = needCraftClass("entity.CraftPlayer");
    private static final Class<?> ServerPlayer_class = needNMSClassOrElse(
            "EntityPlayer",
            "net.minecraft.server.level.EntityPlayer",
            "net.minecraft.server.level.ServerPlayer"
    );

    private static final MethodHandle CraftPlayer_getHandle_method = needMethod(CraftPlayer_class, "getHandle", ServerPlayer_class);
    private static final MethodHandle MinecraftServer_getServer_method = needStaticMethod(MinecraftServer_class, "getServer", MinecraftServer_class);

    private static final  Field ServerPlayer_latency_field = pingField();
    private static final Field MinecraftServer_recentTps_field = needField(MinecraftServer_class, "recentTps"); // Spigot added field

    private final Field MinecraftServer_recentTickTimes_field = tickTimesField();

    private static Field tickTimesField() {
        final String tickTimes;
        String version = getServer().getClass().getPackage().getName();

        final int ver = Integer.parseInt(version.substring(version.lastIndexOf('.') + 1).split("_")[1]);
        if (ver < 13) {
            tickTimes = "h";
        } else if (ver == 13) {
            tickTimes = "d";
        } else if (ver == 14 || ver == 15) {
            tickTimes = "f";
        } else if (ver == 16) {
            tickTimes = "h";
        } else if (ver == 17) {
            tickTimes = "n";
        } else if (ver == 18) {
            tickTimes = "o";
        } else if (ver == 19) {
            tickTimes = "k";
        } else {
            throw new IllegalStateException("Don't know tickTimes field name!");
        }
        return needField(MinecraftServer_class, tickTimes);
    }

    private static  Field pingField() {
        final Field mojang = findField(ServerPlayer_class, "latency");
        if (mojang != null) {
            return mojang;
        }
        final  Field spigotNamedOld = findField(ServerPlayer_class, "ping");
        return spigotNamedOld;
    }

    public int ping(final Player player) {
        if (ServerPlayer_latency_field == null) {
            throw new IllegalStateException("ServerPlayer_latency_field is null");
        }
        final Object nmsPlayer = invokeOrThrow(CraftPlayer_getHandle_method, player);
        try {
            return ServerPlayer_latency_field.getInt(nmsPlayer);
        } catch (final IllegalAccessException e) {
            throw new IllegalStateException(String.format("Failed to get ping for player: '%s'", player.getName()), e);
        }
    }

    public double averageTickTime() {
        final Object server = invokeOrThrow(MinecraftServer_getServer_method);
        try {
            final long[] recentMspt = (long[]) this.MinecraftServer_recentTickTimes_field.get(server);
            return TPSUtil.toMilliseconds(TPSUtil.average(recentMspt));
        } catch (final IllegalAccessException e) {
            throw new IllegalStateException("Failed to get server mspt", e);
        }
    }

    public double [] recentTps() {
        final Object server = invokeOrThrow(MinecraftServer_getServer_method);
        try {
            return (double[]) MinecraftServer_recentTps_field.get(server);
        } catch (final IllegalAccessException e) {
            throw new IllegalStateException("Failed to get server TPS", e);
        }
    }

    private static MethodHandle needMethod(final Class<?> holderClass, final String methodName, final Class<?> returnClass, final Class<?> ... parameterClasses) {
        return Objects.requireNonNull(
                Crafty.findMethod(holderClass, methodName, returnClass, parameterClasses),
                String.format(
                        "Could not locate method '%s' in class '%s'",
                        methodName,
                        holderClass.getCanonicalName()
                )
        );
    }

    private static MethodHandle needStaticMethod(final Class<?> holderClass, final String methodName, final Class<?> returnClass, final Class<?> ... parameterClasses) {
        return Objects.requireNonNull(
                Crafty.findStaticMethod(holderClass, methodName, returnClass, parameterClasses),
                String.format(
                        "Could not locate static method '%s' in class '%s'",
                        methodName,
                        holderClass.getCanonicalName()
                )
        );
    }

    public static Field needField(final Class<?> holderClass, final String fieldName) {
        final Field field;
        try {
            field = holderClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        } catch (final NoSuchFieldException e) {
            throw new IllegalStateException(String.format("Unable to find field '%s' in class '%s'", fieldName, holderClass.getCanonicalName()), e);
        }
    }

    private static Object invokeOrThrow(final MethodHandle methodHandle, final  Object ... params) {
        try {
            if (params.length == 0) {
                return methodHandle.invoke();
            }
            return methodHandle.invokeWithArguments(params);
        } catch (final Throwable throwable) {
            throw new IllegalStateException(String.format("Unable to invoke method with args '%s'", Arrays.toString(params)), throwable);
        }
    }
}