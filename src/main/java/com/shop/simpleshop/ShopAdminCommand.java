package com.shop.simpleshop;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ShopAdminCommand implements CommandExecutor {

    private final SimpleShop plugin;

    public ShopAdminCommand(SimpleShop plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("simpleshop.admin")) {
            sender.sendMessage(color(plugin.getConfig().getString("messages.no-permission", "&cYou do not have permission to do that.")));
            return true;
        }

        if (args.length == 0 || !args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /shopadmin reload");
            return true;
        }

        plugin.reloadConfig();
        plugin.getShopManager().loadItems();
        sender.sendMessage(color(plugin.getConfig().getString("messages.reload-success", "&aSimpleShop config reloaded.")));
        return true;
    }

    private String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s == null ? "" : s);
    }
}
