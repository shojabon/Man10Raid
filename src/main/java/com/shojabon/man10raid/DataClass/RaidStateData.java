package com.shojabon.man10raid.DataClass;

import com.shojabon.mcutils.Utils.SScoreboard;
import com.shojabon.mcutils.Utils.STimer;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Scoreboard;

public abstract class RaidStateData implements Listener {

    Plugin plugin = Bukkit.getPluginManager().getPlugin("Man10Raid");


    //timer
    public STimer timerTillNextState = new STimer();
    public void defineTimer(){}

    //boss bar
    public BossBar bar = null;
    public void defineBossBar(){}

    //score board
    public SScoreboard scoreboard = null;
    public void defineScoreboard(){}

    //inner required start stop cancel functions

    void beforeStart(){
        defineTimer();
        defineBossBar();
        defineScoreboard();
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);

        //register boss bar
        if(bar != null){
            for(Player p:Bukkit.getServer().getOnlinePlayers()){
                this.bar.addPlayer(p);
            }
        }

        //register scoreboard
        if(scoreboard != null){
            for(Player p:Bukkit.getServer().getOnlinePlayers()){
                this.scoreboard.addPlayer(p);
            }
        }

        start();
    }
    void beforeEnd(){
        HandlerList.unregisterAll(this);
        timerTillNextState.stop();
        end();

        //remove bar
        if(bar != null){
            bar.removeAll();
            bar.setVisible(false);
            bar = null;
        }

        //scoreboard
        if(scoreboard != null) {
            scoreboard.remove();
            scoreboard = null;
        }
    }
    public void beforeCancel(){
        HandlerList.unregisterAll(this);
        timerTillNextState.stop();
        cancel();

        //remove bar
        if(bar != null){
            bar.removeAll();
            bar.setVisible(false);
            bar = null;
        }

        //scoreboard
        if(scoreboard != null) {
            scoreboard.remove();
            scoreboard = null;
        }
    }

    // interface start stop cancel functions

    public void start(){}
    public void end(){}
    public void cancel(){}


    //bar events
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        if(bar == null) return;
        bar.addPlayer(e.getPlayer());
        scoreboard.addPlayer(e.getPlayer());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e){
        if(bar == null) return;
        bar.addPlayer(e.getPlayer());
    }
}
