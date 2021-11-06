package com.shojabon.man10raid.Commands.SubCommands;

import com.shojabon.man10raid.Man10Raid;
import com.shojabon.mcutils.Utils.SItemStack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Base64GiveCommand implements CommandExecutor {
    Man10Raid plugin;

    public Base64GiveCommand(Man10Raid plugin){
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
            Bukkit.getLogger().info(item.getBase64());
            sender.sendMessage(Man10Raid.prefix + "§a§lアイテム情報をコンソールに出力しました");
            return false;
        }else if(args.length == 3){
            Player p = Bukkit.getPlayer(args[1]);
            if(p == null || !p.isOnline()) {
                sender.sendMessage(Man10Raid.prefix + "§c§lプレイヤーが存在しません");
                return false;
            }
            p.getInventory().addItem(SItemStack.fromBase64(args[2]).build());
            sender.sendMessage(Man10Raid.prefix + "§a§lアイテムが付与されました");
        }
        return true;
    }
}
