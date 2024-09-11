package com.github.joaoalberis.mrpunish.commands;

import com.github.joaoalberis.mrpunish.Enum.PunishmentType;
import com.github.joaoalberis.mrpunish.database.PunishDataBase;
import com.github.joaoalberis.mrpunish.exceptions.TimeFormatInvalid;
import com.github.joaoalberis.mrpunish.utils.PunishUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Arrays;

import static com.github.joaoalberis.mrpunish.utils.PunishUtils.getRemainingTime;

public class TempBan implements CommandExecutor {

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("tempban")){
            if (commandSender.isOp() || commandSender.hasPermission("mrpunish.tempban")) {
                if (strings.length >= 2) {
                    String playerName = strings[0];
                    if (!PunishDataBase.isBanned(playerName.toLowerCase()) && !PunishDataBase.isTempBanned(playerName.toLowerCase())) {
                        try{
                            File fileConfig = new File("plugins/MrPunish/config.yml");
                            YamlConfiguration config = YamlConfiguration.loadConfiguration(fileConfig);

                            String reason = String.join(" ", Arrays.copyOfRange(strings, 2, strings.length));
                            LocalDateTime time = PunishUtils.getTime(strings[1]);
                            LocalDateTime now = LocalDateTime.now();

                            String remainingTime = getRemainingTime(now, time);

                            String messagePunishPlayer = PunishUtils.formattedMessage(config.getString("tempban_message_player"), playerName, commandSender.getName(), reason, strings[1], remainingTime);
                            String messagePunishServer = PunishUtils.formattedMessage(config.getString("tempban_message_server"), playerName, commandSender.getName(), reason, strings[1], remainingTime);


                            Player player = Bukkit.getPlayer(playerName);

                            if (player != null && player.getName().equalsIgnoreCase(playerName))
                                player.kickPlayer(messagePunishPlayer);


                            Bukkit.getServer().broadcastMessage(messagePunishServer);
                            PunishDataBase.saveData(playerName.toLowerCase(), PunishmentType.TEMPBAN, reason, commandSender.getName().toLowerCase(), time, true);

                            return true;
                        }catch (TimeFormatInvalid e){
                            commandSender.sendMessage(e.getMessage());
                        }
                    }else {
                        commandSender.sendMessage(ChatColor.RED + "This user is already banned");
                    }
                } else {
                    commandSender.sendMessage(ChatColor.DARK_GREEN + "MrPunish version 1.0 by MrJoao");
                    commandSender.sendMessage(ChatColor.GOLD + "Use " + ChatColor.DARK_GREEN + "/tempban (player) (time) (reason)");
                }
            }else {
                commandSender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            }
        }
        return false;
    }

}
