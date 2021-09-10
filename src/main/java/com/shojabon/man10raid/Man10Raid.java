package com.shojabon.man10raid;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class Man10Raid extends JavaPlugin {

    public static ExecutorService threadPool = Executors.newCachedThreadPool();

    @Override
    public void onEnable() {
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
