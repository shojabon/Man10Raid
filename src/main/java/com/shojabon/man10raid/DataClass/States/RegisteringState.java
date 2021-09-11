package com.shojabon.man10raid.DataClass.States;

import com.shojabon.man10raid.DataClass.RaidGame;
import com.shojabon.man10raid.DataClass.RaidStateData;
import com.shojabon.man10raid.Man10Raid;
import com.shojabon.man10raid.Man10RaidAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class RegisteringState extends RaidStateData {

    RaidGame raid = Man10Raid.api.currentGame;

    @Override
    public void start() {
        Bukkit.getServer().broadcastMessage("started registration");
    }

}
