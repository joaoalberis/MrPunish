package com.github.joaoalberis.mrpunish.commands;

import com.github.joaoalberis.mrpunish.Enum.PunishmentType;
import com.github.joaoalberis.mrpunish.database.PunishDataBase;
import com.github.joaoalberis.mrpunish.utils.PunishUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class Mute implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("mute")){
            if (commandSender.isOp() || commandSender.hasPermission("mrpunish.mute")) {
                if (strings.length >= 2) {
                    String playerName = strings[0];
                    if (!PunishDataBase.isMutted(playerName.toLowerCase())) {
                        File fileConfig = new File("plugins/MrPunish/config.yml");
                        YamlConfiguration config = YamlConfiguration.loadConfiguration(fileConfig);
                        String reason = String.join(" ", Arrays.copyOfRange(strings, 1, strings.length));
                        String messagePunishPlayer = PunishUtils.formattedMessage(config.getString("mute_message_player"), playerName, commandSender.getName(), reason, "permanent", null);
                        String messagePunishServer = PunishUtils.formattedMessage(config.getString("mute_message_server"), playerName, commandSender.getName(), reason, "permanent", null);
                        Bukkit.getServer().broadcastMessage(messagePunishServer);

                        Player player = Bukkit.getPlayer(playerName);

                        if (player != null && player.getName().equalsIgnoreCase(playerName)) player.sendMessage(messagePunishPlayer);

                        PunishDataBase.saveData(playerName.toLowerCase(), PunishmentType.MUTE, reason, commandSender.getName().toLowerCase(), null, true);
                        return true;
                    }else {
                        commandSender.sendMessage(ChatColor.RED + "This user is already muted");
                    }
                } else {
                    commandSender.sendMessage(ChatColor.DARK_GREEN + "MrPunish version 1.0 by MrJoao");
                    commandSender.sendMessage(ChatColor.GOLD + "Use " + ChatColor.DARK_GREEN + "/mute (player) (reason)");
                }
            }else {
                commandSender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            }
        }
        return false;
    }
}
