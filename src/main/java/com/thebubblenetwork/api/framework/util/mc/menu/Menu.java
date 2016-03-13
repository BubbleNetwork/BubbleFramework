package com.thebubblenetwork.api.framework.util.mc.menu;

/**
 * Created by Jacob on 12/12/2015.
 */

import com.thebubblenetwork.api.framework.util.mc.chat.ChatColorAppend;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class Menu implements InventoryHolder {
    public static int getRoundedInventorySize(int items) {
        return items + (9 - (items % 9));
    }

    protected Inventory inventory;
    private int size;

    public Menu(String title, int size) {
        Validate.isTrue(size <= 54 && size > 0 && size % 9 == 0, "Inventory size must be divisble by 9, smaller or equal to 54 and bigger than 0");
        inventory = Bukkit.createInventory(this, size, ChatColorAppend.translate(title));
        this.size = size;
    }

    public void show(Player player) {
        player.openInventory(getInventory());
    }

    public abstract void click(Player player, ClickType type, int slot, ItemStack itemStack);

    protected String getFriendlyName(ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta == null || !itemMeta.hasDisplayName()) {
            return null;
        }

        return ChatColorAppend.wipe(itemMeta.getDisplayName());
    }

    public Inventory getInventory() {
        return inventory;
    }

    public final void update() {
        getInventory().setContents(generate());
    }

    public abstract ItemStack[] generate();

    public int getSize() {
        return size;
    }
}
