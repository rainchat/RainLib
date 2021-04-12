package com.rainchat.parkoursprinter.utils;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;

public enum ServerProject {
    UNKNOWN,
    CRAFTBUKKIT,
    SPIGOT,
    PAPER,
    TACO,
    GLOWSTONE;

    private static final ServerProject serverProject = checkProject();

    private ServerProject() {
    }

    private static ServerProject checkProject() {
        String serverPath = Bukkit.getServer().getClass().getName();
        if (serverPath.contains("glowstone")) {
            return GLOWSTONE;
        } else {
            try {
                Class.forName("net.techcable.tacospigot.TacoSpigotConfig");
                return TACO;
            } catch (ClassNotFoundException var5) {
                try {
                    Class.forName("com.destroystokyo.paperclip.Paperclip");
                    return PAPER;
                } catch (ClassNotFoundException var4) {
                    try {
                        Class.forName("com.destroystokyo.paper.PaperConfig");
                        return PAPER;
                    } catch (ClassNotFoundException var3) {
                        try {
                            Class.forName("org.spigotmc.SpigotConfig");
                            return SPIGOT;
                        } catch (ClassNotFoundException var2) {
                            return serverPath.contains("craftbukkit") ? CRAFTBUKKIT : UNKNOWN;
                        }
                    }
                }
            }
        }
    }

    public static ServerProject getServerVersion() {
        return serverProject;
    }

    public static boolean isServer(ServerProject version) {
        return serverProject == version;
    }

    public static boolean isServer(ServerProject... versions) {
        return ArrayUtils.contains(versions, serverProject);
    }
}
