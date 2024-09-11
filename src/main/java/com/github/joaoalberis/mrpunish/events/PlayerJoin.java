package com.github.joaoalberis.mrpunish.events;

import com.github.joaoalberis.mrpunish.Enum.PunishmentType;
import com.github.joaoalberis.mrpunish.database.PunishDataBase;
import com.github.joaoalberis.mrpunish.database.PunishmentEntity;
import com.github.joaoalberis.mrpunish.utils.PunishUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

import static com.github.joaoalberis.mrpunish.utils.PunishUtils.DATE_TIME_FORMATTER;
import static com.github.joaoalberis.mrpunish.utils.PunishUtils.getRemainingTime;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerLoginEvent event){
        Player player = event.getPlayer();
        List<PunishmentEntity> dataListBan = PunishDataBase.getData(player.getName().toLowerCase(), PunishmentType.BAN, true);
        dataListBan.addAll(PunishDataBase.getData(player.getName().toLowerCase(), PunishmentType.TEMPBAN, true));

        if (!dataListBan.isEmpty()){
            PunishmentEntity data1 = dataListBan.get(0);
            File fileConfig = new File("plugins/MrPunish/config.yml");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(fileConfig);
            String messagePunish = "";
            if (data1.getPunishmentType() == PunishmentType.BAN)
                messagePunish = PunishUtils.formattedMessage(config.getString("ban_message_player"), data1.getPlayerName(), data1.getPunishedBy(), data1.getReason(), "permanent", "");

            if (data1.getPunishmentType() == PunishmentType.TEMPBAN){
                String remainingTime = getRemainingTime(LocalDateTime.now(), LocalDateTime.parse(data1.getExpiryTime(), DATE_TIME_FORMATTER));
                messagePunish = PunishUtils.formattedMessage(config.getString("tempban_message_player"), data1.getPlayerName(), data1.getPunishedBy(), data1.getReason(), "teste", remainingTime);
            }


            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, messagePunish);
        }

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        String playerIp = player.getAddress().getAddress().getHostAddress().replaceAll("\\.", "");

        List<PunishmentEntity> dataListBanIp = PunishDataBase.getData(playerIp, PunishmentType.BANIP, true);

        if (PunishDataBase.isBannedIP(playerIp) && !dataListBanIp.isEmpty()){
            PunishmentEntity data1 = dataListBanIp.get(0);
            File fileConfig = new File("plugins/MrPunish/config.yml");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(fileConfig);
            String messagePunish = PunishUtils.formattedMessage(config.getString("banip_message_player"), data1.getPlayerName(), data1.getPunishedBy(), data1.getReason(), "permanent", "");
            player.kickPlayer(messagePunish);
        }
    }

}
