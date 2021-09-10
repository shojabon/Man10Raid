package com.shojabon.man10raid;

import com.shojabon.man10raid.DataClass.RaidGame;

import java.util.HashMap;

public class Man10RaidAPI {

    public RaidGame currentGame;

    public static HashMap<String, RaidGame> games = new HashMap<>();

    Man10Raid plugin;

    public Man10RaidAPI(Man10Raid plugin){
        this.plugin = plugin;
    }


}
