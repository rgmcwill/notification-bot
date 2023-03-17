package mahss.io.discord.bot;

import mahss.io.discord.bot.model.ContextData;
import mahss.io.discord.bot.model.Notification;
import mahss.io.discord.bot.service.savestate.NotificationContextService;
import mahss.io.discord.bot.service.savestate.SubscriptionContextService;
import mahss.io.discord.bot.util.EmbedHelper;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;

import java.util.*;

public class NotificationManager {

    private static final String MESSAGE_FORMAT = "_ _\n**%s**%s";
    private static JDA jda;
    private static NotificationContextService notificationContextService;
    private static SubscriptionContextService subscriptionContextService;
    private static EmbedHelper embedHelper;
    private Stack<Notification> notifications;

    public NotificationManager(JDA jda, NotificationContextService notificationContextService, SubscriptionContextService subscriptionContextService) {
        this.jda = jda;
        this.notificationContextService = notificationContextService;
        this.subscriptionContextService = subscriptionContextService;
        this.embedHelper = new EmbedHelper();
        notifications = new Stack<>();
    }

    public void sendNotifications() {
        List<ContextData> notificationsContext = notificationContextService.readNotificationContext();
        Map<String, Map<String, Map<String, Collection>>> subscriptionContext = subscriptionContextService.readSubscriptionContext();
        while (!notifications.empty()) {
            Notification notification = notifications.pop();
            Optional<ContextData> context = notificationsContext.stream().filter(c -> c.id().equals(notification.getId())).findFirst();
            List<String> recipientsList = context.isPresent() ? context.get().recipients() : new ArrayList<>();
            if (!notificationsContext.stream().anyMatch(c -> c.id().equals(notification.getId()))) {
                subscriptionContext.keySet().stream()
                        //TODO: FIX ME
                        .filter(userId -> {
                            List<String> simpleSubs = ((ArrayList) subscriptionContext.get(userId).get(notification.getType().toUpperCase(Locale.ROOT)).get("basic"));
                            return simpleSubs.contains(notification.getSubType().toString());
                        })
                        .forEach(userId -> {
                            sendEmbed(getUserById(userId).get(), notification);
                            recipientsList.add(userId);
                        });
                notificationsContext.add(new ContextData(notification.getId(), notification.getExpiry(), recipientsList));
            } else {
                subscriptionContext.keySet().stream()
                        //TODO: FIX ME
                        .filter(userId -> !context.get().recipients().contains(userId) && ((ArrayList) subscriptionContext.get(userId).get(notification.getType().toUpperCase(Locale.ROOT)).get("basic")).contains(notification.getSubType().toString()))
                        .forEach(userId -> {
                            sendEmbed(getUserById(userId).get(), notification);
                            recipientsList.add(userId);
                        });
            }
        }
        notificationContextService.writeNotificationContext(notificationsContext);
    }

    public void addNotification(final Notification notificationContent) {
        notifications.push(notificationContent);
    }


    private boolean checkUserSubscriptions(String userId) {
//        subscriptionContext.get(userId).get(notificationContent.getType().toLowerCase()).contains(notificationContent.getSubType().toString())
        return false;
    }

    private Optional<User> getUserById(final String userId) {
        CacheRestAction<User> cachedUser = jda.retrieveUserById(userId);
        User test = cachedUser.useCache(true).complete();
        return Optional.of(test);
    }

    private void sendMessage(User user, String from, String message) {
        String content = String.format(MESSAGE_FORMAT, from, message);
        user.openPrivateChannel()
                .flatMap(channel -> channel.sendMessage(content))
                .queue();
    }

    private void sendMessage(User user, Notification n) {
        String content = String.format(MESSAGE_FORMAT, n.getType(), n.getMessage());
        user.openPrivateChannel()
                .flatMap(channel -> channel.sendMessage(content))
                .queue();
    }

    private void sendEmbed(User user, Notification n) {
        user.openPrivateChannel()
                .flatMap(channel -> {
                    MessageCreateAction message = channel.sendMessageEmbeds(embedHelper.createEmbed(n));
                    return message;
                }).queue((message -> {
                    message.addReaction(Emoji.fromFormatted("heart"));
                }));
    }
}
