package com.github.joaoalberis.mrpunish.events;

import com.github.joaoalberis.mrpunish.Enum.PunishmentType;
import com.github.joaoalberis.mrpunish.database.PunishDataBase;
import com.github.joaoalberis.mrpunish.database.PunishmentEntity;
import com.github.joaoalberis.mrpunish.utils.PunishUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

import static com.github.joaoalberis.mrpunish.utils.PunishUtils.DATE_TIME_FORMATTER;
import static com.github.joaoalberis.mrpunish.utils.PunishUtils.getRemainingTime;

public class OnChat implements Listener {

    @EventHandler
    public void onChatMessage(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName().toLowerCase();

        List<PunishmentEntity> dataList = PunishDataBase.getData(playerName, PunishmentType.MUTE, true);
        dataList.addAll(PunishDataBase.getData(playerName, PunishmentType.TEMPMUTE, true));

        if (!dataList.isEmpty()) {
            PunishmentEntity data1 = dataList.get(0);
            File fileConfig = new File("plugins/MrPunish/config.yml");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(fileConfig);
            String messagePunish = "";
            if (data1.getPunishmentType() == PunishmentType.MUTE)
                messagePunish = PunishUtils.formattedMessage(config.getString("mute_message_player"), data1.getPlayerName(), data1.getPunishedBy(), data1.getReason(), "permanent", "");

            if (data1.getPunishmentType() == PunishmentType.TEMPMUTE) {
                String remainingTime = getRemainingTime(LocalDateTime.now(), LocalDateTime.parse(data1.getExpiryTime(), DATE_TIME_FORMATTER));
                messagePunish = PunishUtils.formattedMessage(config.getString("tempmute_message_player"), data1.getPlayerName(), data1.getPunishedBy(), data1.getReason(), "", remainingTime);
            }
            event.setCancelled(true);
            player.sendMessage(messagePunish);
        }
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage().toLowerCase();
        String playerName = player.getName().toLowerCase();

        if (message.startsWith("/tell ") || message.startsWith("/msg ") || message.startsWith("/w ")) {
            List<PunishmentEntity> dataList = PunishDataBase.getData(playerName, PunishmentType.MUTE, true);
            dataList.addAll(PunishDataBase.getData(playerName, PunishmentType.TEMPMUTE, true));
            if (!dataList.isEmpty()) {
                PunishmentEntity data1 = dataList.get(0);
                File fileConfig = new File("plugins/MrPunish/config.yml");
                YamlConfiguration config = YamlConfiguration.loadConfiguration(fileConfig);
                String messagePunish = "";
                if (data1.getPunishmentType() == PunishmentType.MUTE)
                    messagePunish = PunishUtils.formattedMessage(config.getString("mute_message_player"), data1.getPlayerName(), data1.getPunishedBy(), data1.getReason(), "permanent", "");

                if (data1.getPunishmentType() == PunishmentType.TEMPMUTE) {
                    String remainingTime = getRemainingTime(LocalDateTime.now(), LocalDateTime.parse(data1.getExpiryTime(), DATE_TIME_FORMATTER));
                    messagePunish = PunishUtils.formattedMessage(config.getString("tempmute_message_player"), data1.getPlayerName(), data1.getPunishedBy(), data1.getReason(), "", remainingTime);
                }
                event.setCancelled(true);
                player.sendMessage(messagePunish);
            }
        }
    }

}
