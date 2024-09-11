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

public class Kick implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("kick")){
            if (commandSender.isOp() || commandSender.hasPermission("mrpunish.kick")) {
                if (strings.length >= 2) {
                        Player playerKick = Bukkit.getPlayer(strings[0]);
                        if (playerKick != null && playerKick.getName().equalsIgnoreCase(strings[0])) {
                            try {
                                File fileConfig = new File("plugins/MrPunish/config.yml");
                                YamlConfiguration config = YamlConfiguration.loadConfiguration(fileConfig);
                                String reason = String.join(" ", Arrays.copyOfRange(strings, 1, strings.length));
                                String messagePunishPlayer = PunishUtils.formattedMessage(config.getString("kick_message_player"), playerKick.getName(), commandSender.getName(), reason, "", null);
                                String messagePunishServer = PunishUtils.formattedMessage(config.getString("kick_message_server"), playerKick.getName(), commandSender.getName(), reason, "", null);
                                Bukkit.getServer().broadcastMessage(messagePunishServer);
                                playerKick.kickPlayer(messagePunishPlayer);
                                PunishDataBase.saveData(playerKick.getName().toLowerCase(), PunishmentType.KICK, reason, commandSender.getName().toLowerCase(), null, false);
                                return true;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            commandSender.sendMessage(ChatColor.RED + "This player is not online or does not exist!");
                        }
                    } else {
                        commandSender.sendMessage(ChatColor.DARK_GREEN + "------MrPunish version 1.0 by MrJoao--------");
                        commandSender.sendMessage(ChatColor.GOLD + "Use " + ChatColor.DARK_GREEN + "/kick (player) (reason)");
                    }
                }
            }else {
                commandSender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            }
        return false;
    }
}
