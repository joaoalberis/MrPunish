package com.github.joaoalberis.mrpunish.commands;

import com.github.joaoalberis.mrpunish.database.PunishDataBase;
import com.github.joaoalberis.mrpunish.database.PunishmentEntity;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static com.github.joaoalberis.mrpunish.utils.PunishUtils.DATE_TIME_FORMATTER;

public class HistoryPunish implements CommandExecutor, Listener {


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) return false;

        if (command.getName().equalsIgnoreCase("historypunish")) {
            if (commandSender.isOp() || commandSender.hasPermission("mrpunish.mrview")) {
                if (strings.length == 1) {
                    Player sender = (Player) commandSender;
                    String playerName = strings[0];

                    Inventory gui = Bukkit.createInventory(null, 9 * 6, playerName + " Punishments");

                    List<PunishmentEntity> punishmentEntities = PunishDataBase.getData(playerName.toLowerCase());
                    final int[] index = {0};
                    punishmentEntities.forEach(punishment -> {
                        List<String> lore = new ArrayList<>();
                        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                        ItemMeta headMeta = head.getItemMeta();
                        headMeta.setDisplayName("Punishment " + (index[0] + 1));

                        lore.add("Punishment Type: " + punishment.getPunishmentType().toString());
                        lore.add("Reason: " + punishment.getReason());
                        lore.add("Punished By: " + punishment.getPunishedBy());
                        lore.add("Date Punishment: " + punishment.getDatePunishment().format(DATE_TIME_FORMATTER));
                        lore.add("Active: " + (punishment.isActive() ? "Yes" : "No"));
                        headMeta.setLore(lore);
                        head.setItemMeta(headMeta);
                        gui.setItem(index[0], head);
                        index[0]++;
                    });

                    sender.openInventory(gui);

                    return true;
                } else {
                    commandSender.sendMessage(ChatColor.DARK_GREEN + "MrPunish version 1.0 by MrJoao");
                    commandSender.sendMessage(ChatColor.GOLD + "Use " + ChatColor.DARK_GREEN + "/historypunish (player)");
                }
            } else {
                commandSender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            }
        }
        return false;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().endsWith("Punishments")){
            event.setCancelled(true);
        }
    }
}
