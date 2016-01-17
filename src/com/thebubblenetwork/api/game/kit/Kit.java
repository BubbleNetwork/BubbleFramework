package com.thebubblenetwork.api.game.kit;

import com.thebubblenetwork.api.framework.BubblePlayer;
import com.thebubblenetwork.api.framework.util.mc.chat.ChatColorAppend;
import com.thebubblenetwork.api.game.BubbleGameAPI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Jacob on 12/12/2015.
 */
public class Kit {
    private final String name;
    private Material display;
    private Ability[] abilities;
    private ItemStack[] inventorypreset;
    private String[] description;
    private boolean placeabilities;
    private int price;
    private KitBuyInventory buyInventory;
    private final int maxlevel;

    public Kit(
            Material display, Ability[] abilities, ItemStack[] inventorypreset, String name, String[] description,
            boolean placeabilities,int maxlevel, int price) {
        this.display = display;
        this.abilities = abilities;
        this.inventorypreset = inventorypreset;
        this.name = name;
        this.description = description;
        this.placeabilities = placeabilities;
        this.price = price;
        this.maxlevel = maxlevel;
        buyInventory = new KitBuyInventory(this);
        KitManager.register(this);
    }

    public int getMaxlevel(){
        return maxlevel;
    }

    public boolean isOwned(BubblePlayer player){
        return this == BubbleGameAPI.getInstance().getDefaultKit() || player.getKits(BubbleGameAPI.getInstance().getName()).containsKey(getNameClear());
    }

    public int getLevel(BubblePlayer player){
        if(player.getKits(BubbleGameAPI.getInstance().getName()).containsKey(getNameClear())){
            return player.getKits(BubbleGameAPI.getInstance().getName()).get(getNameClear());
        }
        return 0;
    }

    public int getLevelUpcost(BubblePlayer player) {
        int level = getLevel(player);
        if (level == maxlevel)
            return -1;
        return (price * (level + 1)) / (maxlevel - level);
    }

    public KitBuyInventory getBuyInventory() {
        return buyInventory;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isPlaceabilities() {
        return placeabilities;
    }

    public void setPlaceabilities(boolean placeabilities) {
        this.placeabilities = placeabilities;
    }

    public ItemStack[] getInventorypreset() {
        return inventorypreset;
    }

    public void setInventorypreset(ItemStack[] inventorypreset) {
        this.inventorypreset = inventorypreset;
    }

    public Material getDisplay() {
        return display;
    }

    public Ability[] getAbilities() {
        return abilities;
    }

    public void setAbilities(Ability[] abilities) {
        this.abilities = abilities;
    }

    public String getName() {
        return name;
    }

    public String getNameClear() {
        return ChatColorAppend.wipe(name);
    }

    public String[] getDescription() {
        return description;
    }

    public void setDescription(String[] description) {
        this.description = description;
    }
}
