package com.shojabon.man10raid.Commands.SubCommands.CurrentGameCommand;

import com.shojabon.man10raid.DataClass.RaidGame;
import com.shojabon.man10raid.Enums.RaidState;
import com.shojabon.man10raid.Man10Raid;
import com.shojabon.man10raid.Utils.BaseUtils;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetCurrentStateTimeCommand implements CommandExecutor {
    Man10Raid plugin;

    public SetCurrentStateTimeCommand(Man10Raid plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        RaidGame raid = Man10Raid.api.currentGame;
        if(raid == null){
            sender.sendMessage(Man10Raid.prefix + "§c§l現在試合が行われていません");
            return true;
        }
        if(!BaseUtils.isInt(args[2])){
            sender.sendMessage(Man10Raid.prefix + "§c§l時間は数字でなくてはなりません");
            return true;
        }
        raid.currentGameStateData.timerTillNextState.setRemainingTime(Integer.parseInt(args[2]));
        sender.sendMessage(Man10Raid.prefix + "§a§l時間を設定しました");
        return true;
    }
}
