package com.shojabon.man10raid.DataClass;

import com.shojabon.man10raid.Man10Raid;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public abstract class RaidStateData implements Listener {

    Plugin plugin = Bukkit.getPluginManager().getPlugin("Man10Raid");

    //inner required start stop cancel functions

    void beforeStart(){
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
        start();
    }
    void beforeStop(){
        HandlerList.unregisterAll(this);
        stop();
    }
    void beforeCancel(){
        HandlerList.unregisterAll(this);
        cancel();
    }

    // interface start stop cancel functions

    public void start(){}
    public void stop(){}
    public void cancel(){}
}
