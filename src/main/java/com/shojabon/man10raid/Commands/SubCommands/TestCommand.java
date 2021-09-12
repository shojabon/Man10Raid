package com.shojabon.man10raid.Commands.SubCommands;

import com.shojabon.man10raid.DataClass.RaidGame;
import com.shojabon.man10raid.DataClass.RaidPlayer;
import com.shojabon.man10raid.Enums.RaidState;
import com.shojabon.man10raid.Man10Raid;
import com.shojabon.man10raid.Utils.SItemStack;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TestCommand implements CommandExecutor {
    Man10Raid plugin;

    public TestCommand(Man10Raid plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player p = ((Player) sender);
        p.getInventory().setItemInMainHand(new SItemStack(p.getInventory().getItemInMainHand()).setGlowingEffect(true).build());
        RaidGame raid = Man10Raid.api.currentGame;
        RaidPlayer player = raid.getPlayer(((Player)sender).getUniqueId());
        sender.sendMessage(player.totalDamage + " " + player.totalProjectileDamage + " " + player.totalFriendlyDamage + " " + player.totalHeal);
        return true;
    }
}
