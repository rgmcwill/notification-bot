package mahss.io.discord.bot.model.warframe;

import java.time.LocalDateTime;

public class Fissure extends Mission {

    private RelicType relicType;

    public Fissure(Faction faction, String reward, Type type, String constraint, String celestialBody, String nodeName, LocalDateTime expiry, RelicType relicType, boolean steelPath, boolean railjack) {
        super(faction, reward, type, constraint, celestialBody, nodeName, expiry, steelPath, railjack);
        this.relicType = relicType;
    }

    public RelicType getRelicType() {
        return relicType;
    }

    public enum RelicType {
        LITH,
        MESO,
        NEO,
        AXI,
        REQUIEM
    }
}
