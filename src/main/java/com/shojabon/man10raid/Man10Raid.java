package com.shojabon.man10raid;

import com.shojabon.man10raid.DataClass.RaidGame;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class Man10Raid extends JavaPlugin {

    public static ExecutorService threadPool = Executors.newCachedThreadPool();
    public static Man10RaidAPI api;


    @Override
    public void onEnable() {
        // Plugin startup logic
        api = new Man10RaidAPI(this);
        //
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
