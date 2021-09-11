package com.shojabon.man10raid.DataClass;

import com.shojabon.man10raid.Man10Raid;
import com.shojabon.man10raid.Utils.STimer;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public abstract class RaidStateData implements Listener {

    Plugin plugin = Bukkit.getPluginManager().getPlugin("Man10Raid");


    //timer
    public STimer timerTillNextState = new STimer();

    public void defineTimer(){}

    //inner required start stop cancel functions

    void beforeStart(){
        defineTimer();
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
        start();
    }
    void beforeEnd(){
        HandlerList.unregisterAll(this);
        timerTillNextState.stop();
        end();
    }
    public void beforeCancel(){
        HandlerList.unregisterAll(this);
        timerTillNextState.stop();
        cancel();
    }

    // interface start stop cancel functions

    public void start(){}
    public void end(){}
    public void cancel(){}
}
