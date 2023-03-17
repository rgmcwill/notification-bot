package mahss.io.discord.bot.listener;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class CommandListenerAdapter extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        // Create the EmbedBuilder instance
        EmbedBuilder eb = new EmbedBuilder();

        /*
            Set the title:
            1. Arg: title as string
            2. Arg: URL as string or could also be null
         */
        eb.setTitle("Alert", null);

        /*
            Set the color
         */
        eb.setColor(new Color(0x87d0fa));

        /*
            Set the text of the Embed:
            Arg: text as string
         */
        eb.setDescription("Mission: \n" +
                "> Defense - Mercury");

        /*
            Add fields to embed:
            1. Arg: title as string
            2. Arg: text as string
            3. Arg: inline mode true / false
         */
        eb.addField("Title of field", "test of field", false);

        /*
            Add spacer like field
            Arg: inline mode true / false
         */
        eb.addBlankField(false);

        /*
            Add embed author:
            1. Arg: name as string
            2. Arg: url as string (can be null)
            3. Arg: icon url as string (can be null)
         */
        eb.setAuthor("Warframe", null, "https://n9e5v4d8.ssl.hwcdn.net/images/longlanding/lotusIcon.jpg");

        Message msg = event.getMessage();

        if (msg.getContentRaw().equals("!ping")) {
            MessageChannel channel = event.getChannel();
            channel.sendMessageEmbeds(eb.build()).queue();
        }
//        if (msg.getContentRaw().equals("!ping")) {
//            MessageChannel channel = event.getChannel();
//            long time = System.currentTimeMillis();
//            channel.sendMessage("Pong!") /* => RestAction<Message> */
//                    .queue(response /* => Message */ -> {
//                        response.editMessageFormat("Pong: %d ms", System.currentTimeMillis() - time).queue();
//                    });
//        }
    }
}
