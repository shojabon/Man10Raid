package com.shojabon.man10raid.Commands.SubCommands.Config;

import com.shojabon.man10raid.DataClass.RaidGame;
import com.shojabon.man10raid.Enums.RaidState;
import com.shojabon.man10raid.Man10Raid;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.util.UUID;

public class CreateNewPreset implements CommandExecutor {
    Man10Raid plugin;

    public CreateNewPreset(Man10Raid plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        RaidGame raid = Man10Raid.api.getRaidGame(args[1]);
        if(raid != null){
            sender.sendMessage(Man10Raid.prefix + "§c§lレイドプリセットが存在します");
            return false;
        }
        File file = new File(plugin.getDataFolder() + File.separator + "games" + File.separator + args[1] + ".yml");
        if(file.exists()){
            sender.sendMessage(Man10Raid.prefix + "§c§lレイドプリセットが存在します");
            return false;
        }

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }


        try{
            InputStream input = plugin.getResource("TestGamePreset.yml");
            OutputStream output = new FileOutputStream(file);
            int DEFAULT_BUFFER_SIZE = 1024 * 4;
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int size;
            while (-1 != (size = input.read(buffer))) {
                output.write(buffer, 0, size);
            }
            input.close();
            output.close();
        }catch (Exception e){
            sender.sendMessage(Man10Raid.prefix + "§c§l内部エラーが発生しました");
            return false;
        }


        return true;
    }
}
