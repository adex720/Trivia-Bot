package com.adex.trivia.commands;

import com.adex.trivia.TriviaBot;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Trivia extends Command {

    public Trivia(TriviaBot bot) {
        super(bot, "trivia", "Asks you a trivia");
    }

    @Override
    public MessageEmbed execute(MessageReceivedEvent event) {
        return bot.questionAsker.commandRan(event.getChannel(), event.getAuthor().getIdLong());
    }

    @Override
    public String[] getAliases() {
        return new String[]{"triv"};
    }
}
