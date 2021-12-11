package com.shojabon.man10raid.Commands.SubCommands;

import com.shojabon.man10raid.Man10Raid;
import com.shojabon.mcutils.Utils.SItemStack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MD5TakeCommand implements CommandExecutor {
    Man10Raid plugin;

    public MD5TakeCommand(Man10Raid plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1){
            if(!(sender instanceof Player)) return false;
            Player p = ((Player)sender);
            if(p.getInventory().getItemInMainHand().getType() == Material.AIR){
                sender.sendMessage(Man10Raid.prefix + "§c§l手にアイテムを持ってください");
                return false;
            }
            SItemStack item = new SItemStack(p.getInventory().getItemInMainHand());
            Bukkit.getLogger().info(item.getItemTypeMD5());
            sender.sendMessage(Man10Raid.prefix + "§a§lアイテム情報をコンソールに出力しました");
            return false;
        }else if(args.length == 3){
            Player p = Bukkit.getPlayer(args[1]);
            if(p == null || !p.isOnline()) {
                sender.sendMessage(Man10Raid.prefix + "§c§lプレイヤーが存在しません");
                return false;
            }
            p.closeInventory(InventoryCloseEvent.Reason.PLAYER);

            ItemStack[] items = p.getInventory().getContents().clone();
            for(int i = 0; i < items.length; i++){
                if(items[i] == null || items[i].getType() == Material.AIR) continue;
                if(!new SItemStack(items[i]).getItemTypeMD5().equalsIgnoreCase(args[2])) continue;
                items[i] = new ItemStack(Material.AIR);
            }
            p.getInventory().setContents(items);

            ItemStack[] armorItem = p.getInventory().getArmorContents().clone();
            for(int i = 0; i < armorItem.length; i++){
                if(armorItem[i] == null || armorItem[i].getType() == Material.AIR) continue;
                if(!new SItemStack(armorItem[i]).getItemTypeMD5().equalsIgnoreCase(args[2])) continue;
                armorItem[i] = new ItemStack(Material.AIR);
            }
            p.getInventory().setArmorContents(armorItem);


            sender.sendMessage(Man10Raid.prefix + "§a§lアイテムが消去されました");
        }
        return true;
    }
}
