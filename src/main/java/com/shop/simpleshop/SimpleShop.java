package com.shop.simpleshop;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class SimpleShop extends JavaPlugin {

    private static SimpleShop instance;
    private Economy economy;
    private ShopManager shopManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        if (!setupEconomy()) {
            getLogger().severe(getConfig().getString("messages.vault-missing",
                    "Vault economy plugin not found! Disabling shop."));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.shopManager = new ShopManager(this);

        getCommand("shop").setExecutor(new ShopCommand(this));
        getCommand("shopadmin").setExecutor(new ShopAdminCommand(this));
        getServer().getPluginManager().registerEvents(new ShopGUIListener(this), this);

        getLogger().info("SimpleShop enabled with " + shopManager.getItems().size() + " item(s) loaded.");
    }

    @Override
    public void onDisable() {
        getLogger().info("SimpleShop disabled.");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        this.economy = rsp.getProvider();
        return true;
    }

    public Economy getEconomy() {
        return economy;
    }

    public ShopManager getShopManager() {
        return shopManager;
    }

    public static SimpleShop getInstance() {
        return instance;
    }
}
