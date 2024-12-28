package com.shojabon.man10raid.DataClass;

import com.shojabon.mcutils.Utils.SItemStack;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
//    public HashMap<String, Integer> armorState = new HashMap<>();
    private boolean inventoryStateSaved = false;

    public RaidPlayer(String name, UUID uuid){
        this.name = name;
        this.uuid = uuid;
    }

    public Player getPlayer(){
        return Bukkit.getServer().getPlayer(uuid);
    }



    //inventory functions


//    public HashMap<String, Integer> createInventoryState(ItemStack[] items){
//        HashMap<String, Integer> result = new HashMap<>();
//        for(int i = 0; i < items.length; i++){
//            if(items[i] == null) continue;
//            ItemStack item = items[i].clone();
//            String md5 = new SItemStack(item).getItemTypeMD5(false);
//            if (!result.containsKey(md5)) {
//                result.put(md5, 0);
//            }
//            result.put(md5, result.get(md5) + item.getAmount());
//        }
//        return result;
//    }

    public HashMap<String,Integer> createPlayerInventoryState(Player player){
        HashMap<String,Integer> result=new HashMap<>();
        ItemStack[] inv=player.getInventory().getContents();
        for(int i=0;i<inv.length;i++){
            if(inv[i]==null)continue;
            ItemStack item=inv[i].clone();
            String md5=new SItemStack(item).getItemTypeMD5(false);
            if(!result.containsKey(md5)){
                result.put(md5,0);
            }
            result.put(md5,result.get(md5)+item.getAmount());
        }
        return result;
    }

    public void saveInventoryState(){
        Player p = getPlayer();
        if(p == null) return;
        if(!p.isOnline()) return;

        inventoryState = createPlayerInventoryState(p);
//        armorState = createInventoryState(p.getInventory().getArmorContents());
        inventoryStateSaved = true;
    }

    public boolean isSameInventoryState(){
        Player p = getPlayer();
        if(!p.isOnline()) {
            System.out.println("オンラインでない");
            return false;
        }
        if(inventoryState == null) return true;
        if(!inventoryStateSaved) return true;

        HashMap<String, Integer> currentInventory = createPlayerInventoryState(p);
        for(String key : currentInventory.keySet()){
            if(!inventoryState.containsKey(key)) {
                System.out.println("保存されていないアイテムが存在,"+p.name());
                return false;
            }
            if(inventoryState.get(key) < currentInventory.get(key)) {
                System.out.println("数が多い,"+p.name());
                return false;
            }
        }
//        HashMap<String, Integer> currentArmor = createInventoryState(p.getInventory().getArmorContents());
//        for(String key : currentArmor.keySet()){
//            if(!armorState.containsKey(key)&&!inventoryState.containsKey(key)) {
//                System.out.println("アーマーに保存されていないアイテムが存在,"+p.name()+","+ Arrays.toString(p.getInventory().getArmorContents()));
//                return false;
//            }
//            if(armorState.get(key) < currentArmor.get(key)) {
//                System.out.println("アーマーの数が多い,"+p.name());
//                return false;
//            }
//        }
        return true;

    }

}
