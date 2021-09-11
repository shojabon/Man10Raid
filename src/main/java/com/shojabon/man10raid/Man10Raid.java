package com.shojabon.man10raid;

import com.shojabon.man10raid.Commands.Man10RaidCommand;
import com.shojabon.man10raid.DataClass.RaidGame;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class Man10Raid extends JavaPlugin {

    public static ExecutorService threadPool = Executors.newCachedThreadPool();
    public static Man10RaidAPI api;
    public static String prefix;

    public static FileConfiguration config;


    @Override
    public void onEnable() {
        // Plugin startup logic
        api = new Man10RaidAPI(this);
        //

        config = getConfig();

        Man10RaidCommand command = new Man10RaidCommand(this);
        getCommand("mraid").setExecutor(command);
        getCommand("mraid").setTabCompleter(command);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
