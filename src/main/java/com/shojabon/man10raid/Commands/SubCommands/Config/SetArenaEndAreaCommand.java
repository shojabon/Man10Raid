package com.shojabon.man10raid.Commands.SubCommands.Config;

import com.shojabon.man10raid.DataClass.RaidGame;
import com.shojabon.man10raid.Man10Raid;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetArenaEndAreaCommand implements CommandExecutor {
    Man10Raid plugin;

    public SetArenaEndAreaCommand(Man10Raid plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage(Man10Raid.prefix + "§c§lこのコマンドはプレイヤーにのみ実行可能です");
            return false;
        }
        RaidGame raid = Man10Raid.api.getRaidGame(args[1]);
        if(raid == null){
            sender.sendMessage(Man10Raid.prefix + "§c§lアリーナが存在しません");
            return false;
        }
        raid.setEndAreaPoint(((Player) sender).getLocation());
        Man10Raid.api.saveRaidGameConfig(raid);
        sender.sendMessage(Man10Raid.prefix + "§a§lエンドエリアを設定をしました");
        return true;
    }
}
