package com.rainchat.rainlib.utils;


import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

public class ServerLog {

    public Plugin plugin;

    public ServerLog(Plugin plugin) {
        this.plugin = plugin;
    }

    public void log(Level level, String message) {
        plugin.getServer().getLogger().log(level,
                "[" + plugin.getDescription().getName() + "] " + message);
    }

}
