package mahss.io.discord.bot.model;

import java.time.LocalDateTime;

public class NotificationData {
    private String id;
    private LocalDateTime expire;

    public NotificationData(String id, LocalDateTime expire) {
        this.id = id;
        this.expire = expire;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getExpire() {
        return expire;
    }
}
