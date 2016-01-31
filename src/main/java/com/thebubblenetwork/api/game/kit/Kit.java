package com.thebubblenetwork.api.game.kit;

import com.thebubblenetwork.api.framework.util.mc.chat.ChatColorAppend;
import com.thebubblenetwork.api.game.BubbleGameAPI;
import com.thebubblenetwork.api.global.player.BubblePlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Jacob on 12/12/2015.
 */
public class Kit {
    private final String name;
    private final int maxlevel;
    private Material display;
    private Ability[] abilities;
    private ItemStack[] inventorypreset;
    private ItemStack[] armorpreset;
    private String[] description;
    private boolean placeabilities;
    private int price;
    private KitBuyInventory buyInventory;

    public Kit(Material display, Ability[] abilities, ItemStack[] inventorypreset, ItemStack[] armorpreset, String name, String[] description,
               boolean placeabilities, int maxlevel, int price) {
        this.display = display;
        this.abilities = abilities;
        this.inventorypreset = inventorypreset;
        this.name = name;
        this.description = description;
        this.placeabilities = placeabilities;
        this.price = price;
        this.maxlevel = maxlevel;
        this.armorpreset = armorpreset;
        buyInventory = new KitBuyInventory(this);
        KitManager.register(this);
    }

    public int getMaxlevel() {
        return maxlevel;
    }

    public boolean isOwned(BubblePlayer<Player> player) {
        return this == BubbleGameAPI.getInstance().getDefaultKit() || player.getKits(BubbleGameAPI.getInstance().getName()).containsKey(getNameClear());
    }

    public int getLevel(BubblePlayer<Player> player) {
        if (player.getKits(BubbleGameAPI.getInstance().getName()).containsKey(getNameClear())) {
            return player.getKits(BubbleGameAPI.getInstance().getName()).get(getNameClear());
        }
        return 0;
    }

    public int getLevelUpcost(BubblePlayer<Player> player) {
        int level = getLevel(player);
        if (level == maxlevel)
            return -1;
        return (price * (level + 1)) / (maxlevel - level);
    }

    public void apply(Player p) {
        p.getInventory().clear();
        p.getInventory().setArmorContents(new ItemStack[4]);
        p.getInventory().setArmorContents(armorpreset);
        p.getInventory().setContents(inventorypreset);
        if (placeabilities) {
            for (Ability ability : abilities) {
                p.getInventory().setItem(ability.getSlot(), ability.getDislay());
            }
        }
    }

    public void onAbilityMove(int from, int to) {
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
