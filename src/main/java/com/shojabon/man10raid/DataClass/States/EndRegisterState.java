package com.shojabon.man10raid.DataClass.States;

import com.shojabon.man10raid.DataClass.RaidGame;
import com.shojabon.man10raid.DataClass.RaidPlayer;
import com.shojabon.man10raid.DataClass.RaidStateData;
import com.shojabon.man10raid.Man10Raid;
import com.shojabon.man10raid.Utils.STimer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class EndRegisterState extends RaidStateData {

    RaidGame raid = Man10Raid.api.currentGame;
    STimer timerTillBegin = new STimer();


    @Override
    public void start() {
        defineTimer();
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

        timerTillBegin.start();
    }


    @Override
    public void end() {
    }


    public void defineTimer(){
        timerTillBegin.setRemainingTime(60);
        timerTillBegin.addOnIntervalEvent(e -> {
            Bukkit.getServer().broadcastMessage(e + " seconds remainng");
        });
        timerTillBegin.addOnEndEvent(() -> {
            Bukkit.getServer().broadcastMessage("end!");
        });
    }

    public void sendHighlightedMessage(Player p, String message){
        p.sendMessage("§e§l=================================");
        p.sendMessage("");
        p.sendMessage(message);
        p.sendMessage("");
        p.sendMessage("§e§l=================================");
    }


}
