package com.adex.trivia.commands;

import com.adex.trivia.TriviaBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.Date;

public class Invite extends Command {

    public static final String inviteLink = TriviaBot.DOTENV.get("INVITE");

    public Invite(TriviaBot bot) {
        super(bot, "invite", "Creates an invite for inviting the bot into other severs.");
    }

    @Override
    public MessageEmbed execute(MessageReceivedEvent event) {
        return new EmbedBuilder()
                .setTitle("INVITE")
                .addField("Add me to your server with this link.",
                        inviteLink, false)
                .setColor(Color.white)
                .setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl())
                .setThumbnail(thumbnail)
                .setTimestamp(new Date().toInstant())
                .build();
    }

}
