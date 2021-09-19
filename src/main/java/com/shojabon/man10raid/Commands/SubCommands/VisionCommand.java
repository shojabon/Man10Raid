package com.shojabon.man10raid.Commands.SubCommands;

import com.shojabon.man10raid.Man10Raid;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

public class VisionCommand implements CommandExecutor {
    Man10Raid plugin;

    public VisionCommand(Man10Raid plugin){
        this.plugin = plugin;
    }

    public static ArrayList<UUID> playerInVision = new ArrayList<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player p;
        if(args.length == 4){
            p = Bukkit.getPlayer(args[3]);
            if(p == null || !p.isOnline() || !p.getName().equals(args[3])){
                sender.sendMessage(Man10Raid.prefix + "§c§lプレイヤーが存在しません");
                return false;
            }
        }else {
            p = ((Player) sender);
        }

        if(playerInVision.contains(p.getUniqueId())){
            sender.sendMessage(Man10Raid.prefix + "§c§lプレイヤーはすでにエフェクト付与中です");
            return false;
        }
        playerInVision.add(p.getUniqueId());

        Monster view = null;


        if(args[2].equalsIgnoreCase("creeper")) view = (Creeper) p.getWorld().spawnEntity(p.getLocation(), EntityType.CREEPER);
        if(args[2].equalsIgnoreCase("enderman")) view = (Enderman) p.getWorld().spawnEntity(p.getLocation(), EntityType.ENDERMAN);
        if(args[2].equalsIgnoreCase("spider")) view = (Spider) p.getWorld().spawnEntity(p.getLocation(), EntityType.SPIDER);

        if(view == null) return false;

        if(view instanceof Creeper) {
            ((Creeper) view).setMaxFuseTicks(1000);
        };
        Monster finalView = view;
        finalView.setInvisible(true);
        finalView.setSilent(true);
        finalView.setInvulnerable(true);
        finalView.teleport(p.getLocation());

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Location pastLocation = p.getLocation();
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Vector directionVector = p.getLocation().subtract(pastLocation).toVector();
                if(directionVector.length() != 0) finalView.setVelocity(directionVector.normalize().multiply(Math.sqrt(pastLocation.distance(p.getLocation()))));                finalView.setAware(false);

                GameMode current = p.getGameMode();

                p.setGameMode(GameMode.SPECTATOR);
                p.setSpectatorTarget(finalView);

                Bukkit.getScheduler().runTaskLater(plugin, ()->{
                    p.setGameMode(current);
                    finalView.remove();
                    playerInVision.remove(p.getUniqueId());
                }, 20L *Integer.parseInt(args[1]));
            }, 2);
        }, 1);

        return true;
    }
}
