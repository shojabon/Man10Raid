package com.shojabon.man10raid.DataClass;

import com.shojabon.mcutils.Utils.SItemStack;
import it.unimi.dsi.fastutil.Hash;
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
    public long prizeMoney = 0;

    public long aliveTime = 0;

    //inventory states
    public HashMap<String, Integer> inventoryState = new HashMap<>();
    public HashMap<String, Integer> armorState = new HashMap<>();
    private boolean inventoryStateSaved = false;

    public RaidPlayer(String name, UUID uuid){
        this.name = name;
        this.uuid = uuid;
    }

    public Player getPlayer(){
        return Bukkit.getServer().getPlayer(uuid);
    }



    //inventory functions


    public HashMap<String, Integer> createInventoryState(ItemStack[] items){
        HashMap<String, Integer> result = new HashMap<>();
        for(int i = 0; i < items.length; i++){
            if(items[i] == null) continue;
            ItemStack item = items[i].clone();
            String md5 = new SItemStack(item).getItemTypeMD5(false);
            if (!result.containsKey(md5)) {
                result.put(md5, 0);
            }
            result.put(md5, result.get(md5) + item.getAmount());
        }
        return result;
    }

    public void saveInventoryState(){
        Player p = getPlayer();
        if(p == null) return;
        if(!p.isOnline()) return;

        inventoryState = createInventoryState(p.getInventory().getContents());
        armorState = createInventoryState(p.getInventory().getArmorContents());
        inventoryStateSaved = true;
    }

    public boolean isSameInventoryState(){
        Player p = getPlayer();
        if(!p.isOnline()) return false;
        if(inventoryState == null || armorState == null) return true;
        if(!inventoryStateSaved) return true;

        HashMap<String, Integer> currentInventory = createInventoryState(p.getInventory().getContents());
        for(String key : currentInventory.keySet()){
            if(!inventoryState.containsKey(key)) return false;
            if(inventoryState.get(key) < currentInventory.get(key)) return false;
        }
        HashMap<String, Integer> currentArmor = createInventoryState(p.getInventory().getArmorContents());
        for(String key : currentArmor.keySet()){
            if(!armorState.containsKey(key)) return false;
            if(armorState.get(key) < currentArmor.get(key)) return false;
        }
        return true;

    }


}
