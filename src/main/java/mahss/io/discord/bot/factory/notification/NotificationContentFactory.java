package mahss.io.discord.bot.factory.notification;

import mahss.io.discord.bot.model.Notification;

import java.util.List;

public interface NotificationContentFactory {
    List<Notification> getNotificationsContent();

    interface SubType {
    }
}
