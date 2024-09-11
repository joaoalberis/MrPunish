package com.github.joaoalberis.mrpunish;

import com.github.joaoalberis.mrpunish.commands.*;
import com.github.joaoalberis.mrpunish.database.PunishDataBase;
import com.github.joaoalberis.mrpunish.events.OnChat;
import com.github.joaoalberis.mrpunish.events.PlayerJoin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

import static com.github.joaoalberis.mrpunish.database.PunishDataBase.checkAndRemoveExpiredPunishments;

public final class MrPunish extends JavaPlugin {

    @Override
    public void onEnable() {
        try {
            createConfig();
            register();
            PunishDataBase.initializeDataBase(this.getDataFolder());
            startExpirationCheckThread();
            Bukkit.getConsoleSender().sendMessage("Plugin Habilitado com sucesso");
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage("Plugin nao foi iniciado corretamente");
        }
    }

    @Override
    public void onDisable() {
        PunishDataBase.closeDatabase();
        Bukkit.getConsoleSender().sendMessage("Plugin Desabilitado com sucesso");
    }

    private void startExpirationCheckThread() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            try {
                checkAndRemoveExpiredPunishments();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 20L, 20L);
    }

    private void createConfig(){
        File fileConfig = new File(getDataFolder(), "config.yml");
        if (!fileConfig.exists()){
            fileConfig.getParentFile().mkdirs();
            saveResource("config.yml", false);
        }
    }

    private void register(){
        getCommand("kick").setExecutor(new Kick());
        getCommand("ban").setExecutor(new Ban());
        getCommand("unban").setExecutor(new Unban());
        getCommand("mute").setExecutor(new Mute());
        getCommand("unmute").setExecutor(new UnMute());
        getCommand("banip").setExecutor(new BanIp());
        getCommand("unbanip").setExecutor(new UnBanIp());
        getCommand("tempmute").setExecutor(new TempMute());
        getCommand("tempban").setExecutor(new TempBan());
        getCommand("mrtime").setExecutor(new MrTime());
        getCommand("historypunish").setExecutor(new HistoryPunish());
        getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
        getServer().getPluginManager().registerEvents(new OnChat(), this);
        getServer().getPluginManager().registerEvents(new HistoryPunish(), this);
    }
}
