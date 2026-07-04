package com.shop.simpleshop;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class ShopHolder implements InventoryHolder {

    private final int page;

    public ShopHolder(int page) {
        this.page = page;
    }

    public int getPage() {
        return page;
    }

    @Override
    public Inventory getInventory() {
        // Not used directly; Bukkit manages the inventory instance separately.
        return null;
    }
}
