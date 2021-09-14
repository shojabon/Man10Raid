package com.shojabon.man10raid.DataClass.States;

import com.shojabon.man10raid.DataClass.RaidGame;
import com.shojabon.man10raid.DataClass.RaidPlayer;
import com.shojabon.man10raid.DataClass.RaidStateData;
import com.shojabon.man10raid.Enums.RaidState;
import com.shojabon.man10raid.Man10Raid;
import com.shojabon.man10raid.Man10RaidAPI;
import com.shojabon.man10raid.Utils.SScoreboard;
import com.shojabon.man10raid.Utils.STimer;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.UUID;

public class PreparationState extends RaidStateData {

    RaidGame raid = Man10Raid.api.currentGame;
    Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("Man10Raid");

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
                    Man10RaidAPI.sendHighlightedMessage(player.getPlayer(), "§c§l当選しました！\n" + "あなたの試合は" + (player.registeredGame + 1) + "試合目です");
                }else{
                    //no match
                    Man10RaidAPI.sendHighlightedMessage(player.getPlayer(), "§b§l残念ながら本日の試合は落選しました");
                }
            }
        }

        //if not enough players
        if(raid.getPlayersInGame(raid.currentGame).size() < raid.minimumPlayersToBegin || raid.getPlayersInGame(raid.currentGame).size() == 0){
            Man10RaidAPI.broadcastHighlightedMessage("§b§l参加者が不足しています");
            Man10Raid.api.endGame();
            return;
        }



        //start whitelist
        Man10Raid.whitelist.clearPlayers();
        for(RaidPlayer player: raid.getPlayersInGame(raid.currentGame)){
            Man10Raid.whitelist.addPlayer(player.uuid);
        }
        Man10Raid.whitelist.enable();

        movePlayersToServers();

        Bukkit.getScheduler().runTaskLater(plugin,()-> Man10RaidAPI.broadcastHighlightedMessage("§a§l準備フェーズ開始\n出場者は準備を開始してください"), 20*3);

        timerTillNextState.start(); //start count down
    }

    public void movePlayersToServers(){
        ArrayList<RaidPlayer> currentGamePlayers = raid.getPlayersInGame(raid.currentGame);
        ArrayList<UUID> currentGamePlayerUUIDs = new ArrayList<>();
        for(RaidPlayer player: currentGamePlayers) currentGamePlayerUUIDs.add(player.uuid);

        //kick players
        for(Player p: Bukkit.getServer().getOnlinePlayers()){
            if(currentGamePlayerUUIDs.contains(p.getUniqueId())) continue;
            if(p.hasPermission("man10raid.whitelist.bypass")) continue;
            Man10Raid.api.sendPlayerToServer(p.getName(), Man10Raid.config.getString("servers.main"));
        }

        //move players to raid server
        for(RaidPlayer player: currentGamePlayers){
            Man10Raid.api.sendPlayerToServer(player.name, Man10Raid.config.getString("servers.raid"));
        }

    }


    @Override
    public void end() {
    }

    @Override
    public void defineTimer(){
        timerTillNextState.setRemainingTime(raid.preparationTime);
        timerTillNextState.addOnEndEvent(() -> {
            raid.setGameState(RaidState.IN_GAME);
        });
    }

    @Override
    public void defineBossBar() {
        String title = "§c§l選手準備フェーズ §a§l残り§e§l{time}§a§l秒";
        this.bar = Bukkit.createBossBar("", BarColor.WHITE, BarStyle.SOLID);
        timerTillNextState.linkBossBar(bar, true);
        timerTillNextState.addOnIntervalEvent(e -> bar.setTitle(title.replace("{time}", String.valueOf(e))));
    }

    @Override
    public void defineScoreboard() {
        scoreboard = new SScoreboard("TEST");
        scoreboard.setTitle("§4§lMan10Raid");
        scoreboard.setText(0, "§c§l選手準備中");
        timerTillNextState.addOnIntervalEvent(e -> {
            scoreboard.setText(2, "§a§l残り§e§l" + e + "§a§l秒");
            scoreboard.renderText();
        });
    }

}
