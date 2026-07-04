package com.shop.simpleshop;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopGUIListener implements Listener {

    private final SimpleShop plugin;

    public ShopGUIListener(SimpleShop plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof ShopHolder shopHolder)) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        int slot = event.getRawSlot();
        if (slot < 0 || slot >= event.getInventory().getSize()) {
            return;
        }

        ShopGUI gui = new ShopGUI(plugin);
        int page = shopHolder.getPage();

        // Navigation
        if (slot == ShopGUI.PREV_SLOT) {
            if (page > 0) {
                player.openInventory(gui.buildPage(page - 1));
            }
            return;
        }
        if (slot == ShopGUI.NEXT_SLOT) {
            if (page < gui.getTotalPages() - 1) {
                player.openInventory(gui.buildPage(page + 1));
            }
            return;
        }
        if (slot >= ShopGUI.PAGE_SIZE) {
            return; // clicked filler/info in nav row
        }

        List<ShopItem> items = plugin.getShopManager().getItems();
        int index = page * ShopGUI.PAGE_SIZE + slot;
        if (index < 0 || index >= items.size()) {
            return;
        }
        ShopItem shopItem = items.get(index);

        if (event.getClick().isShiftClick()) {
            handleSell(player, shopItem);
        } else {
            handleBuy(player, shopItem);
        }
    }

    private void handleBuy(Player player, ShopItem item) {
        if (!item.isBuyable()) {
            return;
        }
        Economy econ = plugin.getEconomy();
        double price = item.getBuyPrice();

        if (econ.getBalance(player) < price) {
            player.sendMessage(msg("messages.not-enough-money", "&cYou don't have enough money for that.", null));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            return;
        }

        ItemStack stack = new ItemStack(item.getMaterial(), item.getAmount());
        Map<Integer, ItemStack> leftover = player.getInventory().addItem(stack);
        if (!leftover.isEmpty()) {
            player.sendMessage(msg("messages.inventory-full", "&cYour inventory is full!", null));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            return;
        }

        EconomyResponse response = econ.withdrawPlayer(player, price);
        if (!response.transactionSuccess()) {
            // Roll back given items if payment somehow failed after inventory check
            player.getInventory().removeItem(stack);
            player.sendMessage(msg("messages.not-enough-money", "&cYou don't have enough money for that.", null));
            return;
        }

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("amount", String.valueOf(item.getAmount()));
        placeholders.put("item", ChatColor.stripColor(color(item.getDisplayName())));
        placeholders.put("price", formatPrice(price));
        player.sendMessage(msg("messages.purchase-success",
                "&aBought &f{amount}x {item} &afor &f${price}&a.", placeholders));
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
    }

    private void handleSell(Player player, ShopItem item) {
        if (!item.isSellable()) {
            return;
        }
        int amount = item.getAmount();
        if (!hasAtLeast(player, item.getMaterial(), amount)) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("item", ChatColor.stripColor(color(item.getDisplayName())));
            player.sendMessage(msg("messages.no-items-to-sell", "&cYou don't have any {item} to sell.", placeholders));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            return;
        }

        removeItems(player, item.getMaterial(), amount);

        Economy econ = plugin.getEconomy();
        double price = item.getSellPrice();
        econ.depositPlayer(player, price);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("amount", String.valueOf(amount));
        placeholders.put("item", ChatColor.stripColor(color(item.getDisplayName())));
        placeholders.put("price", formatPrice(price));
        player.sendMessage(msg("messages.sell-success",
                "&aSold &f{amount}x {item} &afor &f${price}&a.", placeholders));
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
    }

    private boolean hasAtLeast(Player player, Material material, int amount) {
        int count = 0;
        for (ItemStack stack : player.getInventory().getContents()) {
            if (stack != null && stack.getType() == material) {
                count += stack.getAmount();
                if (count >= amount) return true;
            }
        }
        return false;
    }

    private void removeItems(Player player, Material material, int amount) {
        ItemStack[] contents = player.getInventory().getContents();
        int remaining = amount;
        for (int i = 0; i < contents.length && remaining > 0; i++) {
            ItemStack stack = contents[i];
            if (stack != null && stack.getType() == material) {
                int take = Math.min(remaining, stack.getAmount());
                stack.setAmount(stack.getAmount() - take);
                remaining -= take;
                if (stack.getAmount() <= 0) {
                    contents[i] = null;
                }
            }
        }
        player.getInventory().setContents(contents);
    }

    private String formatPrice(double value) {
        if (value == Math.floor(value)) {
            return String.valueOf((long) value);
        }
        return String.format("%.2f", value);
    }

    private String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s == null ? "" : s);
    }

    private String msg(String path, String def, Map<String, String> placeholders) {
        String raw = plugin.getConfig().getString(path, def);
        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                raw = raw.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }
        return color(raw);
    }
}
