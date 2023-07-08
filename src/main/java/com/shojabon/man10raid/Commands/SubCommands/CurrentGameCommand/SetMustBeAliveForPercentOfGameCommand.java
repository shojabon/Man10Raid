package com.shojabon.man10raid.Commands.SubCommands.CurrentGameCommand;

import com.shojabon.man10raid.DataClass.RaidGame;
import com.shojabon.man10raid.Enums.RaidState;
import com.shojabon.man10raid.Man10Raid;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class SetMustBeAliveForPercentOfGameCommand implements CommandExecutor {
    Man10Raid plugin;

    public SetMustBeAliveForPercentOfGameCommand(Man10Raid plugin){
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
            raid.mustBeAliveForPercentOfGame = Float.parseFloat(args[2]);
            if(raid.mustBeAliveForPercentOfGame > 1){
                raid.mustBeAliveForPercentOfGame = 1;
            }
            if(raid.mustBeAliveForPercentOfGame < 0){
                raid.mustBeAliveForPercentOfGame = 0;
            }
        }catch (Exception e){
            e.printStackTrace();
            sender.sendMessage(Man10Raid.prefix + "§c§lパーセントを正しく設定してください");
            return true;
        }
        sender.sendMessage(Man10Raid.prefix + "§a§l勝利必に必要な加度を" + (raid.mustBeAliveForPercentOfGame * 100) + "%に設定しました");
        return true;
    }
}
