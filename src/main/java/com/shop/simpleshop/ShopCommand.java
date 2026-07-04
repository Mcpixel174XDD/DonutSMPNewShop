package com.shop.simpleshop;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopCommand implements CommandExecutor {

    private final SimpleShop plugin;

    public ShopCommand(SimpleShop plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can open the shop.");
            return true;
        }

        if (!player.hasPermission("simpleshop.use")) {
            player.sendMessage(color(plugin.getConfig().getString("messages.no-permission", "&cYou do not have permission to do that.")));
            return true;
        }

        ShopGUI gui = new ShopGUI(plugin);
        player.openInventory(gui.buildPage(0));
        return true;
    }

    private String color(String s) {
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', s == null ? "" : s);
    }
}
