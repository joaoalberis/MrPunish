package com.github.joaoalberis.mrpunish.database;

import com.github.joaoalberis.mrpunish.Enum.PunishmentType;

import java.time.LocalDateTime;

public class PunishmentEntity {
    private int id;
    private String playerName;
    private PunishmentType punishmentType;
    private String reason;
    private String punishedBy;
    private LocalDateTime datePunishment;
    private String expiryTime;
    private boolean active;

    public PunishmentEntity(int id, String playerName, PunishmentType punishmentType,
                      String reason, String punishedBy, LocalDateTime datePunishment, String expiryTime, boolean active) {
        this.id = id;
        this.playerName = playerName;
        this.punishmentType = punishmentType;
        this.reason = reason;
        this.punishedBy = punishedBy;
        this.datePunishment = datePunishment;
        this.expiryTime = expiryTime;
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public PunishmentType getPunishmentType() {
        return punishmentType;
    }

    public void setPunishmentType(PunishmentType punishmentType) {
        this.punishmentType = punishmentType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getPunishedBy() {
        return punishedBy;
    }

    public void setPunishedBy(String punishedBy) {
        this.punishedBy = punishedBy;
    }

    public LocalDateTime getDatePunishment() {
        return datePunishment;
    }

    public void setDatePunishment(LocalDateTime datePunishment) {
        this.datePunishment = datePunishment;
    }

    public String getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(String expiryTime) {
        this.expiryTime = expiryTime;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

