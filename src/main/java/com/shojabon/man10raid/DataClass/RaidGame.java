package com.shojabon.man10raid.DataClass;

import com.shojabon.man10raid.DataClass.States.RegisteringState;
import com.shojabon.man10raid.Enums.RaidState;
import com.shojabon.man10raid.Man10Raid;
import com.shojabon.man10raid.Utils.BaseUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class RaidGame {

    public RaidState currentGameState = RaidState.INACTIVE;
    public RaidStateData currentGameStateData;


    // raid settings

    public UUID gameId;

    public String gameName;

    public int scheduledGames = 0;
    public int currentGame = 0;

    public int gameTime = 0;
    public int currentGameTime = 0;

    public ArrayList<Location> playerSpawnPoints = new ArrayList<>();
    public Location endArea = null;

    public boolean friendlyFire = false;

    public int revivesAllowed = 0;


    public int playersAllowed = 50;
    public int maxPlayersAllowed = 55;
    public HashMap<UUID, RaidPlayer> players = new HashMap<>();


    public boolean changeGameState(RaidState state){
        if(state == currentGameState) return true;

        currentGameState = state;
        //stop current state
        if(currentGameStateData != null){
            currentGameStateData.stop();
        }

        //start next state
        RaidStateData data = getStateData(state);
        if(data == null) return true;
        data.start();
        //set current state data
        currentGameStateData = data;
        return true;
    }

    public RaidStateData getStateData(RaidState state){
        switch (state){
            case REGISTERING:
                return new RegisteringState();
        }
        return null;
    }

    public RaidGame(){}

    public RaidGame(String name, FileConfiguration config){
        scheduledGames = config.getInt("scheduledGames");
        gameTime = config.getInt("gameTime");
        playerSpawnPoints = (ArrayList<Location>) config.getList("locations.playerSpawn", new ArrayList<Location>());
        endArea = config.getLocation("locations.endArea");
        friendlyFire = config.getBoolean("settings.friendlyFire");
        revivesAllowed = config.getInt("settings.revivesAllowed");
        playersAllowed = config.getInt("settings.playersAllowed");
        maxPlayersAllowed = config.getInt("settings.maxPlayersAllowed");
    }





}
