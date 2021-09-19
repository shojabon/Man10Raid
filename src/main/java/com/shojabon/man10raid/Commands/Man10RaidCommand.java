package com.shojabon.man10raid.Commands;


import com.shojabon.man10raid.Commands.SubCommands.*;
import com.shojabon.man10raid.Commands.SubCommands.Config.AddArenaPlayerSpawnPointCommand;
import com.shojabon.man10raid.Commands.SubCommands.Config.CreateNewPreset;
import com.shojabon.man10raid.Commands.SubCommands.Config.SetArenaPlayerRespawnPointCommand;
import com.shojabon.man10raid.Commands.SubCommands.Config.SetLobbyCommand;
import com.shojabon.man10raid.Commands.SubCommands.CurrentGameCommand.*;
import com.shojabon.man10raid.Man10Raid;
import com.shojabon.man10raid.Utils.SCommandRouter.SCommandArgument;
import com.shojabon.man10raid.Utils.SCommandRouter.SCommandArgumentType;
import com.shojabon.man10raid.Utils.SCommandRouter.SCommandObject;
import com.shojabon.man10raid.Utils.SCommandRouter.SCommandRouter;

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

        addCommand(
                new SCommandObject()
                        .addArgument(new SCommandArgument().addAllowedString("reload")).
                        addRequiredPermission("man10raid.reload").addExplanation("コンフィグをリロードする").
                        setExecutor(new ReloadCommand(plugin))
        );

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
                        .addArgument(new SCommandArgument().addAllowedString("test"))
                        .addArgument(new SCommandArgument().addAlias("name"))
                        .addArgument(new SCommandArgument().addAlias("server"))

                        .addRequiredPermission("man10raid.test").addExplanation("テストコマンド").
                        setExecutor(new TestCommand(plugin))
        );


        //vision command

        addCommand(
                new SCommandObject()
                        .addArgument(new SCommandArgument().addAllowedString("vision"))
                        .addArgument(new SCommandArgument().addAlias("秒").addAllowedType(SCommandArgumentType.INT))
                        .addArgument(new SCommandArgument().addAlias("モブタイプ")
                                .addAllowedString("creeper")
                                .addAllowedString("spider")
                                .addAllowedString("enderman")
                        )

                        .addRequiredPermission("man10raid.vision").addExplanation("vision").
                        setExecutor(new VisionCommand(plugin))
        );

        addCommand(
                new SCommandObject()
                        .addArgument(new SCommandArgument().addAllowedString("vision"))
                        .addArgument(new SCommandArgument().addAlias("秒").addAllowedType(SCommandArgumentType.INT))
                        .addArgument(new SCommandArgument().addAlias("モブタイプ")
                                .addAllowedString("creeper")
                                .addAllowedString("spider")
                                .addAllowedString("enderman")
                        )
                        .addArgument(new SCommandArgument().addAlias("プレイヤー名").addAllowedType(SCommandArgumentType.ONLINE_PLAYER))
                        .addRequiredPermission("man10raid.vision").addExplanation("vision").
                        setExecutor(new VisionCommand(plugin))
        );

        addCommand(
                new SCommandObject()
                        .addArgument(new SCommandArgument().addAllowedString("radiusExecute"))
                        .addArgument(new SCommandArgument().addAlias("モブUUID"))
                        .addArgument(new SCommandArgument().addAlias("半径").addAllowedType(SCommandArgumentType.INT))
                        .setInfinity()
                        .addRequiredPermission("man10raid.radiusExecute").addExplanation("指定モブの範囲内のプレイヤーの名前を代入してコマンドを発行\n<name> プレイヤー名\n<uuid> プレイヤーUUID").
                        setExecutor(new RadiusExecute(plugin))
        );

        //===========================
        //
        //   arena configuration functions
        //
        //===========================

        //create new preset

        addCommand(
                new SCommandObject()
                        .addArgument(new SCommandArgument().addAllowedString("create")).
                        addArgument(new SCommandArgument().addAlias("アリーナ名")).
                        addRequiredPermission("man10raid.settings.create").addExplanation("新たにプリセットを作成する").
                        setExecutor(new CreateNewPreset(plugin))
        );


        //set lobby location
        addCommand(
                new SCommandObject()
                        .addArgument(new SCommandArgument().addAllowedString("setlobby")).
                        addRequiredPermission("man10raid.test").addExplanation("ロビーの位置を設定する").
                        setExecutor(new SetLobbyCommand(plugin))
        );

        //add arena location

        addCommand(
                new SCommandObject()
                        .addArgument(new SCommandArgument().addAllowedString("setting")).
                        addArgument(new SCommandArgument().addAlias("アリーナ名")).
                        addArgument(new SCommandArgument().addAllowedString("addSpawn")).
                        addRequiredPermission("man10raid.settings.arena.playerspawn").addExplanation("アリーナのスポーンポイントを設定する").
                        setExecutor(new AddArenaPlayerSpawnPointCommand(plugin))
        );

        //add player respawn point

        addCommand(
                new SCommandObject()
                        .addArgument(new SCommandArgument().addAllowedString("setting")).
                        addArgument(new SCommandArgument().addAlias("アリーナ名")).
                        addArgument(new SCommandArgument().addAllowedString("setRespawn")).
                        addRequiredPermission("man10raid.settings.arena.playerRespawn").addExplanation("アリーナのスポーンポイントを設定する").
                        setExecutor(new SetArenaPlayerRespawnPointCommand(plugin))
        );



        //===========================
        //
        //   current game functions
        //
        //===========================

        addCommand(
                new SCommandObject()
                        .addArgument(new SCommandArgument().addAllowedString("current"))
                        .addArgument(new SCommandArgument().addAllowedString("setState"))
                        .addArgument(new SCommandArgument().addAlias("状態")
                                .addAllowedString("REGISTERING")
                                .addAllowedString("PREPARATION")
                                .addAllowedString("IN_GAME")
                                .addAllowedString("FINISH")).

                        addRequiredPermission("man10raid.current.state").addExplanation("試合の状態を設定する").
                        setExecutor(new SetCurrentGameStateCommand(plugin))
        );


        addCommand(
                new SCommandObject()
                        .addArgument(new SCommandArgument().addAllowedString("current"))
                        .addArgument(new SCommandArgument().addAllowedString("win")).
                        addRequiredPermission("man10raid.current.win").addExplanation("試合を勝利にする").
                        setExecutor(new WinCommand(plugin))
        );

        addCommand(
                new SCommandObject()
                        .addArgument(new SCommandArgument().addAllowedString("current"))
                        .addArgument(new SCommandArgument().addAllowedString("lose")).
                        addRequiredPermission("man10raid.current.lose").addExplanation("試合を敗北にする").
                        setExecutor(new LoseCommand(plugin))
        );

        addCommand(
                new SCommandObject()
                        .addArgument(new SCommandArgument().addAllowedString("rejoin"))


                        .addRequiredPermission("man10raid.current.rejoin").addExplanation("試合に再参加する").
                        setExecutor(new ReJoinGameCommand(plugin))
        );

        addCommand(
                new SCommandObject()
                        .addArgument(new SCommandArgument().addAllowedString("cancel"))

                        .addRequiredPermission("man10raid.cancel").addExplanation("試合をキャンセルする").
                        setExecutor(new EndCurrentGameCommand(plugin))
        );

        //extend time
        addCommand(
                new SCommandObject()
                        .addArgument(new SCommandArgument().addAllowedString("current"))
                        .addArgument(new SCommandArgument().addAllowedString("setTime"))
                        .addArgument(new SCommandArgument().addAllowedType(SCommandArgumentType.INT))

                        .addRequiredPermission("man10raid.current.time.extend").addExplanation("試合時間を延長する").
                        setExecutor(new SetCurrentStateTimeCommand(plugin))
        );
    }

}
