package com.shojabon.man10raid.DataClass.States;

import com.shojabon.man10raid.DataClass.RaidGame;
import com.shojabon.man10raid.DataClass.RaidPlayer;
import com.shojabon.man10raid.DataClass.RaidStateData;
import com.shojabon.man10raid.Enums.RaidState;
import com.shojabon.man10raid.Man10Raid;
import com.shojabon.man10raid.Man10RaidAPI;
import com.shojabon.man10raid.Utils.SScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class InGameState extends RaidStateData {

    RaidGame raid = Man10Raid.api.currentGame;
    Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("Man10Raid");

    //statistics
    long totalDamage = 0;
    long totalHeal = 0;
    long totalProjectileDamage = 0;
    long totalFriendlyFire = 0;


    @Override
    public void start() {
        //if no spawn points
        if(raid.playerSpawnPoints.size() == 0){
            Bukkit.getServer().broadcastMessage(Man10Raid.prefix + "スポーンポイントを発見することができませんでした");
            Man10Raid.api.endGame();
            return;
        }

        Bukkit.getScheduler().runTask(plugin, this::movePlayersToArena);
        timerTillNextState.start();
    }


    @Override
    public void end() {
    }

    @Override
    public void defineTimer(){
        timerTillNextState.setRemainingTime(raid.inGameTime);

        ArrayList<RaidPlayer> players = raid.getPlayersInGame(raid.currentGame);

        //action bar
        timerTillNextState.addOnIntervalEvent(e -> {
            for(RaidPlayer player: players){
                Player p = player.getPlayer();
                if(p == null) continue;
                if(!p.isOnline()) continue;
                if(player.livesLeft == 0) continue;
                p.sendActionBar("総合ダメージ:" + player.totalDamage + " 総合回復:" + player.totalHeal + " 総合矢攻撃:" + player.totalProjectileDamage + " 残りライフ:" + player.livesLeft);
            }
        });
        timerTillNextState.addOnEndEvent(() -> {
            Bukkit.getServer().broadcastMessage("end!");
            endGame();
        });
    }

    @Override
    public void defineBossBar() {
        this.bar = Bukkit.createBossBar("ゲーム終了まで 残り{time}秒", BarColor.WHITE, BarStyle.SOLID);
        timerTillNextState.linkBossBar(bar, true);
    }

    @Override
    public void defineScoreboard() {
        scoreboard = new SScoreboard("TEST");
        scoreboard.setTitle("試合中!!");
        ArrayList<RaidPlayer> players = raid.getPlayersInGame(raid.currentGame);
        timerTillNextState.addOnIntervalEvent(e -> {
            scoreboard.setText(0, "残り" + e + "秒");
            scoreboard.setText(1, "総合ダメージ量:" + totalDamage);
            scoreboard.setText(2, "総合回復量:" + totalHeal);
            scoreboard.setText(3, "総合弓ダメージ" + totalProjectileDamage);
            scoreboard.setText(3, "総ライフ数: " + raid.allLivesLeftInCurrentGame());
        });
    }


    public void endGame(){
        raid.setGameState(RaidState.FINISH);
    }

    public void movePlayersToArena(){
        for(RaidPlayer player: raid.getPlayersInGame(raid.currentGame)){
            int spawnIndex = new Random().nextInt(raid.playerSpawnPoints.size());
            player.getPlayer().teleport(raid.playerSpawnPoints.get(spawnIndex));
        }
    }

    //damage functions

    @EventHandler
    public void countDamage(EntityDamageByEntityEvent e){
        if(e.isCancelled()) return;
        if(!(e.getDamager() instanceof Player)) return;

        Player origin = ((Player) e.getDamager());
        RaidPlayer originPlayer = raid.getPlayer(origin.getUniqueId());
        if(originPlayer == null) return;

        //if dead
        if(originPlayer.livesLeft == 0) return;

        //if friendly fire
        if(raid.friendlyFire && e.getEntity() instanceof Player){
            originPlayer.totalFriendlyDamage += e.getDamage();

            totalFriendlyFire += e.getDamage();
            return;
        }

        //if friendly fire off and damaged is a player
        if(e.getEntity() instanceof Player){
            e.setCancelled(true);
            return;
        }

        //if other damage
        originPlayer.totalDamage += e.getDamage();

        totalDamage += e.getDamage();
    }

    //arrow damage
    @EventHandler
    public void arrowDamage(EntityDamageByEntityEvent e){
        if(e.isCancelled()) return;
        if(!(e.getDamager() instanceof Arrow)) return;
        Arrow originProjectile = (Arrow) e.getDamager();
        if(!(originProjectile.getShooter() instanceof Player)) return;

        // if friendly fire stop
        if(!raid.friendlyFire && e.getEntity() instanceof Player){
            e.setCancelled(true);
            return;
        }


        RaidPlayer originPlayer = raid.getPlayer(((Player) originProjectile.getShooter()).getUniqueId());
        if(originPlayer == null) return;

        //if dead
        if(originPlayer.livesLeft == 0) return;


        originPlayer.totalProjectileDamage += e.getDamage();
        originPlayer.totalDamage += e.getDamage();

        totalProjectileDamage += e.getDamage();
        totalDamage += e.getDamage();
    }

    //heal calculation

    //thrown potion owner(potion uuid, owner uuid)
    HashMap<UUID, UUID> potionOwner = new HashMap<>();

    //received effect origin(affected uuid, potion owner uuid)
    HashMap<UUID, UUID> effectOwner = new HashMap<>();


    @EventHandler
    public void onThrow(ProjectileLaunchEvent e){
        if(!(e.getEntity().getShooter() instanceof Player)) return;
        if(e.getEntity().getType() != EntityType.SPLASH_POTION) return;
        potionOwner.put(e.getEntity().getUniqueId(), ((Player) e.getEntity().getShooter()).getUniqueId());
    }

    @EventHandler
    public void onPotionTakeEffect(PotionSplashEvent e){
        if(!potionOwner.containsKey(e.getEntity().getUniqueId())) return;
        for(LivingEntity effectedEntity: e.getAffectedEntities()){
            if(!(effectedEntity instanceof Player)) continue;
            effectOwner.put(effectedEntity.getUniqueId(), potionOwner.get(e.getEntity().getUniqueId()));
        }
        potionOwner.remove(e.getEntity().getUniqueId());
    }

    @EventHandler
    public void onRegainHealth(EntityRegainHealthEvent e){
        if(!(e.getEntity() instanceof Player)) return;
        if(e.getRegainReason() != EntityRegainHealthEvent.RegainReason.MAGIC)return;
        if(!effectOwner.containsKey(e.getEntity().getUniqueId())) return;

        UUID originUUID = effectOwner.get(e.getEntity().getUniqueId());
        effectOwner.remove(e.getEntity().getUniqueId());

        RaidPlayer originPlayer = raid.getPlayer(originUUID);
        if(originPlayer == null) return;

        //if dead
        if(originPlayer.livesLeft == 0) return;

        originPlayer.totalHeal += e.getAmount();
        totalHeal += e.getAmount();
    }

    //on death

    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        raid.removeOneLife(e.getEntity().getUniqueId(), false);
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent e){
        RaidPlayer originPlayer = raid.getPlayer(e.getPlayer().getUniqueId());
        if(originPlayer == null) return;
        originPlayer.saveInventoryState();
        raid.removeOneLife(e.getPlayer().getUniqueId(), true);
    }

}