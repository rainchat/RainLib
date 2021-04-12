package com.rainchat.parkoursprinter;

import com.rainchat.parkoursprinter.commands.MainCommand;

import com.rainchat.parkoursprinter.listeners.Dash;
import com.rainchat.parkoursprinter.utils.commands.CommandManager;

import org.bukkit.plugin.java.JavaPlugin;



public final class ParkourSprinter extends JavaPlugin {



    @Override
    public void onEnable() {

        CommandManager commandManager = new CommandManager(this);
        commandManager.addCommand(new MainCommand());

        getServer().getPluginManager().registerEvents(new Dash(), this);

    }

    @Override
    public void onDisable() {

    }



}