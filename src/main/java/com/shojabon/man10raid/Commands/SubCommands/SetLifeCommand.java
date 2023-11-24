package com.shojabon.man10raid.Commands.SubCommands;

import com.shojabon.man10raid.DataClass.RaidGame;
import com.shojabon.man10raid.Enums.RaidState;
import com.shojabon.man10raid.Man10Raid;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetLifeCommand implements CommandExecutor {
    Man10Raid plugin;

    public SetLifeCommand(Man10Raid plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        RaidGame raid = Man10Raid.api.currentGame;
        if(raid == null){
            sender.sendMessage(Man10Raid.prefix + "§c§l現在試合が行われていません");
            return true;
        }
        try{
            Player targetPlayer = Bukkit.getPlayer(args[2]);
            if(targetPlayer == null || !targetPlayer.getName().equalsIgnoreCase(args[2])){
                sender.sendMessage(Man10Raid.prefix + "§c§lプレイヤーが見つかりませんでした");
                return true;
            }
            if(raid.getPlayer(targetPlayer.getUniqueId()) == null){
                sender.sendMessage(Man10Raid.prefix + "§c§lプレイヤーが見つかりませんでした");
                return true;
            }
            // print current lives if no args
            if(args.length == 3){
                sender.sendMessage(Man10Raid.prefix + "§a§l" + targetPlayer.getName() + "の残機数: " + raid.getPlayer(targetPlayer.getUniqueId()).livesLeft);
            }else{
                raid.getPlayer(targetPlayer.getUniqueId()).livesLeft = Integer.parseInt(args[3]);
                if (raid.getPlayer(targetPlayer.getUniqueId()).livesLeft <= 0) {
                    raid.getPlayer(targetPlayer.getUniqueId()).livesLeft = 0;
                }
            }
        }catch (Exception e){
            sender.sendMessage(Man10Raid.prefix + "§c§l/mraid current setLife <プレイヤー名> <残機数>");
            return true;
        }
        sender.sendMessage(Man10Raid.prefix + "§a§l状態をセットしました");
        return true;
    }
}
