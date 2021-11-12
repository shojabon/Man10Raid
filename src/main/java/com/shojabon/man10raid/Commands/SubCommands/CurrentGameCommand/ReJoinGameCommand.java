package com.shojabon.man10raid.Commands.SubCommands.CurrentGameCommand;

import com.shojabon.man10raid.DataClass.RaidGame;
import com.shojabon.man10raid.DataClass.RaidPlayer;
import com.shojabon.man10raid.Enums.RaidState;
import com.shojabon.man10raid.Man10Raid;
import com.shojabon.man10raid.utils.PlayerInventoryViewerMenu;
import com.shojabon.mcutils.Utils.SInventory.SInventory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReJoinGameCommand implements CommandExecutor {
    Man10Raid plugin;

    public ReJoinGameCommand(Man10Raid plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        RaidGame raid = Man10Raid.api.currentGame;
        if(!(sender instanceof Player)){
            sender.sendMessage(Man10Raid.prefix + "§c§lこのコマンドはプレイヤーからのみ実行可能です");
            return true;
        }
        if(raid == null){
            sender.sendMessage(Man10Raid.prefix + "§c§l現在試合が行われていません");
            return true;
        }
        if(raid.currentGameState != RaidState.IN_GAME){
            sender.sendMessage(Man10Raid.prefix + "§c§l現在試合中ではありません");
            return true;
        }
        RaidPlayer player = raid.getPlayer(((Player)sender).getUniqueId());
        if(player == null){
            sender.sendMessage(Man10Raid.prefix + "§c§lあなたは選手ではありません");
            return true;
        }
        if(player.registeredGame != raid.currentGame){
            sender.sendMessage(Man10Raid.prefix + "§c§lこの試合の選手ではありません");
            return true;
        }
        if(player.livesLeft <= 0){
            sender.sendMessage(Man10Raid.prefix + "§c§lライフがありません");
            return true;
        }
        if(!player.isSameInventoryState()){
            sender.sendMessage(Man10Raid.prefix + "§c§lインベントリの状態が同じではありません");
            PlayerInventoryViewerMenu inventory = new PlayerInventoryViewerMenu("§c§lインベントリをこの状態にしてください", plugin);
            inventory.setInventoryContents(player.inventoryState);
            inventory.setArmorContents(player.armorState);
            inventory.open(player.getPlayer());
            return true;
        }

        raid.teleportPlayerToArena(player.getPlayer());

        return true;
    }
}
