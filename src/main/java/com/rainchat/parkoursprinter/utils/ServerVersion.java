package com.rainchat.parkoursprinter.utils;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;

public enum ServerVersion {
    UNKNOWN,
    V1_7,
    V1_8,
    V1_9,
    V1_10,
    V1_11,
    V1_12,
    V1_13,
    V1_14,
    V1_15,
    V1_16,
    V1_17,
    V1_18,
    V1_19,
    V1_20;

    private static final String serverPackagePath = Bukkit.getServer().getClass().getPackage().getName();
    private static final String serverPackageVersion = serverPackagePath.substring(serverPackagePath.lastIndexOf(46) + 1);
    private static final String serverReleaseVersion = serverPackageVersion.indexOf(82) != -1 ? serverPackageVersion.substring(serverPackageVersion.indexOf(82) + 1) : "";
    private static final ServerVersion serverVersion = getVersion();

    private ServerVersion() {
    }

    private static ServerVersion getVersion() {
        ServerVersion[] var0 = values();
        int var1 = var0.length;

        for (int var2 = 0; var2 < var1; ++var2) {
            ServerVersion version = var0[var2];
            if (serverPackageVersion.toUpperCase().startsWith(version.name())) {
                return version;
            }
        }

        return UNKNOWN;
    }

    public boolean isLessThan(ServerVersion other) {
        return this.ordinal() < other.ordinal();
    }

    public boolean isAtOrBelow(ServerVersion other) {
        return this.ordinal() <= other.ordinal();
    }

    public boolean isGreaterThan(ServerVersion other) {
        return this.ordinal() > other.ordinal();
    }

    public boolean isAtLeast(ServerVersion other) {
        return this.ordinal() >= other.ordinal();
    }

    public static String getServerVersionString() {
        return serverPackageVersion;
    }

    public static String getVersionReleaseNumber() {
        return serverReleaseVersion;
    }

    public static ServerVersion getServerVersion() {
        return serverVersion;
    }

    public static boolean isServerVersion(ServerVersion version) {
        return serverVersion == version;
    }

    public static boolean isServerVersion(ServerVersion... versions) {
        return ArrayUtils.contains(versions, serverVersion);
    }

    public static boolean isServerVersionAbove(ServerVersion version) {
        return serverVersion.ordinal() > version.ordinal();
    }

    public static boolean isServerVersionAtLeast(ServerVersion version) {
        return serverVersion.ordinal() >= version.ordinal();
    }

    public static boolean isServerVersionAtOrBelow(ServerVersion version) {
        return serverVersion.ordinal() <= version.ordinal();
    }

    public static boolean isServerVersionBelow(ServerVersion version) {
        return serverVersion.ordinal() < version.ordinal();
    }
}