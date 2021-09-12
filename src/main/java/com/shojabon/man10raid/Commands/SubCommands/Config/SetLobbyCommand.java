package com.shojabon.man10raid.Commands.SubCommands.Config;

import com.shojabon.man10raid.DataClass.RaidGame;
import com.shojabon.man10raid.Enums.RaidState;
import com.shojabon.man10raid.Man10Raid;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetLobbyCommand implements CommandExecutor {
    Man10Raid plugin;

    public SetLobbyCommand(Man10Raid plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage(Man10Raid.prefix + "§c§lこのコマンドはプレイヤーにのみ実行可能です");
            return false;
        }
        Man10Raid.config.set("lobbyLocation", ((Player) sender).getLocation());
        Man10Raid.lobbyLocation = ((Player) sender).getLocation();
        plugin.saveConfig();
        sender.sendMessage(Man10Raid.prefix + "§a§lロビーを設定しました");
        return true;
    }
}
