package com.shojabon.man10raid.DataClass.States;

import com.shojabon.man10raid.DataClass.RaidStateData;
import com.shojabon.man10raid.Man10Raid;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class RegisteringState extends RaidStateData {

    @Override
    public void start() {
        super.start();

    }

    @EventHandler
    public void test(PlayerMoveEvent e){
        e.getPlayer().sendMessage("a");
    }
}
