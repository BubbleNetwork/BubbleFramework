package com.thebubblenetwork.api.game.kit;

import com.thebubblenetwork.api.framework.BubbleNetwork;
import com.thebubblenetwork.api.framework.BukkitBubblePlayer;
import com.thebubblenetwork.api.framework.util.mc.chat.ChatColorAppend;
import com.thebubblenetwork.api.game.BubbleGameAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Created by Jacob on 12/12/2015.
 */
public class Kit {
    private final String name;
    private final int maxlevel;
    private Material display;
    private ItemStack[][] inventorypreset;
    private ItemStack[][] armorpreset;
    private String[] description;
    private int price;
    private KitBuyInventory buyInventory;

    public Kit(Material display, List<ItemStack[]> inventorypreset, List<ItemStack[]> armorpreset, String name, String[] description, int maxlevel, int price) {
        if (maxlevel < 1) {
            throw new IllegalArgumentException("Maxlevel too low");
        }
        if (inventorypreset.size() != maxlevel || armorpreset.size() != maxlevel) {
            throw new IllegalArgumentException("Invalid levels");
        }
        this.display = display;
        this.inventorypreset = new ItemStack[maxlevel][];
        this.name = name;
        this.description = description;
        this.price = price;
        this.maxlevel = maxlevel;
        this.armorpreset = new ItemStack[maxlevel][];
        buyInventory = new KitBuyInventory(this);
        for (int i = 0; i < maxlevel; i++) {
            this.inventorypreset[i] = inventorypreset.get(i);
            this.armorpreset[i] = armorpreset.get(i);
        }
    }

    public int getMaxlevel() {
        return maxlevel;
    }

    public boolean isOwned(BukkitBubblePlayer player) {
        return this == BubbleGameAPI.getInstance().getDefaultKit() || getLevel(player) > 0;
    }

    public int getLevel(BukkitBubblePlayer player) {
        int level = player.getKit(BubbleGameAPI.getInstance().getName(),getName());
        if(level > 0){
            return level;
        }
        return BubbleGameAPI.getInstance().getDefaultKit() == this ? 1 : 0;
    }

    public int getLevelUpcost(BukkitBubblePlayer player) {
        int level = getLevel(player);
        if (level == maxlevel) {
            return -1;
        }
        return (price * (level + 1)) / (maxlevel - level);
    }

    public void apply(BukkitBubblePlayer p) {
        Player bukkitPlayer = p.getPlayer();
        int level = getLevel(p);
        bukkitPlayer.getInventory().clear();
        bukkitPlayer.getInventory().setArmorContents(new ItemStack[4]);
        bukkitPlayer.getInventory().setArmorContents(getArmorpreset(level));
        bukkitPlayer.getInventory().setContents(getInventorypreset(level));
    }

    public void buy(BukkitBubblePlayer player) {
        level(player, 1);
    }

    public void level(BukkitBubblePlayer player, int level) {
        player.setKit(BubbleGameAPI.getInstance().getName(), getNameClear(), level);
        player.save();
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

    public ItemStack[] getInventorypreset(int level) {
        return inventorypreset[level - 1];
    }

    public ItemStack[] getArmorpreset(int level) {
        return armorpreset[level - 1];
    }

    public void setInventorypreset(ItemStack[] inventorypreset, int level) {
        this.inventorypreset[level - 1] = inventorypreset;
    }

    public void setInventorypreset(ItemStack[][] inventorypreset) {
        this.inventorypreset = inventorypreset;
    }

    public Material getDisplay() {
        return display;
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
