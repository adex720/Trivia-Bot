package com.adex.trivia;

import com.adex.trivia.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class DiscordListener extends ListenerAdapter {

    private static final HashMap<Character, Integer> ANSWERS = new HashMap<>() {{
        put('A', 0);
        put('a', 0);
        put('B', 1);
        put('b', 1);
        put('C', 2);
        put('c', 2);
        put('D', 3);
        put('d', 3);
    }};

    public final ArrayList<Command> commands;

    private final TriviaBot bot;

    public DiscordListener(TriviaBot bot) {
        commands = new ArrayList<>();
        this.bot = bot;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        User user = event.getAuthor();
        if (!user.isBot()) {
            String message = event.getMessage().getContentRaw();

            String prefix = event.isFromGuild() ? bot.prefixList.getPrefix(event.getGuild().getIdLong())
                    : bot.prefixList.defaultPrefix;

            if (!message.startsWith(prefix)) {
                if (message.length() > 1) {
                    if (!message.startsWith("<@!" + bot.jda.getSelfUser().getIdLong())) {
                        return;
                    }
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("TRIVIA")
                            .setColor(getColorFromLong(user.getIdLong()))
                            .addField("Hello " + user.getAsTag() + "!", "My prefix on this server is `" +
                                    (event.isFromGuild() ? bot.prefixList.getPrefix(event.getGuild().getIdLong())
                                            : bot.prefixList.defaultPrefix) + "`", false)
                            .addField("To start trivia, type: ", "`" + prefix + "trivia` or `" + prefix + "triv`", false)
                            .setFooter(user.getName(), user.getAvatarUrl())
                            .setTimestamp(new Date().toInstant())
                            .build()).queue();
                    return;
                }
                if (message.length() != 0) { // message length can be 0 if it contains a file
                    char answer = message.charAt(0);
                    if (ANSWERS.containsKey(answer)) {
                        bot.questionAsker.questionAnswered(event.getChannel(), ANSWERS.get(answer), user);
                    }
                }
                return;
            }


            String first = event.getMessage().getContentRaw().split(" ")[0].substring(prefix.length());
            // !help I'm stuck -> help

            for (Command command : commands) {
                if (isRightCommand(command, first)) {
                    MessageEmbed reply = command.execute(event);
                    if (reply != null)
                        event.getChannel().sendMessage(reply).queue();
                }
            }
        }
    }

    public static boolean isRightCommand(Command command, String string) {
        string = string.toLowerCase();
        if (command.name.equals(string))
            return true;
        for (String alias : command.getAliases()) {
            if (alias.equals(string))
                return true;
        }

        return false;
    }

    public static Color getColorFromLong(long original) {

        int colorInt = (int) original % 16777216 - 1;  // 256^3 = 16777216

        int r = (colorInt) & 0xFF;
        int g = (colorInt >> 8) & 0xFF;
        int b = (colorInt >> 16) & 0xFF;

        return new Color(r, g, b);
    }
}
