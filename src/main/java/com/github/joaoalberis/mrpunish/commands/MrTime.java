package com.github.joaoalberis.mrpunish.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MrTime implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("mrtime")) {
            commandSender.sendMessage("-----------MrPunish-----------");
            commandSender.sendMessage("s - Seconds");
            commandSender.sendMessage("m - minutes");
            commandSender.sendMessage("h - hours");
            commandSender.sendMessage("d - days");
            commandSender.sendMessage("w - weeks");
            commandSender.sendMessage("mo - months");
            commandSender.sendMessage("y - years");
            commandSender.sendMessage("------------------------------");
            return true;
        }
        return false;
    }
}
