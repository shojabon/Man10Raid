package com.shojabon.man10raid.DataClass.States;

import com.shojabon.man10raid.DataClass.RaidGame;
import com.shojabon.man10raid.DataClass.RaidStateData;
import com.shojabon.man10raid.Enums.RaidState;
import com.shojabon.man10raid.Man10Raid;
import com.shojabon.man10raid.Man10RaidAPI;
import com.shojabon.man10raid.Utils.STimer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class RegisteringState extends RaidStateData {

    RaidGame raid = Man10Raid.api.currentGame;

    @Override
    public void start() {


        Bukkit.getServer().broadcastMessage("started registration");
        timerTillNextState.start();
    }

    @Override
    public void end() {
        Bukkit.getServer().broadcastMessage("end registration");
    }

    @Override
    public void defineTimer(){
        timerTillNextState.setRemainingTime(raid.registrationTime);
        timerTillNextState.addOnIntervalEvent(e -> {
            Bukkit.getServer().broadcastMessage(e + " seconds remaining");
        });
        timerTillNextState.addOnEndEvent(() -> {
            raid.setGameState(RaidState.PREPARATION);

        });
    }

}
