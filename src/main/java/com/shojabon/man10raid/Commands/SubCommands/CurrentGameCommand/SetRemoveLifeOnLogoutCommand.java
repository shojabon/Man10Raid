package com.shojabon.man10raid.Commands.SubCommands.CurrentGameCommand;

import com.shojabon.man10raid.DataClass.RaidGame;
import com.shojabon.man10raid.Enums.RaidState;
import com.shojabon.man10raid.Man10Raid;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class SetRemoveLifeOnLogoutCommand implements CommandExecutor {
    Man10Raid plugin;

    public SetRemoveLifeOnLogoutCommand(Man10Raid plugin){
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
            raid.removeLifeOnLogout = Boolean.parseBoolean(args[2]);
        }catch (Exception e){
            sender.sendMessage(Man10Raid.prefix + "§c§l引数が正しくありません");
            return false;
        }
        sender.sendMessage(Man10Raid.prefix + "§a§l状態をセットしました");
        return true;
    }
}
