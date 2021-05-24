package com.adex.trivia.commands;

import com.adex.trivia.TriviaBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.Date;

public class Invite extends Command {

    public static final String INVITE_LINK =  "https://discord.com/oauth2/authorize?client_id=844453314526576660&permissions=2147797056&scope=bot";

    public Invite(TriviaBot bot) {
        super(bot, "invite", "Creates an invite for inviting the bot into other severs.");
    }

    @Override
    public MessageEmbed execute(MessageReceivedEvent event) {
        return new EmbedBuilder()
                .setTitle("INVITE")
                .addField("Add me to your server with this link.",
                        INVITE_LINK, false)
                .setColor(Color.white)
                .setFooter(event.getAuthor().getName(), event.getAuthor().getAvatarUrl())
                .setTimestamp(new Date().toInstant())
                .build();
    }

    @Override
    public String[] getAliases() {
        return  new String[]{"inv"};
    }
}
