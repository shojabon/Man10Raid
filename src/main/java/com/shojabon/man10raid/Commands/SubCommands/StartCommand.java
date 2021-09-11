package com.shojabon.man10raid.Commands.SubCommands;

import com.shojabon.man10raid.DataClass.RaidGame;
import com.shojabon.man10raid.Enums.RaidState;
import com.shojabon.man10raid.Man10Raid;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class StartCommand implements CommandExecutor {
    Man10Raid plugin;

    public StartCommand(Man10Raid plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        RaidGame raid = Man10Raid.api.getRaidGame(args[1]);
        if(raid == null){
            sender.sendMessage(Man10Raid.prefix + "§c§lレイドプリセットが存在しません");
            return false;
        }
        if(Man10Raid.api.currentGame != null){
            sender.sendMessage(Man10Raid.prefix + "§c§l現在ゲームが進行中です");
            return false;
        }
        raid.gameId = UUID.randomUUID();
        Man10Raid.api.currentGame = raid;
        raid.changeGameState(RaidState.REGISTERING);
        sender.sendMessage(Man10Raid.prefix + "§a§l" + args[1] + "の予備登録を開始しました");
        return true;
    }
}