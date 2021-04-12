package com.rainchat.parkoursprinter.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Dash implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = event.getItem();
        if (stack == null) {
            return;
        }

        if (event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            if (isDisplayed(stack, "&e&lРюкзак 1-го уровня")) {
                ItemStack chestplate = player.getInventory().getChestplate();
                if (chestplate == null) {
                    player.sendMessage("Успешно!");
                    Inventory inventory = player.getInventory();

                    inventory.setItem(0, new ItemStack(Material.AIR));
                    inventory.setItem(1, new ItemStack(Material.AIR));
                    inventory.setItem(7, new ItemStack(Material.AIR));
                    inventory.setItem(8, new ItemStack(Material.AIR));
                    return;
                }
                if (isDisplayed(chestplate, "&e&lРюкзак 1-го уровня")) {
                    player.sendMessage("Инвентарь не очищен!");
                }
            }
        }
    }

    public boolean isDisplayed(ItemStack itemStack, String display) {
        Bukkit.broadcastMessage(itemStack
                .getItemMeta()
                .getDisplayName()
                .equals(ChatColor.translateAlternateColorCodes('&', display)) + " ");
        return itemStack
                .getItemMeta()
                .getDisplayName()
                .equals(ChatColor.translateAlternateColorCodes('&', display));
    }
}
