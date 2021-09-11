package com.shojabon.man10raid.Commands.SubCommands;

import com.shojabon.man10raid.DataClass.RaidGame;
import com.shojabon.man10raid.Man10Raid;
import com.shojabon.man10raid.Utils.MySQL.ThreadedMySQLAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {
    Man10Raid plugin;

    public ReloadCommand(Man10Raid plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Man10Raid.api.cancelGame();
        plugin.reloadConfig();
        Man10Raid.config = plugin.getConfig();

        Man10Raid.api.clearCache();

        Man10Raid.mysql = new ThreadedMySQLAPI(plugin);
        Man10Raid.prefix = Man10Raid.config.getString("prefix");

        sender.sendMessage(Man10Raid.prefix + "§a§lプラグインがリロードされました");
        return true;
    }
}
