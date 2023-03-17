package mahss.io.discord.bot.model.warframe;

import java.time.LocalDateTime;

public class Mission {
    private Faction faction;
    private String reward;
    private String constraint;
    private Type type;

    private String celestialBody;
    private String nodeName;

    private LocalDateTime expiry;

    private boolean steelPath;
    private boolean railjack;


    public Mission(Faction faction, String reward, Type type, String constraint, String celestialBody, String nodeName, LocalDateTime expiry, boolean steelPath, boolean railjack) {
        this.faction = faction;
        this.reward = reward;
        this.type = type;
        this.constraint = constraint;
        this.celestialBody = celestialBody;
        this.nodeName = nodeName;
        this.expiry = expiry;
        this.steelPath = steelPath;
        this.railjack = railjack;
    }

    public Mission(Faction faction, String reward, Type type, String constraint, String celestialBody, String nodeName, LocalDateTime expiry) {
        this(faction, reward, type, constraint, celestialBody, nodeName, expiry, false, false);
    }

    public Faction getFaction() {
        return faction;
    }

    public String getReward() {
        return reward;
    }

    public Type getType() {
        return type;
    }

    public String getConstraint() {
        return constraint;
    }

    public String getCBody() {
        return celestialBody;
    }

    public String getNode() {
        return nodeName;
    }

    public LocalDateTime getExpiry() {
        return expiry;
    }

    public boolean isSteelPath() {
        return steelPath;
    }

    public boolean isRailjack() {
        return railjack;
    }

    public enum Type {
        ANCIENT_RETRIBUTION,
        ARENA,
        ASSASSINATION,
        ASSAULT,
        CAPTURE,
        CONCLAVE,
        DARK_SECTOR_DEFECTION,
        DARK_SECTOR_DEFENSE,
        DARK_SECTOR_DISRUPTION,
        DARK_SECTOR_EXCAVATION,
        DARK_SECTOR_SABOTAGE,
        DARK_SECTOR_SURVIVAL,
        DEFENSE,
        DISRUPTION,
        EXCAVATION,
        EXTERMINATION_ARCHWING,
        EXTERMINATION,
        FREE_ROAM,
        HIJACK,
        HIVE,
        HIVE_SABOTAGE,
        INTERCEPTION,
        INTERCEPTION_ARCHWING,
        MOBILE_DEFENSE,
        MOBILE_DEFENSE_ARCHWING,
        OROKIN_SABOTAGE,
        ORPHIX,
        PURSUIT_ARCHWING,
        RELAY,
        RESCUE,
        RUSH_ARCHWING,
        SABOTAGE,
        SABOTAGE_ARCHWING,
        SKIRMISH,
        SPY,
        SURVIVAL,
        VOLATILE,
        KUVA_SURVIVAL,
        CROSSFIRE
    }

    public enum Faction {
        OROKIN,
        CORRUPTED,
        INFESTED,
        CROSSFIRE,
        CORPUS,
        GRINEER,
        TENNO,
        NARMER
    }
}
