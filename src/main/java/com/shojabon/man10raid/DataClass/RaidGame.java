package com.shojabon.man10raid.DataClass;

import com.shojabon.man10raid.DataClass.States.FinishState;
import com.shojabon.man10raid.DataClass.States.InGameState;
import com.shojabon.man10raid.DataClass.States.PreparationState;
import com.shojabon.man10raid.DataClass.States.RegisteringState;
import com.shojabon.man10raid.Enums.RaidState;
import com.shojabon.man10raid.Man10Raid;
import com.shojabon.mcutils.Utils.MySQL.MySQLAPI;
import it.unimi.dsi.fastutil.Hash;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

public class RaidGame {

    Plugin plugin = Bukkit.getPluginManager().getPlugin("Man10Raid");

    public RaidState currentGameState = RaidState.INACTIVE;
    public RaidStateData currentGameStateData;

    public boolean won = false;


    // raid settings

    public int neededWinCommand = 1;
    public int executedWinCommandCount = 0;

    public UUID gameId;

    public String gameName;

    public int scheduledGames = 0;
    public int currentGame = 0;

    //time settings
    public int registrationTime = 0;
    public int preparationTime = 0;
    public int inGameTime = 0;
    public int endAreaTime = 0;

    //live time counter
    public int inGameTimeLeft = 0;

    //payout settings
    public double totalDamagePayoutMultiplier = 0;
    public double totalProjectileDamagePayoutMultiplier = 0;
    public double totalHealPayoutMultiplier = 0;
    public double totalFriendlyFirePayoutMultiplier = 0;



    //location settings
    public ArrayList<Location> playerSpawnPoints = new ArrayList<>();
    public Location endArea = null;
    public Location respawnLocation = null;

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
        this.gameName = name;
        scheduledGames = config.getInt("scheduledGames");

        //time settings
        registrationTime = config.getInt("time.registration");
        preparationTime = config.getInt("time.preparation");
        inGameTime = config.getInt("time.inGame");
        endAreaTime = config.getInt("time.endArea");

        //locations
        playerSpawnPoints = (ArrayList<Location>) config.getList("locations.playerSpawn", new ArrayList<Location>());
        respawnLocation = config.getLocation("locations.playerRespawn");
        endArea = config.getLocation("locations.endArea");

        //payout

        totalDamagePayoutMultiplier = config.getDouble("payout.totalDamage");
        totalProjectileDamagePayoutMultiplier = config.getDouble("payout.totalProjectileDamage");
        totalHealPayoutMultiplier = config.getDouble("payout.totalHeal");
        totalFriendlyFirePayoutMultiplier = config.getDouble("payout.totalFriendlyFire");


        //settings
        friendlyFire = config.getBoolean("settings.friendlyFire");
        revivesAllowed = config.getInt("settings.revivesAllowed");
        playersAllowed = config.getInt("settings.playersAllowed");
        minimumPlayersToBegin = config.getInt("settings.minimumPlayersToBegin");
        maxPlayersAllowed = config.getInt("settings.maxPlayersAllowed");
        neededWinCommand = config.getInt("settings.neededWinCommand");

        //load commands
        ConfigurationSection selection = config.getConfigurationSection("commands");
        if(selection == null) return;
        for(String key: selection.getKeys(false)){
            try{
                System.out.println(key);
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
        if(players.size()%playersAllowed != 0) maxGames++;

        if(maxGames > scheduledGames && scheduledGames != -1) maxGames = scheduledGames; //if maxGames bigger than scheduled games and not all player game
        if(scheduledGames > maxGames) scheduledGames = maxGames; // if scheduled games is too many games for the amount of players

        for(int game = 0; game < maxGames; game++){

            // if total player bigger than game size
            int playerPerGame = players.size();
            if(playerPerGame > playersAllowed) playerPerGame = playersAllowed;

            for(int i = 0; i < playerPerGame; i++){
                RaidPlayer player = players.get(registeredPlayers.get((game*playersAllowed) + i));
                player.registeredGame = game;
                player.livesLeft = revivesAllowed;

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

    public void setRespawnLocation(Location l){
        respawnLocation = l;
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

    public void removeOneLife(UUID uuid, boolean playerLeft){
        RaidPlayer deadPlayer = getPlayer(uuid);
        if(deadPlayer == null) return;
        if(deadPlayer.livesLeft <= 0) return;
        deadPlayer.livesLeft --;
        Player p = deadPlayer.getPlayer();
        if(p.isOnline() && !playerLeft){
            if(deadPlayer.livesLeft > 0) {
                //player still can play in arena
                if(respawnLocation == null){
                    //no respawn point
                    p.setBedSpawnLocation(playerSpawnPoints.get(0), true);
                }else{
                    p.setBedSpawnLocation(respawnLocation, true);
                }

                p.sendMessage("残りライフ" + deadPlayer.livesLeft);
            }else{
                //no respawns left
                p.setBedSpawnLocation(Man10Raid.lobbyLocation, true);
                p.sendMessage("あなたは死んだ");
            }
        }

        Bukkit.getScheduler().runTaskLater(plugin, ()->{
            //write if all dead function here
            if(allLivesLeftInCurrentGame() <= 0){
                //all dead (not counting players in different server and in lobby)
                Bukkit.broadcastMessage("全員死亡した");
                setGameState(RaidState.FINISH);
            }
        }, 20);
    }

    public int allLivesLeftInCurrentGame(){
        int total = 0;
        for(RaidPlayer player: getPlayersInGame(currentGame)){
            Player p = player.getPlayer();
            if(p == null) continue;
            if(!p.isOnline()) continue;
            if(!p.getLocation().getWorld().equals(playerSpawnPoints.get(0).getWorld())) continue;
            total += player.livesLeft;
        }
        return total;
    }

    public void teleportPlayerToArena(Player p){
        if(respawnLocation == null){
            p.teleport(playerSpawnPoints.get(0));
        }else{
            p.teleport(respawnLocation);
        }
    }

    public void payOutToPlayers(int game){
        ArrayList<RaidPlayer> players = getPlayersInGame(game);
        for(RaidPlayer player: players){
            if(!player.getPlayer().isOnline()) continue;
            long money = (long) (player.totalDamage * totalDamagePayoutMultiplier +
                                player.totalProjectileDamage * totalProjectileDamagePayoutMultiplier +
                                player.totalHeal * totalHealPayoutMultiplier +
                                player.totalFriendlyDamage * totalFriendlyFirePayoutMultiplier);
            player.prizeMoney = money;
            if(Man10Raid.vault.deposit(player.uuid, money)){
                player.paymentSuccess = true;
            }

        }
    }

    public void logPlayersInGame(int game){
        ArrayList<RaidPlayer> players = getPlayersInGame(game);
        ArrayList<HashMap<String, Object>> payloads = new ArrayList<>();

        for(RaidPlayer player: players){
            HashMap<String, Object> localPayload = new HashMap<>();
            localPayload.put("game_id", gameId);
            localPayload.put("game_registered_match", player.registeredGame);
            localPayload.put("name", player.name);
            localPayload.put("uuid", player.uuid);
            localPayload.put("total_damage", player.totalDamage);
            localPayload.put("total_friendly_damage", player.totalFriendlyDamage);
            localPayload.put("total_projectile_damage", player.totalProjectileDamage);
            localPayload.put("total_heal", player.totalHeal);
            localPayload.put("payment_amount", player.prizeMoney);
            localPayload.put("won", won);
            localPayload.put("payment_success", player.paymentSuccess);
            payloads.add(localPayload);
        }

        Man10Raid.mysql.futureExecute(MySQLAPI.buildInsertQuery(payloads, "raid_player_log"));
    }


    //ranking functions

    public static <K, V extends Comparable<V> > Map<K, V>
    valueSort(final Map<K, V> map)
    {
        Comparator<K> valueComparator = (k1, k2) -> {
            int comp = map.get(k1).compareTo(
                    map.get(k2));
            if (comp == 0)
                return 1;
            else
                return comp;
        };

        // SortedMap created using the comparator
        Map<K, V> sorted = new TreeMap<K, V>(valueComparator);

        sorted.putAll(map);

        return sorted;
    }


    public ArrayList<RaidPlayer> getTotalDamageRanking(int game){
        ArrayList<RaidPlayer> players = getPlayersInGame(game);
        HashMap<UUID, RaidPlayer> playerMap = new HashMap<>();

        ArrayList<RaidPlayer> result = new ArrayList<>();

        TreeMap<UUID, Long> valueMap = new TreeMap<>();
        for(RaidPlayer player: players) {
            playerMap.put(player.uuid, player);
            valueMap.put(player.uuid, player.totalDamage);
        }

        Map<UUID, Long> finalMap = valueSort(valueMap);

        for(UUID uuid: finalMap.keySet()){
            result.add(playerMap.get(uuid));
        }
        Collections.reverse(result);


        return result;
    }

    public ArrayList<RaidPlayer> getTotalProjectileDamageRanking(int game){
        ArrayList<RaidPlayer> players = getPlayersInGame(game);
        HashMap<UUID, RaidPlayer> playerMap = new HashMap<>();

        ArrayList<RaidPlayer> result = new ArrayList<>();

        TreeMap<UUID, Long> valueMap = new TreeMap<>();
        for(RaidPlayer player: players) {
            playerMap.put(player.uuid, player);
            valueMap.put(player.uuid, player.totalProjectileDamage);
        }

        Map<UUID, Long> finalMap = valueSort(valueMap);

        for(UUID uuid: finalMap.keySet()){
            result.add(playerMap.get(uuid));
        }
        Collections.reverse(result);


        return result;
    }

    public ArrayList<RaidPlayer> getTotalHealRanking(int game){
        ArrayList<RaidPlayer> players = getPlayersInGame(game);
        HashMap<UUID, RaidPlayer> playerMap = new HashMap<>();

        ArrayList<RaidPlayer> result = new ArrayList<>();

        TreeMap<UUID, Long> valueMap = new TreeMap<>();
        for(RaidPlayer player: players) {
            playerMap.put(player.uuid, player);
            valueMap.put(player.uuid, player.totalHeal);
        }

        Map<UUID, Long> finalMap = valueSort(valueMap);

        for(UUID uuid: finalMap.keySet()){
            result.add(playerMap.get(uuid));
        }
        Collections.reverse(result);


        return result;
    }

    //logging functions

    public void logCurrentMatch(){
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("game_id", gameId);
        payload.put("game_match", currentGame);
        payload.put("won", won);
        payload.put("game_time", (inGameTime - inGameTimeLeft));

        Man10Raid.mysql.futureExecute(MySQLAPI.buildInsertQuery(payload, "raid_game_log"));
    }





}
