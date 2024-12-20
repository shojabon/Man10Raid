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
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.UUID;

public class FinishState extends RaidStateData {

    RaidGame raid = Man10Raid.api.currentGame;

    STimer endAreaTimer = new STimer();

    Plugin plugin = Bukkit.getPluginManager().getPlugin("Man10Raid");


    @Override
    public void start() {
        timerTillNextState.start();

        //raid results

        if(raid.won){
            Man10RaidAPI.broadcastHighlightedMessage("§c§l勝利");
        }else{
            Man10RaidAPI.broadcastHighlightedMessage("§b§l敗北");
        }

        for(RaidPlayer player: raid.getPlayersInGame(raid.currentGame)){
            Player p = player.getPlayer();
            if(p == null) continue;
            if(!p.isOnline()) continue;
            p.sendMessage("§e§l==============[結果発表]==============");
            p.sendMessage("");
            p.sendMessage("§a§l総ダメージ数: §e§l" + player.totalDamage);
            p.sendMessage("§a§l総プロジェクタイルダメージ数: §e§l" + player.totalProjectileDamage);
            p.sendMessage("§a§l総ヒール数: §e§l" + player.totalHeal);
            p.sendMessage("");
            p.sendMessage("§e§l===================================");
        }

        if(raid.won){
            raid.payOutToPlayers(raid.currentGame);
        }

        //logging
        raid.logPlayersInGame(raid.currentGame);
        raid.logCurrentMatch();

    }

    @Override
    public void end() {
        endAreaTimer.stop();
    }

    @Override
    public void defineTimer(){
        timerTillNextState.setRemainingTime(60);
        timerTillNextState.addOnEndEvent(() -> {
            if(raid.won){
                //win process
                for(RaidPlayer player : raid.getPlayersInGame(raid.currentGame)){
                    Bukkit.broadcastMessage(player.name + " " + player.totalDamage);
                }

                // check for cheaters
                for(RaidPlayer player : raid.getPlayersInGame(raid.currentGame)){
                    System.out.println(player.name + " " + player.livesLeft + " " + player.aliveTime + " " + raid.totalGameTime + " " +  ((float) raid.totalGameTime * raid.mustBeAliveForPercentOfGame));
                    if(player.livesLeft == 0) continue;
                    if(player.aliveTime >= ((float) raid.totalGameTime * raid.mustBeAliveForPercentOfGame)) continue;
                    player.livesLeft = 0;
                }

                if(raid.endArea != null){
                    //if end area exists
                    raid.setGameState(RaidState.CONGRATULATIONS);
                    return;
                }
            }else{
                //lose process
                executeFinishCommands(raid,raid.loseCommands);
            }

            //endgame process
            if(raid.currentGame < raid.scheduledGames-1){
                //has next
                raid.currentGame += 1;
                raid.setGameState(RaidState.PREPARATION);
                return;
            }
            //if last
            Man10Raid.api.endGame();

        });
    }

    @Override
    public void defineBossBar() {
        String title = "§c§l終了フェーズ §a§l残り§e§l{time}§a§l秒";
        this.bar = Bukkit.createBossBar(title, BarColor.WHITE, BarStyle.SOLID);
        timerTillNextState.linkBossBar(bar, true);
        timerTillNextState.addOnIntervalEvent(e -> bar.setTitle(title.replace("{time}", String.valueOf(e))));
    }

    @Override
    public void defineScoreboard() {
        scoreboard = new SScoreboard("TEST");
        scoreboard.setTitle("§c§lMan10Raid");
        scoreboard.setText(0, "§c§l終了フェーズ");
        timerTillNextState.addOnIntervalEvent(e -> {
            scoreboard.setText(2, "§a§l残り§e§l" + e + "§a§l秒");

            scoreboard.renderText();
        });
    }

//    @EventHandler
//    public void disableDamage(EntityDamageEvent e){
//        if(raid.currentGameState != RaidState.FINISH) return;
//        if(!(e.getEntity() instanceof Player)) return;
//        e.setCancelled(true);
//    }

    @Override
    public void cancel() {
        endAreaTimer.stop();
    }


}
