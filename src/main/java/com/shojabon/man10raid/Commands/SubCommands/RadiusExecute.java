package com.shojabon.man10raid.Commands.SubCommands;

import com.shojabon.man10raid.Man10Raid;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class RadiusExecute implements CommandExecutor {
    Man10Raid plugin;

    public RadiusExecute(Man10Raid plugin){
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        //mraid radiusExecute <mobuuid> <r> command <player> <uuid>
        try{
            World w = Bukkit.getWorld(Objects.requireNonNull(Man10Raid.config.getString("arenaWorld")));
            if(w == null) return false;
            Entity entity = w.getEntity(UUID.fromString(args[1]));
            if(entity == null) return false;
            List<Entity> entityList = entity.getNearbyEntities(Integer.parseInt(args[2]), Integer.parseInt(args[2]), Integer.parseInt(args[2]));

            StringBuilder finalCommand = new StringBuilder();
            for(int i = 3; i < args.length; i++){
                finalCommand.append(args[i]).append(" ");
            }

            for(Entity ent: entityList){
                if(!(ent instanceof Player)) continue;
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand.toString().replace("<name>", ent.getName()).replace("<uuid>", ent.getUniqueId().toString()));
            }
        }catch (Exception e){
            sender.sendMessage(Man10Raid.prefix + "§c§l内部エラーが発生しました");
            return false;
        }
        return true;
    }
}
