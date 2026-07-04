package com.shop.simpleshop;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class ShopManager {

    private final SimpleShop plugin;
    private final List<ShopItem> items = new ArrayList<>();

    public ShopManager(SimpleShop plugin) {
        this.plugin = plugin;
        loadItems();
    }

    public void loadItems() {
        items.clear();
        List<?> rawList = plugin.getConfig().getList("items");
        if (rawList == null) {
            plugin.getLogger().warning("No 'items' list found in config.yml — shop will be empty.");
            return;
        }

        for (Object obj : rawList) {
            if (!(obj instanceof ConfigurationSection) && !(obj instanceof java.util.Map)) {
                continue;
            }

            String materialName;
            String name;
            double buyPrice;
            double sellPrice;
            int amount;

            if (obj instanceof ConfigurationSection section) {
                materialName = section.getString("material");
                name = section.getString("name", materialName);
                buyPrice = section.getDouble("buy-price", -1);
                sellPrice = section.getDouble("sell-price", -1);
                amount = section.getInt("amount", 1);
            } else {
                @SuppressWarnings("unchecked")
                java.util.Map<String, Object> map = (java.util.Map<String, Object>) obj;
                materialName = String.valueOf(map.get("material"));
                name = map.containsKey("name") ? String.valueOf(map.get("name")) : materialName;
                buyPrice = map.containsKey("buy-price") ? ((Number) map.get("buy-price")).doubleValue() : -1;
                sellPrice = map.containsKey("sell-price") ? ((Number) map.get("sell-price")).doubleValue() : -1;
                amount = map.containsKey("amount") ? ((Number) map.get("amount")).intValue() : 1;
            }

            if (materialName == null) {
                continue;
            }

            Material material = Material.matchMaterial(materialName.trim().toUpperCase());
            if (material == null) {
                plugin.getLogger().warning("Skipping unknown material in config.yml: " + materialName
                        + " (not valid on this server version)");
                continue;
            }

            items.add(new ShopItem(material, name, buyPrice, sellPrice, Math.max(1, amount)));
        }
    }

    public List<ShopItem> getItems() {
        return items;
    }
}
