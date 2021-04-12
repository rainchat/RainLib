package com.rainchat.parkoursprinter.commands;

import com.rainchat.parkoursprinter.utils.commands.AbstractCommand;

import me.mattstudios.mfgui.gui.components.ItemBuilder;
import me.mattstudios.mfgui.gui.guis.GuiItem;
import me.mattstudios.mfgui.gui.guis.PaginatedGui;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class MainCommand extends AbstractCommand {

    public MainCommand() {
        super(CommandType.PLAYER_ONLY, "spigui");
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        Bukkit.broadcastMessage("Все прошло успешно");
        Player player = (Player) sender;
        PaginatedGui paginatedGui = new PaginatedGui(6, 45, "GUI Title");

        paginatedGui.setItem(6, 3, ItemBuilder.from(Material.PAPER).setName("Previous").asGuiItem(event -> paginatedGui.previous()));
        paginatedGui.setItem(6, 7, ItemBuilder.from(Material.PAPER).setName("Next").asGuiItem(event -> paginatedGui.next()));
        for (Material material: Material.values()){
            paginatedGui.addItem(new GuiItem(material, action -> {
                action.getWhoClicked().sendMessage("Предмет по которому вы нажали " + material);
            }));
        }
        paginatedGui.setDefaultClickAction(event -> {
            event.setCancelled(true);
        });
        paginatedGui.open(player);
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "epiclevels.menu";
    }

    @Override
    public String getSyntax() {
        return "/levels";
    }

    @Override
    public String getDescription() {
        return "Displays top levels.";
    }
}
