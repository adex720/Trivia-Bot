package com.adex.trivia;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class QuestionAsker {

    public static final int TIME = 15000; // 15 seconds time to answer

    private final TriviaBot bot;

    public final HashMap<Long, Question.BuildQuestion> active_questions;
    private final Timer timer;


    public QuestionAsker(TriviaBot bot) {
        this.bot = bot;
        active_questions = new HashMap<>();
        timer = new Timer();
    }

    public MessageEmbed commandRan(MessageChannel channel, User user) {
        long userId = user.getIdLong();

        if (!active_questions.containsKey(userId)) {
            final Question.BuildQuestion question = Question.getRandom();
            channel.sendMessage(question.message).queue(message -> addQuestion(userId, question));
            bot.profiles.triviaAsked(userId);
        } else {
            return new EmbedBuilder()
                    .setTitle("TRIVIA")
                    .addField("Please be patient", "<@!" + userId + "> please answer your current trivia before starting a new one!", false)
                    .setColor(Color.red)
                    .setFooter(user.getName(), user.getAvatarUrl())
                    .setTimestamp(new Date().toInstant())
                    .build();
        }

        return null;
    }

    public void questionAnswered(MessageChannel channel, int answer, User user) {
        long userId = user.getIdLong();

        Question.BuildQuestion question = active_questions.get(userId);
        if (question != null) {
            bot.profiles.triviaAnswered(userId, question.correctId == answer, question.difficulty);

            if (question.correctId == answer) {
                channel.sendMessage(new EmbedBuilder()
                        .setTitle("TRIVIA")
                        .addField("congratulations ", "<@!" + userId + "> Your answer was right", false)
                        .setColor(Color.green)
                        .setFooter(user.getName(), user.getAvatarUrl())
                        .setTimestamp(new Date().toInstant())
                        .build()).queue();
            } else {
                channel.sendMessage(new EmbedBuilder()
                        .setTitle("TRIVIA")
                        .addField("Wrong answer ", "<@!" + userId + "> The correct answer was `" + question.correctAnswer + "`.", false)
                        .setColor(Color.red)
                        .setFooter(user.getName(), user.getAvatarUrl())
                        .setTimestamp(new Date().toInstant())
                        .build()).queue();
            }

            active_questions.remove(userId);
        }
    }

    public void addQuestion(long userId, Question.BuildQuestion question) {
        active_questions.put(userId, question);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                active_questions.remove(userId, question);
            }
        }, TIME);
    }
}
