package com.shojabon.man10raid.DataClass.States;

import com.shojabon.man10raid.DataClass.RaidGame;
import com.shojabon.man10raid.DataClass.RaidStateData;
import com.shojabon.man10raid.Enums.RaidState;
import com.shojabon.man10raid.Man10Raid;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

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

    @Override
    public void defineBossBar() {
        this.bar = Bukkit.createBossBar("出場者登録中 残り{time}秒", BarColor.WHITE, BarStyle.SOLID);
        timerTillNextState.linkBossBar(bar, true);
    }
}
