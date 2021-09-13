package com.shojabon.man10raid.DataClass.States;

import com.shojabon.man10raid.DataClass.RaidGame;
import com.shojabon.man10raid.DataClass.RaidPlayer;
import com.shojabon.man10raid.DataClass.RaidStateData;
import com.shojabon.man10raid.Enums.RaidState;
import com.shojabon.man10raid.Man10Raid;
import com.shojabon.man10raid.Utils.SScoreboard;
import com.shojabon.man10raid.Utils.STimer;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PreparationState extends RaidStateData {

    RaidGame raid = Man10Raid.api.currentGame;

    @Override
    public void start() {
        //teleport all players to lobby
        raid.teleportAllPlayersToLobby();

        //if first game
        if(raid.currentGame == 0){
            raid.dividePlayers();
            for(RaidPlayer player: raid.players.values()){
                if(player.getPlayer() == null) continue; //if offline

                if(player.registeredGame != -1){
                    //has match
                    sendHighlightedMessage(player.getPlayer(), "§c§l当選しました！\n" + "あなたの試合は" + (player.registeredGame + 1) + "試合目です");
                }else{
                    //no match
                    sendHighlightedMessage(player.getPlayer(), "§b§l残念ながら本日の試合は落選しました");
                }
            }
        }

        //move player whitelist process

        Bukkit.getServer().broadcastMessage("starting preparation timer");
        timerTillNextState.start(); //start count down
    }


    @Override
    public void end() {
    }

    @Override
    public void defineTimer(){
        timerTillNextState.setRemainingTime(raid.preparationTime);
        timerTillNextState.addOnIntervalEvent(e -> {
            Bukkit.getServer().broadcastMessage(e + " seconds remaining");
        });
        timerTillNextState.addOnEndEvent(() -> {
            raid.setGameState(RaidState.IN_GAME);
        });
    }

    @Override
    public void defineBossBar() {
        this.bar = Bukkit.createBossBar("選手準備フェーズ 残り{time}秒", BarColor.WHITE, BarStyle.SOLID);
        timerTillNextState.linkBossBar(bar, true);
    }

    @Override
    public void defineScoreboard() {
        scoreboard = new SScoreboard("TEST");
        scoreboard.setTitle("選手準備中!!");
        timerTillNextState.addOnIntervalEvent(e -> {
            scoreboard.setText(0, "残り" + e + "秒");
        });
        scoreboard.setText(1, "test2");
        scoreboard.setText(3, "test4");
        scoreboard.setText(2, "test3");
    }

    public void sendHighlightedMessage(Player p, String message){
        p.sendMessage("§e§l=================================");
        p.sendMessage("");
        p.sendMessage(message);
        p.sendMessage("");
        p.sendMessage("§e§l=================================");
    }

}
