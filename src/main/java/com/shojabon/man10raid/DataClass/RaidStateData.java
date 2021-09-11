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


    public void start(){
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }
    public void stop(){
        HandlerList.unregisterAll(this);

    }
    public void cancel(){
        HandlerList.unregisterAll(this);

    }
}
