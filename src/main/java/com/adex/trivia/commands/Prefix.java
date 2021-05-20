package com.adex.trivia.commands;

import com.adex.trivia.DiscordListener;
import com.adex.trivia.TriviaBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Prefix extends Command {

    public Prefix(TriviaBot bot) {
        super(bot, "prefix", "Allows an administrator to change the prefix for the server.", "prefix <new prefix>");
    }

    @Override
    public MessageEmbed execute(MessageReceivedEvent event) {
        if (event.getMessage().isFromGuild()) {
            if (event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                String message = event.getMessage().getContentRaw();
                int length = bot.prefixList.getPrefix(event.getGuild().getIdLong()).length() + 7;
                if (length < message.length()) { // typing only '-prefix' doesn't throw exception and trying to set it into nothing wont work
                    User user = event.getAuthor();
                    String newPrefix = message.substring(length);
                    bot.prefixList.setPrefix(event.getGuild().getIdLong(), newPrefix);
                    return new EmbedBuilder()
                            .setTitle("NEW PREFIX")
                            .setColor(DiscordListener.getColorFromLong(user.getIdLong()))
                            .addField(user.getAsTag() + " changed the prefix into `" + newPrefix + "`",
                                    "Example: `" + newPrefix + "trivia`", false)
                            .build();
                }
            }
        }

        return null;
    }
}
