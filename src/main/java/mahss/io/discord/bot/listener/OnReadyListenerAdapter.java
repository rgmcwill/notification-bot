package mahss.io.discord.bot.listener;

import mahss.io.discord.bot.NotificationManager;
import mahss.io.discord.bot.factory.notification.NotificationContentFactory;
import mahss.io.discord.bot.factory.notification.WarframeNotificationContentFactory;
import mahss.io.discord.bot.service.WarframeDataService;
import mahss.io.discord.bot.service.savestate.NotificationContextService;
import mahss.io.discord.bot.service.savestate.NotificationContextServiceImpl;
import mahss.io.discord.bot.service.savestate.SubscriptionContextService;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OnReadyListenerAdapter extends ListenerAdapter {

    private static final String NOTIFICATION_CONTEXT_FILE = "context.json";
    private static final String SUBSCRIPTION_CONTEXT_FILE = "subscriptions.json";
    private static final String BOT_RUNNING = "Notification Bot Running";
    private static final String WARFRAME_API_URL = "https://api.warframestat.us/pc?language=en";

    @Override
    public void onReady(ReadyEvent e) {
        System.out.println(BOT_RUNNING);

        NotificationContextService notificationContextService = new NotificationContextServiceImpl(NOTIFICATION_CONTEXT_FILE);
        SubscriptionContextService subscriptionContextService = new SubscriptionContextService(SUBSCRIPTION_CONTEXT_FILE);

        List<NotificationContentFactory> notificationFactories = new ArrayList<>();
        notificationFactories.add(new WarframeNotificationContentFactory(new WarframeDataService(WARFRAME_API_URL), subscriptionContextService));

        NotificationManager notificationManager = new NotificationManager(e.getJDA(), notificationContextService, subscriptionContextService);

        Set<String> subscriptions = subscriptionContextService.readSubscriptionContext().keySet();

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        Runnable task = () -> {
            try {
                notificationFactories.stream().forEach(factory -> {
                    factory.getNotificationsContent().stream().forEach(notificationContent -> {
                        notificationManager.addNotification(notificationContent);
                    });
                });
                notificationManager.sendNotifications();
            } catch (Exception exp) {
                exp.printStackTrace();
            }
        };


        executor.scheduleWithFixedDelay(task, 0, 1, TimeUnit.MINUTES);
    }


}
