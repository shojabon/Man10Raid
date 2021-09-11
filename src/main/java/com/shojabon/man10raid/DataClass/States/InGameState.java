package com.shojabon.man10raid.DataClass.States;

import com.shojabon.man10raid.DataClass.RaidGame;
import com.shojabon.man10raid.DataClass.RaidPlayer;
import com.shojabon.man10raid.DataClass.RaidStateData;
import com.shojabon.man10raid.Enums.RaidState;
import com.shojabon.man10raid.Man10Raid;
import com.shojabon.man10raid.Man10RaidAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Random;

public class InGameState extends RaidStateData {

    RaidGame raid = Man10Raid.api.currentGame;


    @Override
    public void start() {
        //if no spawn points
        if(raid.playerSpawnPoints.size() == 0){
            Bukkit.getServer().broadcastMessage(Man10Raid.prefix + "スポーンポイントを発見することができませんでした");
            Man10Raid.api.endGame();
            return;
        }

        movePlayersToArena();
        timerTillNextState.start();
    }


    @Override
    public void end() {
    }
    @Override
    public void defineTimer(){
        timerTillNextState.setRemainingTime(raid.inGameTime);
        timerTillNextState.addOnIntervalEvent(e -> {
            Bukkit.getServer().broadcastMessage(e + " seconds remaining");
        });
        timerTillNextState.addOnEndEvent(() -> {
            Bukkit.getServer().broadcastMessage("end!");

        });
    }

    public void movePlayersToArena(){
        for(RaidPlayer player: raid.getPlayersInGame(raid.currentGame)){
            int spawnIndex = new Random().nextInt(raid.playerSpawnPoints.size());
            player.getPlayer().teleport(raid.playerSpawnPoints.get(spawnIndex));
        }
    }



}
