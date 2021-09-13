package com.shojabon.man10raid;

import com.shojabon.man10raid.DataClass.RaidGame;
import com.shojabon.man10raid.Enums.RaidState;
import com.shojabon.man10raid.Utils.BaseUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Man10RaidAPI {

    public RaidGame currentGame;

    public static HashMap<String, RaidGame> games = new HashMap<>();

    Man10Raid plugin;

    public Man10RaidAPI(Man10Raid plugin){
        this.plugin = plugin;
    }

    public boolean saveRaidGameConfig(RaidGame game){
        //path check
        File path = new File(plugin.getDataFolder() + File.separator + "games");
        if(!path.exists()) {
            if(!path.mkdirs()) return false;
        }

        YamlConfiguration config = new YamlConfiguration();
        // save data
        config.set("scheduledGames", game.scheduledGames);

        //time settings
        config.set("time.registration", game.registrationTime);
        config.set("time.preparation", game.preparationTime);
        config.set("time.inGame", game.inGameTime);
        config.set("time.endArea", game.endAreaTime);


        config.set("locations.playerSpawn", game.playerSpawnPoints);
        config.set("locations.endArea", game.endArea);
        config.set("settings.friendlyFire", game.friendlyFire);
        config.set("settings.revivesAllowed", game.revivesAllowed);
        config.set("settings.playersAllowed", game.playersAllowed);
        config.set("settings.maxPlayersAllowed", game.maxPlayersAllowed);
        config.set("settings.minimumPlayersToBegin", game.minimumPlayersToBegin);

        //file check
        File customConfigFile = new File(plugin.getDataFolder() + File.separator + "games", game.gameName + ".yml");
        try {
            config.save(customConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public RaidGame getRaidGame(String name){
        File customConfigFile = new File(plugin.getDataFolder() + File.separator + "games", name + ".yml");
        if(!customConfigFile.exists()) return null;

        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(customConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            return null;
        }

        RaidGame raid = new RaidGame(name, config);

        games.put(name, raid);
        return games.get(name);
    }

    //current game control

    public void endGame(){
        if(currentGame == null) return;
        currentGame.teleportAllPlayersToLobby();
        currentGame.setGameState(RaidState.INACTIVE);
        currentGame = null;
        Man10Raid.whitelist.disable();
    }

    public void cancelGame(){
        if(currentGame == null) return;
        currentGame.currentGameStateData.beforeCancel();
        currentGame.teleportAllPlayersToLobby();
        currentGame.setGameState(RaidState.INACTIVE);
        currentGame = null;
        Man10Raid.whitelist.disable();
    }

    //command script

    public void executeScript(ArrayList<String> script){
        Man10Raid.threadPool.submit(() -> {
           for(String code: script){
               String[] args = code.split(" ");

               //sleep function
               if(args[0].equalsIgnoreCase("sleep") && args.length == 2 && BaseUtils.isInt(args[1])){
                   try {
                       Thread.sleep(1000L * Integer.parseInt(args[1]));
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
                   continue;
               }

               //command function
               Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), code));
           }
        });
    }

    //clear cache

    public void clearCache(){
        games.clear();
    }


}
