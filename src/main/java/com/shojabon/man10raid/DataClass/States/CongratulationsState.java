package com.shojabon.man10raid.DataClass.States;

import com.shojabon.man10raid.DataClass.RaidGame;
import com.shojabon.man10raid.DataClass.RaidPlayer;
import com.shojabon.man10raid.DataClass.RaidStateData;
import com.shojabon.man10raid.Enums.RaidState;
import com.shojabon.man10raid.Man10Raid;
import com.shojabon.man10raid.Man10RaidAPI;
import com.shojabon.mcutils.Utils.SScoreboard;
import com.shojabon.mcutils.Utils.STimer;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public class CongratulationsState extends RaidStateData {

    RaidGame raid = Man10Raid.api.currentGame;

    STimer endAreaTimer = new STimer();



    @Override
    public void start() {
        timerTillNextState.start();
        for(RaidPlayer player : raid.getPlayersInGame(raid.currentGame)){
            if(player.getPlayer() == null) continue;
            if(!player.getPlayer().isOnline()) continue;
            if(player.livesLeft == 0) continue;
            player.getPlayer().teleport(raid.endArea);
        }

    }

    @Override
    public void end() {
        endAreaTimer.stop();
    }

    @Override
    public void defineTimer(){
        timerTillNextState.setRemainingTime(raid.endAreaTime);
        timerTillNextState.addOnEndEvent(this::endGameProcess);
    }

    @Override
    public void defineBossBar() {
        String title = "§c§lおめでとうフェーズ §a§l残り§e§l{time}§a§l秒";
        this.bar = Bukkit.createBossBar(title, BarColor.WHITE, BarStyle.SOLID);
        timerTillNextState.linkBossBar(bar, true);
        timerTillNextState.addOnIntervalEvent(e -> bar.setTitle(title.replace("{time}", String.valueOf(e))));
    }

    @Override
    public void defineScoreboard() {
        scoreboard = new SScoreboard("TEST");
        scoreboard.setTitle("§c§lMan10Raid");
        scoreboard.setText(0, "§c§lおめでとうフェーズ");
        timerTillNextState.addOnIntervalEvent(e -> {
            scoreboard.setText(2, "§a§l残り§e§l" + e + "§a§l秒");

            scoreboard.renderText();
        });
    }

    public void endGameProcess(){
        if(raid.currentGame < raid.scheduledGames-1){
            //has next
            raid.currentGame += 1;
            raid.setGameState(RaidState.PREPARATION);
            return;
        }
        //if last
        Man10Raid.api.endGame();
    }

    @EventHandler
    public void disableDamage(EntityDamageEvent e){
        if(!(e.getEntity() instanceof Player)) return;
        e.setCancelled(true);
    }

    @Override
    public void cancel() {
        endAreaTimer.stop();
    }


}
