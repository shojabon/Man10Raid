package com.shojabon.man10raid.Commands.SubCommands;

import com.shojabon.man10raid.Man10Raid;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.jetbrains.annotations.NotNull;

public class VisionCommand implements CommandExecutor {
    Man10Raid plugin;

    public VisionCommand(Man10Raid plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player p = ((Player) sender);

        Monster view = null;
        if(args[1].equalsIgnoreCase("creeper")) view = (Creeper) p.getWorld().spawnEntity(p.getLocation(), EntityType.CREEPER);
        if(args[1].equalsIgnoreCase("enderman")) view = (Enderman) p.getWorld().spawnEntity(p.getLocation(), EntityType.ENDERMAN);
        if(args[1].equalsIgnoreCase("spider")) view = (Spider) p.getWorld().spawnEntity(p.getLocation(), EntityType.SPIDER);

        if(view == null) return false;

        view.setAI(false);
        view.setInvisible(true);

        GameMode current = p.getGameMode();


        p.setGameMode(GameMode.SPECTATOR);
        p.setSpectatorTarget(view);

        Monster finalView = view;
        Bukkit.getScheduler().runTaskLater(plugin, ()->{
            finalView.remove();
            p.setGameMode(current);
        }, 20*3);

        return true;
    }
}
