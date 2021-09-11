package com.shojabon.man10raid;

import com.shojabon.man10raid.DataClass.RaidGame;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
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
        config.set("gameTime", game.gameTime);
        config.set("locations.playerSpawn", game.playerSpawnPoints);
        config.set("locations.endArea", game.endArea);
        config.set("settings.friendlyFire", game.friendlyFire);
        config.set("settings.revivesAllowed", game.revivesAllowed);
        config.set("settings.playersAllowed", game.playersAllowed);
        config.set("settings.maxPlayersAllowed", game.maxPlayersAllowed);

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


}
