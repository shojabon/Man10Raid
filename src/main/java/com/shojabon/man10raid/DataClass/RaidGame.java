package com.shojabon.man10raid.DataClass;

import com.shojabon.man10raid.Enums.RaidState;
import com.shojabon.man10raid.Man10Raid;
import com.shojabon.man10raid.Utils.BaseUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class RaidGame {

    public RaidState currentGameState = RaidState.INACTIVE;


    // raid settings

    public String gameName;

    public int scheduledGames = 0;
    public int currentGame = 0;

    public int gameTime = 0;
    public int currentGameTime = 0;

    public HashMap<RaidState, List<String>> commandList = new HashMap<>();

    public ArrayList<Location> playerSpawnPoints = new ArrayList<>();
    public Location endArea = null;

    public boolean friendlyFire = false;

    public int revivesAllowed = 0;


    public int playersAllowed = 50;
    public int maxPlayersAllowed = 55;
    public HashMap<UUID, RaidPlayer> players = new HashMap<>();


    public void changeGameState(RaidState state){
        if(state == currentGameState) return;

        currentGameState = state;
        List<String> commands = commandList.get(state);
        Man10Raid.threadPool.submit(()-> {
            for(String command: commands){

                //if sleep
                String[] splitCommand = command.split(" ");
                if(splitCommand[0].equalsIgnoreCase("sleep")){
                    if(splitCommand.length != 2) continue;
                    if(!BaseUtils.isInt(splitCommand[1])) continue;
                    try {
                        Thread.sleep(Integer.parseInt(splitCommand[1]));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                Bukkit.getServer().dispatchCommand( Bukkit.getServer().getConsoleSender(), command);
            }
        });
    }





}
