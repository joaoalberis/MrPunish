package com.github.joaoalberis.mrpunish.database;

import com.github.joaoalberis.mrpunish.Enum.PunishmentType;
import com.github.joaoalberis.mrpunish.utils.PunishUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.io.File;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.github.joaoalberis.mrpunish.utils.PunishUtils.DATE_TIME_FORMATTER;
import static com.github.joaoalberis.mrpunish.utils.PunishUtils.getRemainingTime;

public class PunishDataBase {

    private static Connection connection;


    public static void initializeDataBase(File dataFolder) {
        try {
            if(!dataFolder.exists()){
                dataFolder.mkdirs();
            }

            File dbFile = new File(dataFolder, "mrpunish.db");
            String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
            connection = DriverManager.getConnection(url);

            createTablePunishments();
        }catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage("There was a problem initializing the database");
            e.printStackTrace();
        }
    }

    private static void createTablePunishments() throws SQLException {
        String createTable = "CREATE TABLE IF NOT EXISTS punishments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "player_name TEXT NOT NULL, " +
                "punishment_type TEXT NOT NULL, " +
                "reason TEXT, " +
                "punished_by TEXT, " +
                "date_punishment TEXT, " +
                "expiry_time TEXT, " +
                "active BOOLEAN NOT NULL)";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTable);
        }
    }

    public static void closeDatabase() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isBanned(String playerName) {
        String query = "SELECT * FROM punishments WHERE player_name = ? AND punishment_type = 'BAN' AND active = TRUE";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, playerName);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isTempBanned(String playerName) {
        String query = "SELECT * FROM punishments WHERE player_name = ? AND punishment_type = 'TEMPBAN' AND active = TRUE";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, playerName);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isBannedIP(String ip) {
        String query = "SELECT * FROM punishments WHERE player_name = ? AND punishment_type = 'BANIP' AND active = TRUE";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, ip);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isMutted(String playerName) {
        String query = "SELECT * FROM punishments WHERE player_name = ? AND punishment_type = 'MUTE' AND active = TRUE";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, playerName);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isTempMutted(String playerName) {
        String query = "SELECT * FROM punishments WHERE player_name = ? AND punishment_type = 'TEMPMUTE' AND active = TRUE";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, playerName);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void checkAndRemoveExpiredPunishments() {
        String query = "SELECT * FROM punishments WHERE expiry_time < ? AND active = TRUE";
        LocalDateTime now = LocalDateTime.now();

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, now.format(DATE_TIME_FORMATTER));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String playerName = rs.getString("player_name");
                PunishmentType punishmentType = PunishmentType.valueOf(rs.getString("punishment_type"));

                updateData(playerName, punishmentType, null, null, null, false);

                Player player = Bukkit.getPlayer(playerName);
                if (player != null && player.getName().equalsIgnoreCase(playerName)) {
                    File fileConfig = new File("plugins/MrPunish/config.yml");
                    YamlConfiguration config = YamlConfiguration.loadConfiguration(fileConfig);
                    String timeExpired = rs.getString("expiry_time");
                    String remainingTime = getRemainingTime(now, LocalDateTime.parse(timeExpired, DATE_TIME_FORMATTER));
                    String message = PunishUtils
                            .formattedMessage(config.getString("expired_punishment"),
                                    player.getName(), rs.getString("punished_by"),
                                    rs.getString("reason"),
                                    "",
                                    remainingTime);

                    player.sendMessage(message);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<PunishmentEntity> getData(String playerName, PunishmentType punishmentType, boolean active){
        String query = "SELECT * FROM punishments WHERE player_name = ? AND punishment_type = ? AND active = ?";
        List<PunishmentEntity> punishments = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, playerName);
            stmt.setString(2, punishmentType.toString());
            stmt.setBoolean(3, active);
            return executeQueryGetData(stmt, punishments);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return punishments;
    }

    public static List<PunishmentEntity> getData(String playerName){
        String query = "SELECT * FROM punishments WHERE player_name = ?";
        List<PunishmentEntity> punishments = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, playerName);
            return executeQueryGetData(stmt, punishments);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return punishments;
    }

    public static void updateData(@Nullable String playerName, @Nullable PunishmentType punishmentType, String reason, String punishedBy, LocalDateTime expiryTime, Boolean active){
        StringBuilder query = new StringBuilder("UPDATE punishments SET ");

        if (reason != null) {
            query.append("reason = ?, ");
        }
        if (punishedBy != null) {
            query.append("punished_by = ?, ");
        }
        if (expiryTime != null) {
            query.append("expiry_time = ?, ");
        }
        if (active != null) {
            query.append("active = ?, ");
        }

        query.setLength(query.length() - 2);
        query.append(" WHERE player_name = ? AND punishment_type = ?");

        try (PreparedStatement stmt = connection.prepareStatement(query.toString())) {
            int parameterIndex = 1;
            if (reason != null) stmt.setString(parameterIndex++, reason);
            if (punishedBy != null) stmt.setString(parameterIndex++, punishedBy);
            if (expiryTime != null) stmt.setString(parameterIndex++, expiryTime.format(DATE_TIME_FORMATTER));
            if (active != null) stmt.setBoolean(parameterIndex++, active);
            stmt.setString(parameterIndex++, playerName);
            stmt.setString(parameterIndex, punishmentType.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveData(String playerName, PunishmentType punishmentType, String reason, String punishedBy, LocalDateTime expiryTime, boolean active){
        String query = "INSERT INTO punishments (player_name, punishment_type, reason, punished_by, date_punishment, expiry_time, active) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, playerName);
            stmt.setString(2, punishmentType.toString());
            stmt.setString(3, reason);
            stmt.setString(4, punishedBy);
            stmt.setString(5, LocalDateTime.now().format(DATE_TIME_FORMATTER));
            if (expiryTime == null) stmt.setNull(6, Types.NULL);
            else stmt.setString(6, expiryTime.format(DATE_TIME_FORMATTER));
            stmt.setBoolean(7, active);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static List<PunishmentEntity> executeQueryGetData(PreparedStatement stmt, List<PunishmentEntity> punishments) throws SQLException {
        ResultSet rs = stmt.executeQuery();
        while(rs.next()){

            String expirtyTime = rs.getString("expiry_time");

            PunishmentEntity punishment = new PunishmentEntity(
                    rs.getInt("id"),
                    rs.getString("player_name"),
                    PunishmentType.valueOf(rs.getString("punishment_type")),
                    rs.getString("reason"),
                    rs.getString("punished_by"),
                    LocalDateTime.parse(rs.getString("date_punishment"), DATE_TIME_FORMATTER),
                    expirtyTime == null ? "permanent" : LocalDateTime.parse(expirtyTime, DATE_TIME_FORMATTER).format(DATE_TIME_FORMATTER),
                    rs.getBoolean("active")
            );
            punishments.add(punishment);
        }
        return punishments;
    }
}
