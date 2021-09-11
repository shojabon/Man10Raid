package com.shojabon.man10raid.DataClass.States;

import com.shojabon.man10raid.DataClass.RaidGame;
import com.shojabon.man10raid.DataClass.RaidPlayer;
import com.shojabon.man10raid.DataClass.RaidStateData;
import com.shojabon.man10raid.Man10Raid;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class EndRegisterState extends RaidStateData {

    RaidGame raid = Man10Raid.api.currentGame;


    @Override
    public void start() {
        raid.currentGameTime = 0;
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


    @Override
    public void end() {
    }

    public void sendHighlightedMessage(Player p, String message){
        p.sendMessage("§e§l=================================");
        p.sendMessage("");
        p.sendMessage(message);
        p.sendMessage("");
        p.sendMessage("§e§l=================================");
    }


}
