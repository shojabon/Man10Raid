package com.shojabon.man10raid.Commands.SubCommands;

import com.shojabon.man10raid.DataClass.RaidGame;
import com.shojabon.man10raid.Enums.RaidState;
import com.shojabon.man10raid.Man10Raid;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

public class RegisterPlayerCommand implements CommandExecutor {
    Man10Raid plugin;

    public RegisterPlayerCommand(Man10Raid plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        //if game is not on going
        RaidGame raid = Man10Raid.api.currentGame;
        if(raid == null){
            sender.sendMessage(Man10Raid.prefix + "§c§l現在ゲームがありません");
            return false;
        }

        if(args.length == 1){
            // register self
            raid.registerPlayer(((Player)sender), false);
        }else{
            //if uuid
            if(args[1].length() >= 32){
                OfflinePlayer p = Bukkit.getServer().getOfflinePlayer(UUID.fromString(args[1]));
                if(!p.getUniqueId().toString().equals(args[1])){
                    sender.sendMessage(Man10Raid.prefix + "§c§lプレイヤーが存在しません");
                    return false;
                }
                raid.registerPlayer(UUID.fromString(args[1]), plugin.getName(), false);
                raid.preRegisterPlayer(UUID.fromString(args[1]), 1);
                return true;
            }
            // if register other
            Player targetPlayer = Bukkit.getServer().getPlayer(args[1]);
            if(targetPlayer == null || !targetPlayer.getName().equals(args[1])){
                sender.sendMessage(Man10Raid.prefix + "§c§lプレイヤーが存在しません");
                return false;
            }
            raid.registerPlayer(targetPlayer, false);
        }
        return true;
    }
}
