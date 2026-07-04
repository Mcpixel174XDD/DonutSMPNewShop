package com.shop.simpleshop;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ShopGUI {

    public static final int PAGE_SIZE = 45; // rows 1-5 (0-44), row 6 (45-53) reserved for nav
    public static final int PREV_SLOT = 45;
    public static final int INFO_SLOT = 49;
    public static final int NEXT_SLOT = 53;

    private final SimpleShop plugin;

    public ShopGUI(SimpleShop plugin) {
        this.plugin = plugin;
    }

    public int getTotalPages() {
        int size = plugin.getShopManager().getItems().size();
        return Math.max(1, (int) Math.ceil(size / (double) PAGE_SIZE));
    }

    public Inventory buildPage(int page) {
        int totalPages = getTotalPages();
        if (page < 0) page = 0;
        if (page >= totalPages) page = totalPages - 1;

        String rawTitle = plugin.getConfig().getString("gui.title", "&8&lShop");
        String title = ChatColor.translateAlternateColorCodes('&', rawTitle) + ChatColor.DARK_GRAY
                + " (" + (page + 1) + "/" + totalPages + ")";

        Inventory inv = plugin.getServer().createInventory(new ShopHolder(page), 54, title);

        List<ShopItem> items = plugin.getShopManager().getItems();
        int start = page * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, items.size());

        for (int i = start; i < end; i++) {
            inv.setItem(i - start, buildItemStack(items.get(i)));
        }

        // Nav row filler
        ItemStack filler = namedItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int slot = 45; slot < 54; slot++) {
            inv.setItem(slot, filler);
        }

        if (page > 0) {
            inv.setItem(PREV_SLOT, namedItem(Material.ARROW, ChatColor.YELLOW + "Previous Page"));
        }
        if (page < totalPages - 1) {
            inv.setItem(NEXT_SLOT, namedItem(Material.ARROW, ChatColor.YELLOW + "Next Page"));
        }
        inv.setItem(INFO_SLOT, namedItem(Material.BOOK, ChatColor.AQUA + "Left-click to buy, Shift-click to sell"));

        return inv;
    }

    private ItemStack buildItemStack(ShopItem shopItem) {
        ItemStack stack = new ItemStack(shopItem.getMaterial());
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', shopItem.getDisplayName()));

            List<String> lore = new ArrayList<>();
            if (shopItem.isBuyable()) {
                lore.add(ChatColor.GREEN + "Buy: " + ChatColor.WHITE + "$" + format(shopItem.getBuyPrice())
                        + ChatColor.GRAY + " (x" + shopItem.getAmount() + ")");
            }
            if (shopItem.isSellable()) {
                lore.add(ChatColor.GOLD + "Sell: " + ChatColor.WHITE + "$" + format(shopItem.getSellPrice())
                        + ChatColor.GRAY + " (x" + shopItem.getAmount() + ")");
            }
            lore.add("");
            if (shopItem.isBuyable()) {
                lore.add(ChatColor.YELLOW + "Left-click to buy");
            }
            if (shopItem.isSellable()) {
                lore.add(ChatColor.YELLOW + "Shift-click to sell");
            }
            meta.setLore(lore);
            stack.setItemMeta(meta);
        }
        return stack;
    }

    private ItemStack namedItem(Material material, String name) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            stack.setItemMeta(meta);
        }
        return stack;
    }

    private String format(double value) {
        if (value == Math.floor(value)) {
            return String.valueOf((long) value);
        }
        return String.format("%.2f", value);
    }
}
