package com.adex.trivia.commands;

import com.adex.trivia.TriviaBot;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class Command {

    protected final TriviaBot bot;
    public final String thumbnail;

    public final String name;
    public final String description;
    public final String usage;


    protected Command(TriviaBot bot, String name, String description, String usage) {
        this.bot = bot;
        this.name = name;
        this.description = description;
        this.usage = usage;

        thumbnail = bot.jda.getSelfUser().getAvatarUrl();
    }

    protected Command(TriviaBot bot, String name, String description) {
        this.bot = bot;
        this.name = name;
        this.description = description;
        usage = name;

        thumbnail = bot.jda.getSelfUser().getAvatarUrl();
    }

    public abstract MessageEmbed execute(MessageReceivedEvent event);

    public String[] getAliases() {
        return new String[0];
    }

}
