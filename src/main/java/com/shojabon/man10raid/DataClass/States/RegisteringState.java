package com.shojabon.man10raid.DataClass.States;

import com.shojabon.man10raid.DataClass.RaidGame;
import com.shojabon.man10raid.DataClass.RaidPlayer;
import com.shojabon.man10raid.DataClass.RaidStateData;
import com.shojabon.man10raid.Enums.RaidState;
import com.shojabon.man10raid.Man10Raid;
import com.shojabon.man10raid.Man10RaidAPI;
import com.shojabon.man10raid.Utils.SScoreboard;
import com.shojabon.man10raid.Utils.SStringBuilder;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

import java.util.HashMap;
import java.util.UUID;

public class RegisteringState extends RaidStateData {

    RaidGame raid = Man10Raid.api.currentGame;


    @Override
    public void start() {
        Man10RaidAPI.broadcastHighlightedMessage("§a§l選手登録を開始しました\nレイドに参加したい者は選手登録をしてください");
        timerTillNextState.start();
    }

    @Override
    public void end() {
    }

    @Override
    public void defineTimer(){
        timerTillNextState.setRemainingTime(raid.registrationTime);
        timerTillNextState.addOnEndEvent(() -> {
            raid.setGameState(RaidState.PREPARATION);
        });
    }

    @Override
    public void defineBossBar() {
        String barTitle = "§c§l出場者登録中 §a§l残り§e§l{time}§a§l秒 現在登録者§e§l{registered}§a§l人";
        this.bar = Bukkit.createBossBar("", BarColor.WHITE, BarStyle.SOLID);
        timerTillNextState.linkBossBar(bar, true);
        timerTillNextState.addOnIntervalEvent(e -> bar.setTitle(barTitle.replace("{registered}", String.valueOf(raid.players.size())).replace("{time}", String.valueOf(e))));
    }

    @Override
    public void defineScoreboard() {
        scoreboard = new SScoreboard("TEST");
        scoreboard.setTitle("§4§lMan10Raid");

        scoreboard.setText(0, "§c§l選手登録中");

        timerTillNextState.addOnIntervalEvent(e -> {
            scoreboard.setText(3, "§a§l残り§e§l" + e + "§a§l秒");

            scoreboard.setText(2, "§a§l現在登録者§e§l" + raid.players.size() + "§a§l人");
            scoreboard.renderText();
        });
    }
}
