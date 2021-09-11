package com.shojabon.man10raid.DataClass;

import java.util.UUID;

public class RaidPlayer {

    double totalDamage = 0;
    String name;
    UUID uuid;

    public RaidPlayer(String name, UUID uuid){
        this.name = name;
        this.uuid = uuid;
    }


}
