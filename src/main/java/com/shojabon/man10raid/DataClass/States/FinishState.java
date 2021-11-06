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
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class FinishState extends RaidStateData {

    RaidGame raid = Man10Raid.api.currentGame;

    STimer endAreaTimer = new STimer();

    Plugin plugin = Bukkit.getPluginManager().getPlugin("Man10Raid");

    public void executeFinishCommands(ArrayList<String> commands){
        for(String command: commands){
            // players alive and dead and all
            if(command.contains("<PLAYER-ALIVE>")){
                for(RaidPlayer player : raid.getPlayersInGame(raid.currentGame)){
                    String localCommand = command;
                    if(player.livesLeft != 0) localCommand = localCommand.replaceAll("<PLAYER-ALIVE>", player.name);
                    String finalLocalCommand = localCommand;
                    Bukkit.getScheduler().runTask(plugin, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalLocalCommand));
                }
            }
            if(command.contains("<PLAYER-DEAD>")){
                for(RaidPlayer player : raid.getPlayersInGame(raid.currentGame)){
                    String localCommand = command;
                    if(player.livesLeft == 0) localCommand = localCommand.replaceAll("<PLAYER-DEAD>", player.name);
                    String finalLocalCommand = localCommand;
                    Bukkit.getScheduler().runTask(plugin, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalLocalCommand));
                }
            }
            if(command.contains("<PLAYER>")){
                for(RaidPlayer player : raid.getPlayersInGame(raid.currentGame)){
                    String localCommand = command;
                    localCommand = localCommand.replaceAll("<PLAYER>", player.name);
                    String finalLocalCommand = localCommand;
                    Bukkit.getScheduler().runTask(plugin, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalLocalCommand));
                }
            }

            //top n damage
            if(command.contains("<PLAYER-TOTAL-DAMAGE-TOP:")){
                String[] localCommand = command.split(" ");
                for(int i = 0; i < localCommand.length; i++) {
                    if(!localCommand[i].contains("<PLAYER-TOTAL-DAMAGE-TOP:")) continue;
                    int n = Integer.parseInt(localCommand[i].replace("<PLAYER-TOTAL-DAMAGE-TOP:", "").replace(">", ""));
                    ArrayList<RaidPlayer> players = raid.getTotalDamageRanking(raid.currentGame);
                    if(n > players.size()) n = players.size();
                    for(int ii = 0; ii < n; ii++){
                        localCommand[i] = players.get(ii).name;

                        StringBuilder finalCommand = new StringBuilder();
                        for(String elem : localCommand) finalCommand.append(elem).append(" ");
                        Bukkit.getScheduler().runTask(plugin, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand.substring(0, finalCommand.length()-1)));
                    }


                }
            }
        }
    }

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
        timerTillNextState.setRemainingTime(10);
        timerTillNextState.addOnEndEvent(() -> {
            if(raid.won){
                //win process
                executeFinishCommands(raid.winCommands);
                if(raid.endArea != null){
                    //if end area exists
                    raid.setGameState(RaidState.CONGRATULATIONS);
                    return;
                }
            }else{
                //lose process
                executeFinishCommands(raid.loseCommands);
                Bukkit.getServer().broadcastMessage("敗北");
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
