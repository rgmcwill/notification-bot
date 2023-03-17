package mahss.io.discord.bot.model.warframe;

import java.time.LocalDateTime;
import java.util.List;

public class Bounty {
    private String id;
    private String name;
    private LocalDateTime expiry;
    private Syndicate syndicate;
    private int[] levels;
    private int stages;
    private List<String> rewards;

    public Bounty(String id, String name, LocalDateTime expiry, Syndicate syndicate, int[] levels, int stages, List<String> rewards) {
        this.id = id;
        this.name = name;
        this.expiry = expiry;
        this.syndicate = syndicate;
        this.levels = levels;
        this.stages = stages;
        this.rewards = rewards;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getExpiry() {
        return expiry;
    }

    public Syndicate getSyndicate() {
        return syndicate;
    }

    public int[] getLevels() {
        return levels;
    }

    public int getStages() {
        return stages;
    }

    public List<String> getRewards() {
        return rewards;
    }
}
