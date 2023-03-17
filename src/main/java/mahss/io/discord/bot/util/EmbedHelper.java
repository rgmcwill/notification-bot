package mahss.io.discord.bot.util;


import mahss.io.discord.bot.model.Notification;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

public class EmbedHelper {

    public MessageEmbed createEmbed(Notification n) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle(capitalize(n.getSubType().toString()), null);

        embedBuilder.setColor(stringToColour(n.getType()));

        embedBuilder.setDescription(n.getMessage());

//        embedBuilder.addField("Title of field", "test of field", false);

//        embedBuilder.addBlankField(false);

        embedBuilder.setAuthor(n.getType(), null, "https://n9e5v4d8.ssl.hwcdn.net/images/longlanding/lotusIcon.jpg");

        return embedBuilder.build();
    }


    private Color stringToColour(String str) {
        int hash = 0;
        for (int i = 0; i < str.length(); i++) {
            hash = str.charAt(i) + ((hash << 5) - hash);
        }
        String colour = "";
        for (int i = 0; i < 3; i++) {
            int value = (hash >> (i * 8)) & 0xFF;
            colour += String.format("%02x", value);
        }
        return new Color(Integer.parseInt(colour, 16));
    }

    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        } else {
            return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
        }
    }
}

