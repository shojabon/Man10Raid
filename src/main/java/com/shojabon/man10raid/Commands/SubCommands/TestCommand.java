package com.shojabon.man10raid.Commands.SubCommands;

import com.shojabon.man10raid.Man10Raid;
import com.shojabon.man10raid.Utils.SItemStack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class TestCommand implements CommandExecutor {
    Man10Raid plugin;

    public TestCommand(Man10Raid plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player p = ((Player) sender);

        ItemStack item  = new ItemStack(Material.DIAMOND_SWORD);

        ItemMeta meta = item.getItemMeta();
        meta.setUnbreakable(true);

        item.setItemMeta(meta);
        p.getInventory().addItem(new SItemStack(item).setDamage(10000).build());
        return true;
    }
}
