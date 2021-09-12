package com.shojabon.man10raid.DataClass.States;

import com.shojabon.man10raid.DataClass.RaidGame;
import com.shojabon.man10raid.DataClass.RaidStateData;
import com.shojabon.man10raid.Enums.RaidState;
import com.shojabon.man10raid.Man10Raid;
import com.shojabon.man10raid.Utils.STimer;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public class FinishState extends RaidStateData {

    RaidGame raid = Man10Raid.api.currentGame;

    STimer endAreaTimer = new STimer();



    @Override
    public void start() {
        timerTillNextState.start();
        Bukkit.getServer().broadcastMessage("started finish state");

    }

    @Override
    public void end() {
        endAreaTimer.stop();
        Bukkit.getServer().broadcastMessage("end finish state");
    }

    @Override
    public void defineTimer(){
        timerTillNextState.setRemainingTime(10);
        timerTillNextState.addOnEndEvent(() -> {
            if(raid.won){
                //win process
                Bukkit.getServer().broadcastMessage("勝利");
                if(raid.endArea != null){
                    //if end area exists
                    endAreaProcess();
                    return;
                }
            }else{
                //lose process
                Bukkit.getServer().broadcastMessage("敗北");
            }

            //final
            endGameProcess();

        });
    }

    @Override
    public void defineBossBar() {
        this.bar = Bukkit.createBossBar("終了フェーズ {time}秒", BarColor.WHITE, BarStyle.SOLID);
        timerTillNextState.linkBossBar(bar, true);
    }

    //winner area
    public void endAreaProcess(){
        endAreaTimer.setRemainingTime(raid.endAreaTime);
        endAreaTimer.addOnEndEvent(()->{
            Bukkit.getServer().broadcastMessage("end area終了");
            endGameProcess();
        });
        endAreaTimer.start();
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
