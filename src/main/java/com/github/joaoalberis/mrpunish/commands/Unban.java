package com.github.joaoalberis.mrpunish.commands;

import com.github.joaoalberis.mrpunish.Enum.PunishmentType;
import com.github.joaoalberis.mrpunish.database.PunishDataBase;
import com.github.joaoalberis.mrpunish.utils.PunishUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Unban implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("unban")){
            if (commandSender.isOp() || commandSender.hasPermission("mrpunish.unban")) {
                if (strings.length == 1) {
                    String playerName = strings[0];
                    if (PunishDataBase.isBanned(playerName.toLowerCase()) || PunishDataBase.isTempBanned(playerName.toLowerCase())) {
                        File fileConfig = new File("plugins/MrPunish/config.yml");
                        YamlConfiguration config = YamlConfiguration.loadConfiguration(fileConfig);
                        String message = PunishUtils.formattedMessage(config.getString("unban_message_player"), playerName, commandSender.getName(), "", "", null);

                        commandSender.sendMessage(message);
                        PunishDataBase.updateData(playerName.toLowerCase(), PunishmentType.BAN, null, null, null, false);
                        PunishDataBase.updateData(playerName.toLowerCase(), PunishmentType.TEMPBAN, null, null, null, false);
                        return true;
                    }else {
                        commandSender.sendMessage(ChatColor.RED + "This user is not banned");
                    }
                } else {
                    commandSender.sendMessage(ChatColor.DARK_GREEN + "MrPunish version 1.0 by MrJoao");
                    commandSender.sendMessage(ChatColor.GOLD + "Use " + ChatColor.DARK_GREEN + "/unban (player)");
                }
            }else {
                commandSender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            }
        }
        return false;
    }
}
