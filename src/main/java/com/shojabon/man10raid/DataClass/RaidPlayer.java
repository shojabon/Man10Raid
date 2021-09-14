package com.shojabon.man10raid.DataClass;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class RaidPlayer {

    //statistics
    //current game damage counters
    public long totalDamage = 0;
    public long totalFriendlyDamage = 0;
    public long totalProjectileDamage = 0;
    public long totalHeal = 0;

    public String name;
    public UUID uuid;
    public int registeredGame = -1;
    public int livesLeft = 0;

    public boolean paymentSuccess = false;

    //inventory states
    public HashMap<Integer, ItemStack> inventoryState = new HashMap<>();
    public HashMap<Integer, ItemStack> armorState = new HashMap<>();

    public RaidPlayer(String name, UUID uuid){
        this.name = name;
        this.uuid = uuid;
    }

    public Player getPlayer(){
        return Bukkit.getServer().getPlayer(uuid);
    }



    //inventory functions


    public HashMap<Integer, ItemStack> createInventoryState(ItemStack[] items){
        HashMap<Integer, ItemStack> result = new HashMap<>();
        for(int i = 0; i < items.length; i++){
            if(items[i] == null) continue;
            result.put(i, items[i]);
        }
        return result;
    }

    public void saveInventoryState(){
        Player p = getPlayer();
        if(!p.isOnline()) return;

        inventoryState = createInventoryState(p.getInventory().getContents());
        armorState = createInventoryState(p.getInventory().getArmorContents());
    }

    public boolean isSameInventoryState(){
        Player p = getPlayer();
        if(!p.isOnline()) return false;
        System.out.println(inventoryState.size());
        System.out.println(armorState.size());

        if(!inventoryState.equals(createInventoryState(p.getInventory().getContents()))) return false;
        if(!armorState.equals(createInventoryState(p.getInventory().getArmorContents()))) return false;
        return true;

    }

}
