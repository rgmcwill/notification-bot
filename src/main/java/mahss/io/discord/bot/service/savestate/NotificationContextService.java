package mahss.io.discord.bot.service.savestate;

import mahss.io.discord.bot.model.ContextData;

import java.util.List;

public interface NotificationContextService {

    List<ContextData> readNotificationContext();

    void writeNotificationContext(List<ContextData> data);
}
