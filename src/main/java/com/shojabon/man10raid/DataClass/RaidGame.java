package com.shojabon.man10raid.DataClass;

import com.shojabon.man10raid.DataClass.States.FinishState;
import com.shojabon.man10raid.DataClass.States.InGameState;
import com.shojabon.man10raid.DataClass.States.PreparationState;
import com.shojabon.man10raid.DataClass.States.RegisteringState;
import com.shojabon.man10raid.Enums.RaidState;
import com.shojabon.man10raid.Man10Raid;
import com.shojabon.man10raid.Man10RaidAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class RaidGame {

    Plugin plugin = Bukkit.getPluginManager().getPlugin("Man10Raid");

    public RaidState currentGameState = RaidState.INACTIVE;
    public RaidStateData currentGameStateData;

    public boolean won = false;


    // raid settings

    public UUID gameId;

    public String gameName;

    public int scheduledGames = 0;
    public int currentGame = 0;

    //time settings
    public int registrationTime = 0;
    public int preparationTime = 0;
    public int inGameTime = 0;
    public int endAreaTime = 0;


    //location settings
    public ArrayList<Location> playerSpawnPoints = new ArrayList<>();
    public Location endArea = null;

    //game settings
    public boolean friendlyFire = false;
    public int revivesAllowed = 0;

    //player count settings
    public int playersAllowed = 50;
    public int minimumPlayersToBegin = 0;
    public int maxPlayersAllowed = 55;
    public HashMap<UUID, RaidPlayer> players = new HashMap<>();

    //commands
    public HashMap<RaidState, ArrayList<String>> commands = new HashMap<>();

    // constructors

    public RaidGame(){}

    public RaidGame(String name, FileConfiguration config){
        scheduledGames = config.getInt("scheduledGames");

        //time settings
        registrationTime = config.getInt("time.registration");
        preparationTime = config.getInt("time.preparation");
        inGameTime = config.getInt("time.inGame");
        endAreaTime = config.getInt("time.endArea");


        playerSpawnPoints = (ArrayList<Location>) config.getList("locations.playerSpawn", new ArrayList<Location>());
        endArea = config.getLocation("locations.endArea");
        friendlyFire = config.getBoolean("settings.friendlyFire");
        revivesAllowed = config.getInt("settings.revivesAllowed");
        playersAllowed = config.getInt("settings.playersAllowed");
        minimumPlayersToBegin = config.getInt("settings.minimumPlayersToBegin");
        maxPlayersAllowed = config.getInt("settings.maxPlayersAllowed");

        //load commands
        ConfigurationSection selection = config.getConfigurationSection("commands");
        if(selection == null) return;
        for(String key: selection.getKeys(false)){
            try{
                commands.put(RaidState.valueOf(key), new ArrayList<>(selection.getStringList(key)));
            }catch (Exception e){

            }
        }
    }

    // if game playable

    public int playable(){
        if(inGameTime < 0) return -1;
        if(scheduledGames == 0) return -2;
        if(playersAllowed <= 0) return -3;
        if(playerSpawnPoints.size() == 0) return -4;
        return 0;
    }


    // state functions

    public void setGameState(RaidState state){
        if(state == currentGameState) return;

        Bukkit.getScheduler().runTask(plugin, ()-> {
            currentGameState = state;
            //stop current state
            if(currentGameStateData != null){
                currentGameStateData.beforeEnd();
            }

            //start next state
            RaidStateData data = getStateData(state);
            if(data == null) return;
            data.beforeStart();
            //set current state data
            currentGameStateData = data;
            //execute commands
            if(!commands.containsKey(state)) return;
            Man10Raid.api.executeScript(commands.get(state));
        });
        return;
    }

    public RaidStateData getStateData(RaidState state){
        switch (state){
            case REGISTERING:
                return new RegisteringState();
            case PREPARATION:
                return new PreparationState();
            case IN_GAME:
                return new InGameState();
            case FINISH:
                return new FinishState();
        }
        return null;
    }

    //registration function

    public boolean registerPlayer(Player p, boolean bypass){
        if(currentGameState != RaidState.REGISTERING && !bypass){
            p.sendMessage(Man10Raid.prefix + "§c§l現在選手登録をすることはできません");
            return false;
        }
        if(players.containsKey(p.getUniqueId())) {
            p.sendMessage(Man10Raid.prefix + "§c§lあなたはすでに登録されています");
            return false;
        }
        players.put(p.getUniqueId(), new RaidPlayer(p.getName(), p.getUniqueId()));
        p.sendMessage(Man10Raid.prefix + "§a§l登録しました");
        return true;
    }

    public void dividePlayers(){
        ArrayList<UUID> registeredPlayers = new ArrayList<>(players.keySet());
        Collections.shuffle(registeredPlayers);
        if(playersAllowed == 0) return;


        int maxGames = players.size()/playersAllowed;

        if(maxGames > scheduledGames && scheduledGames != -1) maxGames = scheduledGames; //if maxGames bigger than scheduled games and not all player game

        for(int game = 0; game < maxGames; game++){

            // if total player bigger than game size
            int playerPerGame = players.size();
            if(playerPerGame > playersAllowed) playerPerGame = playersAllowed;

            for(int i = 0; i < playerPerGame; i++){
                RaidPlayer player = players.get(registeredPlayers.get((game*playersAllowed) + i));
                player.registeredGame = game;

                //set whitelist message
                Man10Raid.whitelist.setKickMessages(player.uuid, "あなたは" + (game + 1)  + "試合目です");
            }
        }
    }

    public ArrayList<RaidPlayer> getPlayersInGame(int gameNumber){
        ArrayList<RaidPlayer> result = new ArrayList<>();
        for(RaidPlayer player: players.values()){
            if(player.registeredGame == gameNumber) result.add(player);
        }
        return result;
    }

    //set settings functions
    //location point

    public void addPlayerSpawnPoint(Location l){
        playerSpawnPoints.add(l);
        Man10Raid.api.saveRaidGameConfig(this);
    }

    public void setEndAreaPoint(Location l){
        endArea = l;
        Man10Raid.api.saveRaidGameConfig(this);
    }

    //time

    public void setRegistrationTime(int time){
        registrationTime = time;
        Man10Raid.api.saveRaidGameConfig(this);
    }

    public void setPreparationTime(int time){
        preparationTime = time;
        Man10Raid.api.saveRaidGameConfig(this);
    }

    public void setInGameTime(int time){
        inGameTime = time;
        Man10Raid.api.saveRaidGameConfig(this);
    }

    public void setEndAreaTime(int time){
        endAreaTime = time;
        Man10Raid.api.saveRaidGameConfig(this);
    }

    //player functions

    public RaidPlayer getPlayer(UUID uuid){
        if(!players.containsKey(uuid)){
            return null;
        }
        return players.get(uuid);
    }

    public void teleportAllPlayersToLobby(){
        Bukkit.getServer().getScheduler().runTask(plugin, ()->{
            for(RaidPlayer player: players.values()){
                if(player.getPlayer() != null && player.getPlayer().isOnline()){
                    player.getPlayer().teleport(Man10Raid.lobbyLocation);
                }
            }
        });
    }






}
