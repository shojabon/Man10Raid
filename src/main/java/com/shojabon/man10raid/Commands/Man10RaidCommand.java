package com.shojabon.man10raid.Commands;


import com.shojabon.man10raid.Commands.SubCommands.RegisterPlayerCommand;
import com.shojabon.man10raid.Commands.SubCommands.SetCurrentGameState;
import com.shojabon.man10raid.Commands.SubCommands.StartCommand;
import com.shojabon.man10raid.Commands.SubCommands.TestCommand;
import com.shojabon.man10raid.Man10Raid;
import com.shojabon.man10raid.Utils.SCommandRouter.SCommandArgument;
import com.shojabon.man10raid.Utils.SCommandRouter.SCommandArgumentType;
import com.shojabon.man10raid.Utils.SCommandRouter.SCommandObject;
import com.shojabon.man10raid.Utils.SCommandRouter.SCommandRouter;

import java.util.Arrays;

public class Man10RaidCommand extends SCommandRouter {

    Man10Raid plugin;

    public Man10RaidCommand(Man10Raid plugin){
        this.plugin = plugin;
        registerCommands();
        registerEvents();
        pluginPrefix = Man10Raid.prefix;
    }

    public void registerEvents(){
        setNoPermissionEvent(e -> e.sender.sendMessage(Man10Raid.prefix + "§c§lあなたは権限がありません"));
        setOnNoCommandFoundEvent(e -> e.sender.sendMessage(Man10Raid.prefix + "§c§lコマンドが存在しません"));
    }

    public void registerCommands(){
        //start game command
        addCommand(
                new SCommandObject()
                        .addArgument(new SCommandArgument().addAllowedString("start")).
                        addArgument(new SCommandArgument().addAlias("ゲームコンフィグ名")).
                        addRequiredPermission("man10raid.start").addExplanation("レイドを開始する").
                        setExecutor(new StartCommand(plugin))
        );

        //register player command

        addCommand(
                new SCommandObject()
                        .addArgument(new SCommandArgument().addAllowedString("register")).
                        addRequiredPermission("man10raid.register").addExplanation("レイド選手登録をする").
                        setExecutor(new RegisterPlayerCommand(plugin))
        );

        addCommand(
                new SCommandObject()
                        .addArgument(new SCommandArgument().addAllowedString("register"))
                        .addArgument(new SCommandArgument().addAllowedType(SCommandArgumentType.ONLINE_PLAYER).addAlias("プレイヤー名")).

                        addRequiredPermission("man10raid.register.other").addExplanation("レイド選手登録をする").
                        setExecutor(new RegisterPlayerCommand(plugin))
        );

        //test
        addCommand(
                new SCommandObject()
                        .addArgument(new SCommandArgument().addAllowedString("test")).

                        addRequiredPermission("man10raid.test").addExplanation("テストコマンド").
                        setExecutor(new TestCommand(plugin))
        );

        addCommand(
                new SCommandObject()
                        .addArgument(new SCommandArgument().addAllowedString("setstate")).
                        addArgument(new SCommandArgument().addAlias("状態")).

                        addRequiredPermission("man10raid.test").addExplanation("試合の状態を設定する").
                        setExecutor(new SetCurrentGameState(plugin))
        );

    }

}
