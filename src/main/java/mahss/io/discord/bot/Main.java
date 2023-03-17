package mahss.io.discord.bot;

import mahss.io.discord.bot.listener.CommandListenerAdapter;
import mahss.io.discord.bot.listener.OnReadyListenerAdapter;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class Main {

    private static final String TOKEN_DEFAULT = System.getenv("NOTIFICATION_BOT_TOKEN");

    public static void main(String[] args) {
        JDABuilder.createDefault(TOKEN_DEFAULT)
                .disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE) // Disable parts of the cache
                .setBulkDeleteSplittingEnabled(false) // Enable the bulk delete event
                .setCompression(Compression.NONE) // Disable compression (not recommended)
                .setActivity(Activity.watching("for things to notify you of")) // Set activity (like "playing Something")
                .addEventListeners(new OnReadyListenerAdapter())
                .addEventListeners(new CommandListenerAdapter())
                .build();
    }
}