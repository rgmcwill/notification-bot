package mahss.io.discord.bot.model;

import java.util.List;

public class User {
    private String id;
    private List<String> subscriptions;

    public User (final String id, final List<String> subscriptions) {
        this.id = id;
        this.subscriptions = subscriptions;
    }

    public String getId() {
        return id;
    }

    public List<String> getSubscriptions() {
        return subscriptions;
    }
}
