package com.adex.trivia.commands;

import com.adex.trivia.Profile;
import com.adex.trivia.TriviaBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.*;

public class Leaderboard extends Command {

    public static final HashMap<String, Integer> CATEGORIES;

    public static final HashMap<Long, Long> COOLDOWNS = new HashMap<>();
    public static final int COOLDOWN = 60000;
    public static final int PER_PAGE = 20;

    private final Timer timer;

    static {
        CATEGORIES = new HashMap<>() {{
            put("trivias", 1);
            put("trivias_total", 1);
            put("trivias-total", 1);
            put("total", 1);
            put("t", 1);

            put("correct", 2);
            put("c", 2);
            put("right", 2);

            put("balance", 3);
            put("bal", 3);
            put("b", 3);
        }};
    }

    public Leaderboard(TriviaBot bot) {
        super(bot, "leaderboard", "Shows the leaderboard of a category", "leaderboard <category> [page]");

        timer = new Timer();
    }

    @Override
    public MessageEmbed execute(MessageReceivedEvent event) {
        User user = event.getAuthor();
        Long time = COOLDOWNS.get(user.getIdLong());

        if (time != null)
            return new EmbedBuilder()
                    .setTitle("LEADERBOARD")
                    .setColor(Color.red)
                    .addField("Please wait " + (COOLDOWN - (System.currentTimeMillis() - time)) / 1000 + " more seconds.",
                            "The cooldown is 1 minute", false)
                    .setFooter(user.getName(), user.getAvatarUrl())
                    .setTimestamp(new Date().toInstant())
                    .build();


        String[] args = event.getMessage().getContentRaw().split(" ");

        if (args.length == 1) {
            return new EmbedBuilder()
                    .setColor(Color.red)
                    .setTitle("LEADERBOARD")
                    .addField("Failed to show leaderboard", "Valid categories are `trivias`, `correct` and `balance`", false)
                    .setFooter(event.getAuthor().getName(), event.getAuthor().getAvatarUrl())
                    .setTimestamp(new Date().toInstant())
                    .build();
        }

        Integer categoryId = CATEGORIES.get(args[1].toLowerCase());

        if (categoryId == null) {
            return new EmbedBuilder()
                    .setColor(Color.red)
                    .setTitle("LEADERBOARD")
                    .addField("Failed to show leaderboard", "`" + args[1] + "`is not a valid category.\n" +
                            "Valid categories are `trivias`, `correct` and `balance`", false)
                    .setFooter(event.getAuthor().getName(), event.getAuthor().getAvatarUrl())
                    .setTimestamp(new Date().toInstant())
                    .build();
        }

        int page = 1;

        if (args.length > 2) {
            try {
                page = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                return new EmbedBuilder()
                        .setColor(Color.red)
                        .setTitle("LEADERBOARD")
                        .addField("Failed to show leaderboard", "Valid categories are `trivias`, `correct` and `balance`", false)
                        .setFooter(event.getAuthor().getName(), event.getAuthor().getAvatarUrl())
                        .setTimestamp(new Date().toInstant())
                        .build();
            }
        }

        if (page >= 1) {
            if ((bot.profiles.profiles.size() - 1) / PER_PAGE <= page) {

                COOLDOWNS.put(user.getIdLong(), System.currentTimeMillis());
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        COOLDOWNS.remove(user.getIdLong());
                    }
                }, COOLDOWN);

                ArrayList<Profile> profiles = bot.profiles.getLeaderboard(categoryId);
                Collections.reverse(profiles);

                EmbedBuilder embedBuilder = new EmbedBuilder()
                        .setTitle("LEADERBOARD")
                        .setColor(Color.white)
                        .setFooter(event.getAuthor().getName(), event.getAuthor().getAvatarUrl())
                        .setTimestamp(new Date().toInstant());

                StringBuilder text = new StringBuilder();
                page--;

                int amount = profiles.size() <= page * PER_PAGE ? PER_PAGE : profiles.size() % PER_PAGE;
                int start = page * PER_PAGE;
                int end = amount + start;

                for (int i = start; i < end; i++) {
                    Profile profile = profiles.get(i);
                    text.append("<@!").append(profile.id).append(">: ").append(profile.getValueById(categoryId)).append("\n");
                }

                Profile ownProfile = bot.profiles.getProfile(event.getAuthor().getIdLong());
                if (ownProfile != null) {
                    text.append("Your rank is ").append(profiles.indexOf(ownProfile) + 1).append(".");
                }

                return embedBuilder
                        .addField(args[1].toUpperCase(), text.toString(), false)
                        .build();
            }

            return new EmbedBuilder()
                    .setColor(Color.red)
                    .setTitle("LEADERBOARD")
                    .addField("Failed to show leaderboard", "There currently are`" +
                            (bot.profiles.profiles.size() - 1) / PER_PAGE + "` pages. Can't show page `" + page + "`.", false)
                    .setFooter(event.getAuthor().getName(), event.getAuthor().getAvatarUrl())
                    .setTimestamp(new Date().toInstant())
                    .build();
        }

        return new EmbedBuilder()
                .setColor(Color.red)
                .setTitle("LEADERBOARD")
                .addField("Failed to show leaderboard", "Page must be at least 1.", false)
                .setFooter(event.getAuthor().getName(), event.getAuthor().getAvatarUrl())
                .setTimestamp(new Date().toInstant())
                .build();
    }

    @Override
    public String[] getAliases() {
        return new String[]{"lb", "ranks"};
    }

    public void stopTimer() {
        timer.cancel();
    }

}
