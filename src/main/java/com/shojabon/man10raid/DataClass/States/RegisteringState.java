package com.shojabon.man10raid.DataClass.States;

import com.shojabon.man10raid.DataClass.RaidStateData;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class RegisteringState extends RaidStateData {

    @Override
    public void start() {
        Bukkit.getServer().broadcastMessage("started registration");
    }

}
