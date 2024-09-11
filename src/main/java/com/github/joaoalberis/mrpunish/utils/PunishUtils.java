package com.github.joaoalberis.mrpunish.utils;

import com.github.joaoalberis.mrpunish.exceptions.TimeFormatInvalid;
import org.bukkit.ChatColor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class PunishUtils {

    private static final Map<String, ChronoUnit> timeUnits;
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    static {
        timeUnits = new HashMap<>();
        timeUnits.put("s", ChronoUnit.SECONDS);
        timeUnits.put("m", ChronoUnit.MINUTES);
        timeUnits.put("h", ChronoUnit.HOURS);
        timeUnits.put("d", ChronoUnit.DAYS);
        timeUnits.put("w", ChronoUnit.WEEKS);
        timeUnits.put("mo", ChronoUnit.MONTHS);
        timeUnits.put("y", ChronoUnit.YEARS);
    }

    public static String formattedMessage(String originalMessage, String player, String executor, String reason, String time, String reamingTIme){
        originalMessage = originalMessage.replaceAll("\\n", "\n");
        originalMessage = originalMessage.replaceAll("%player%", player);
        originalMessage = originalMessage.replaceAll("%player_executor%", executor);
        originalMessage = originalMessage.replaceAll("%reason%", reason);
        originalMessage = originalMessage.replaceAll("%time%", String.valueOf(time));
        originalMessage = originalMessage.replaceAll("%remaining_time%", reamingTIme != null ? reamingTIme : "");
        originalMessage = originalMessage.replaceAll("&", "§");
        return originalMessage;
    }

    public static String getRemainingTime(LocalDateTime now, LocalDateTime endTime) {
        Duration duration = Duration.between(now, endTime);

        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;

        StringBuilder remainingTime = new StringBuilder();
        if (days > 0) remainingTime.append(days).append(" day").append(days > 1 ? "s" : "").append(", ");
        if (hours > 0) remainingTime.append(hours).append(" hour").append(hours > 1 ? "s" : "").append(", ");
        if (minutes > 0) remainingTime.append(minutes).append(" minute").append(minutes > 1 ? "s" : "").append(", ");
        remainingTime.append(seconds).append(" second").append(seconds != 1 ? "s" : "");

        return remainingTime.toString();
    }

    public static LocalDateTime getTime(String time){
        if (!time.matches("\\d+(s|m|h|d|w|mo|y)")){
            throw new TimeFormatInvalid(ChatColor.RED + "the time format entered is not valid, use a valid format.\nUse /mrtime to see formats");
        }

        String numberPart = time.replaceAll("[^0-9]", "");
        String unitPart = time.replaceAll("[0-9]", "");

        long timeValue = Long.parseLong(numberPart);

        return LocalDateTime.now().plus(timeValue, timeUnits.get(unitPart));
    }

}
