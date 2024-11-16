package com.shojabon.man10raid.DataClass;

import com.shojabon.mcutils.Utils.SScoreboard;
import com.shojabon.mcutils.Utils.STimer;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.UUID;

public abstract class RaidStateData implements Listener {

    Plugin plugin = Bukkit.getPluginManager().getPlugin("Man10Raid");


    //timer
    public STimer timerTillNextState = new STimer();
    public void defineTimer(){}

    //boss bar
    public BossBar bar = null;
    public void defineBossBar(){}

    //score board
    public SScoreboard scoreboard = null;
    public void defineScoreboard(){}

    //inner required start stop cancel functions

    void beforeStart(){
        defineTimer();
        defineBossBar();
        defineScoreboard();
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);

        //register boss bar
        if(bar != null){
            for(Player p:Bukkit.getServer().getOnlinePlayers()){
                this.bar.addPlayer(p);
            }
        }

        //register scoreboard
        if(scoreboard != null){
            for(Player p:Bukkit.getServer().getOnlinePlayers()){
                this.scoreboard.addPlayer(p);
            }
        }

        start();
    }
    void beforeEnd(){
        HandlerList.unregisterAll(this);
        timerTillNextState.stop();
        end();

        //remove bar
        if(bar != null){
            bar.removeAll();
            bar.setVisible(false);
            bar = null;
        }

        //scoreboard
        if(scoreboard != null) {
            scoreboard.remove();
            scoreboard = null;
        }
    }
    public void beforeCancel(){
        HandlerList.unregisterAll(this);
        timerTillNextState.stop();
        cancel();

        //remove bar
        if(bar != null){
            bar.removeAll();
            bar.setVisible(false);
            bar = null;
        }

        //scoreboard
        if(scoreboard != null) {
            scoreboard.remove();
            scoreboard = null;
        }
    }

    // interface start stop cancel functions

    public void start(){}
    public void end(){}
    public void cancel(){}


    //bar events
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        if(bar == null) return;
        bar.addPlayer(e.getPlayer());
        scoreboard.addPlayer(e.getPlayer());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e){
        if(bar == null) return;
        bar.addPlayer(e.getPlayer());
    }

    public void executeFinishCommands(RaidGame raid,ArrayList<String> commands){
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

            //weighted
            if(command.contains("<WEIGHTED-PLAYER-TOTAL-DAMAGE:")){
                String[] localCommand = command.split(" ");
                for(int i = 0; i < localCommand.length; i++) {
                    if(!localCommand[i].contains("<WEIGHTED-PLAYER-TOTAL-DAMAGE:")) continue;
                    int n = Integer.parseInt(localCommand[i].replace("<WEIGHTED-PLAYER-TOTAL-DAMAGE:", "").replace(">", ""));
                    ArrayList<RaidPlayer> players = getWeightedPlayers(raid,n);
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

    private ArrayList<RaidPlayer> getWeightedPlayers(RaidGame raid,int count){
        LinkedHashMap<Long, UUID> uuidRangeList = new LinkedHashMap<>();
        ArrayList<RaidPlayer> result = new ArrayList<>();
        long currentIndex = 0;

        for(RaidPlayer player: raid.getPlayersInGame(raid.currentGame)){
            if(player.totalDamage == 0) continue;
            if(player.livesLeft == 0) continue;
            currentIndex += player.totalDamage;
            uuidRangeList.put(currentIndex, player.uuid);
        }


        for(int i = 0; i < count; i++){
            if(currentIndex == 0) currentIndex++;
            long winner = new Random().nextInt((int) currentIndex);
            UUID winnerUUID = null;
            long key = -1;
            for(int ii = 0; ii < uuidRangeList.size(); ii++){
                long winnerKey = (long)uuidRangeList.keySet().toArray()[ii];
                if(winner > winnerKey) continue;
                winnerUUID = uuidRangeList.get(winnerKey);
                key = winnerKey;
                break;
            }
            if(key == -1) continue;
            result.add(raid.getPlayer(winnerUUID));
        }
        return result;
    }
}
