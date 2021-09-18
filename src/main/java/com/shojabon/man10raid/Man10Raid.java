package com.shojabon.man10raid;

import com.shojabon.man10raid.Commands.Man10RaidCommand;
import com.shojabon.man10raid.Commands.SubCommands.VisionCommand;
import com.shojabon.man10raid.DataClass.RaidGame;
import com.shojabon.man10raid.DataClass.RaidPlayer;
import com.shojabon.man10raid.Utils.MySQL.ThreadedMySQLAPI;
import com.shojabon.man10raid.Utils.SInventory.SInventory;
import com.shojabon.man10raid.Utils.STimer;
import com.shojabon.man10raid.Utils.SWhiteList;
import com.shojabon.man10raid.Utils.VaultAPI;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class Man10Raid extends JavaPlugin implements @NotNull Listener {

    public static ExecutorService threadPool = Executors.newCachedThreadPool();
    public static Man10RaidAPI api;
    public static String prefix;
    public static ThreadedMySQLAPI mysql;

    public static FileConfiguration config;

    public static Location lobbyLocation;

    public static SWhiteList whitelist;

    public static VaultAPI vault;


    @Override
    public void onEnable() {
        saveDefaultConfig();
        // Plugin startup logic
        api = new Man10RaidAPI(this);
        //

        config = getConfig();
        lobbyLocation = config.getLocation("lobbyLocation");
        prefix = config.getString("prefix");

        mysql = new ThreadedMySQLAPI(this);
        createTables();
        whitelist = new SWhiteList(this, "このサーバーに参加できません");
        whitelist.setBypassPermission("man10raid.whitelist.bypass");
        Man10RaidCommand command = new Man10RaidCommand(this);
        getCommand("mraid").setExecutor(command);
        getCommand("mraid").setTabCompleter(command);

        vault = new VaultAPI();

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        STimer.pluginEnabled = false;
        api.endGame();
        whitelist.disable();
        SInventory.closeAllSInventories();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        e.getPlayer().teleport(Man10Raid.lobbyLocation);
    }

    @EventHandler
    public void getOutOfSpectator(PlayerToggleSneakEvent e){
        if(!VisionCommand.playerInVision.contains(e.getPlayer().getUniqueId())) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onLeaveSpectator(PlayerQuitEvent e){
        if(!VisionCommand.playerInVision.contains(e.getPlayer().getUniqueId())) return;
        e.getPlayer().setGameMode(GameMode.SURVIVAL);
    }

    public void createTables(){
        mysql.futureExecute("CREATE TABLE IF NOT EXISTS `raid_player_log` (\n" +
                "\t`id` INT(10) NOT NULL AUTO_INCREMENT,\n" +
                "\t`game_id` VARCHAR(50) NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',\n" +
                "\t`game_registered_match` INT(10) NULL DEFAULT NULL,\n" +
                "\t`name` VARCHAR(50) NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',\n" +
                "\t`uuid` VARCHAR(50) NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',\n" +
                "\t`total_damage` BIGINT(19) NULL DEFAULT NULL,\n" +
                "\t`total_friendly_damage` BIGINT(19) NULL DEFAULT NULL,\n" +
                "\t`total_projectile_damage` BIGINT(19) NULL DEFAULT NULL,\n" +
                "\t`total_heal` BIGINT(19) NULL DEFAULT NULL,\n" +
                "\t`payment_amount` BIGINT(19) NULL DEFAULT NULL,\n" +
                "\t`won` TINYTEXT NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',\n" +
                "\t`payment_success` TINYTEXT NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',\n" +
                "\t`date_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                "\tPRIMARY KEY (`id`) USING BTREE\n" +
                ")\n" +
                "COLLATE='utf8mb4_0900_ai_ci'\n" +
                "ENGINE=InnoDB\n" +
                ";\n");

        mysql.futureExecute("CREATE TABLE IF NOT EXISTS `raid_game_log` (\n" +
                "\t`id` INT(10) NOT NULL AUTO_INCREMENT,\n" +
                "\t`game_id` VARCHAR(50) NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',\n" +
                "\t`game_match` INT(10) NULL DEFAULT NULL,\n" +
                "\t`game_time` INT(10) NULL DEFAULT NULL,\n" +
                "\t`won` TINYTEXT NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',\n" +
                "\t`date_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                "\tPRIMARY KEY (`id`) USING BTREE\n" +
                ")\n" +
                "COLLATE='utf8mb4_0900_ai_ci'\n" +
                "ENGINE=InnoDB\n" +
                ";\n");
    }
//
//    @EventHandler
//    public void test(InventoryClickEvent e){
//        if(e.getSlotType() != InventoryType.SlotType.ARMOR && e.getRawSlot() != 5) return;
//        if(e.getCursor() == null) return;
//        if(e.getCursor().getType() == Material.AIR) return;
//        e.setCancelled(true);
//
//        ItemStack[] items = e.getWhoClicked().getInventory().getArmorContents();
//        ArrayUtils.reverse(items);
//
//        ItemStack originalItem = items[e.getRawSlot()-5];
//        items[e.getRawSlot()-5] = e.getCursor().clone();
//        ArrayUtils.reverse(items);
//
//        if(originalItem == null) originalItem = new ItemStack(Material.AIR);
//        e.getWhoClicked().setItemOnCursor(originalItem.clone());
//        e.getWhoClicked().getInventory().setArmorContents(items.clone());
//
//
//    }

}
