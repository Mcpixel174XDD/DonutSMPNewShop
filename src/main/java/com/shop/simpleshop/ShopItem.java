package com.shop.simpleshop;

import org.bukkit.Material;

public class ShopItem {

    private final Material material;
    private final String displayName;
    private final double buyPrice;
    private final double sellPrice;
    private final int amount;

    public ShopItem(Material material, String displayName, double buyPrice, double sellPrice, int amount) {
        this.material = material;
        this.displayName = displayName;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.amount = amount;
    }

    public Material getMaterial() {
        return material;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public int getAmount() {
        return amount;
    }

    public boolean isBuyable() {
        return buyPrice >= 0;
    }

    public boolean isSellable() {
        return sellPrice >= 0;
    }
}
