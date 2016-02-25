package com.thebubblenetwork.api.framework.util.mc.menu;

import com.thebubblenetwork.api.framework.BubbleNetwork;
import com.thebubblenetwork.api.framework.util.mc.items.ItemStackBuilder;
import com.thebubblenetwork.api.game.BubbleGameAPI;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jacob on 13/12/2015.
 */
public abstract class BuyInventory extends Menu {
    private static ItemStackBuilder
            backgrounddefault = new ItemStackBuilder(Material.STAINED_GLASS_PANE)
            .withAmount(1)
            .withColor(DyeColor.GRAY)
            .withName(ChatColor.DARK_GRAY + "()"),
            yesitemdefault = new ItemStackBuilder(Material.EMERALD_BLOCK)
                    .withAmount(1)
                    .withName(ChatColor.GREEN + "Yes"),
            noitemdefault = new ItemStackBuilder(Material.REDSTONE_BLOCK)
                    .withAmount(1)
                    .withName(ChatColor.RED + "No");

    private static int[] yesslotsdefault = new int[]{(9 * 2) + 3}, noslotsdefault = new int[]{(9 * 2) + 5};
    private static int defaultsize = 9 * 5;
    private String name;
    private List<Integer> yesslots, noslots;
    private ItemStackBuilder yesitem, noitem, background;

    public BuyInventory(
            int size, String name,int[] yesslots, int[] noslots, ItemStackBuilder yesitem,
            ItemStackBuilder noitem, ItemStackBuilder background) {
        super(name, size);
        this.name = name;
        this.yesslots = fromArray(yesslots);
        this.noslots = fromArray(noslots);
        this.yesitem = yesitem;
        this.noitem = noitem;
        this.background = background;
        update();
        BubbleNetwork.getInstance().registerMenu(BubbleGameAPI.getInstance(),this);
    }

    public BuyInventory(String name, String yes, String no) {
        this(getDefaultsize(), name, getYesslotsdefault().clone(), getNoslotsdefault().clone(),
                getYesitemdefault().clone().withName(ChatColor.GREEN + yes), getNoitemdefault().clone().withName(ChatColor.RED + no),
                getBackgrounddefault().clone());
    }

    public BuyInventory(String name) {
        this(defaultsize, name,getYesslotsdefault().clone(), getNoslotsdefault().clone(), getYesitemdefault().clone(), getNoitemdefault().clone(), getBackgrounddefault().clone());
    }

    public static int[] getYesslotsdefault() {
        return yesslotsdefault;
    }

    public static int[] getNoslotsdefault() {
        return noslotsdefault;
    }

    public static int getDefaultsize() {
        return defaultsize;
    }

    public static ItemStackBuilder getBackgrounddefault() {
        return backgrounddefault;
    }

    public static ItemStackBuilder getYesitemdefault() {
        return yesitemdefault;
    }

    public static ItemStackBuilder getNoitemdefault() {
        return noitemdefault;
    }

    private static ArrayList<Integer> fromArray(int[] i) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        for (int o : i) {
            arrayList.add(o);
        }
        return arrayList;
    }

    public String getName() {
        return name;
    }

    public List<Integer> getYesslots() {
        return yesslots;
    }

    public List<Integer> getNoslots() {
        return noslots;
    }

    public ItemStackBuilder getYesitem() {
        return yesitem;
    }

    public ItemStackBuilder getNoitem() {
        return noitem;
    }

    public ItemStackBuilder getBackground() {
        return background;
    }

    public ItemStack[] generate() {
        ItemStack[] is = new ItemStack[getInventory().getSize()];
        for (int i = 0; i < is.length; i++) {
            ItemStack item;
            if (yesslots.contains(i)) {
                item = yesitem.build();
            } else if (noslots.contains(i)) {
                item = noitem.build();
            } else item = background.build();
            is[i] = item;
        }
        return is;
    }

    public void click(Player p, ClickType type, int slot, ItemStack is) {
        if (type != ClickType.LEFT) return;
        if (yesslots.contains(slot))
            onAllow(p);
        else if (noslots.contains(slot))
            onCancel(p);
    }

    public abstract void onCancel(Player p);

    public abstract void onAllow(Player p);
}
