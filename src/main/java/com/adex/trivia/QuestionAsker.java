package com.adex.trivia;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class QuestionAsker {

    public static final int TIME = 15000; // 15 seconds time to answer

    public final HashMap<Long, Question.BuildQuestion> active_questions;
    private final Timer timer;


    public QuestionAsker() {
        active_questions = new HashMap<>();
        timer = new Timer();
    }

    public MessageEmbed commandRan(MessageChannel channel, long userId) {

        if (!active_questions.containsKey(userId)) {
            final Question.BuildQuestion question = Question.getRandom();
            channel.sendMessage(question.message).queue(message -> addQuestion(userId, question));
            bot.profiles.triviaAsked(userId);
        } else {
            return new EmbedBuilder()
                    .setTitle("TRIVIA")
                    .addField("Please be patient", "<@!" + userId + "> please answer your current trivia before starting a new one!", false)
                    .setColor(Color.red)
                    .build();
        }

        return null;
    }

    public void questionAnswered(MessageChannel channel, int answer, long userId) {
        Question.BuildQuestion question = active_questions.get(userId);
        if (question != null) {
            bot.profiles.triviaAnswered(userId, question.correctId == answer, question.difficulty);
            
            if (question.correctId == answer) {
                channel.sendMessage(new EmbedBuilder()
                        .setTitle("TRIVIA")
                        .addField("congratulations ", "<@!" + userId + "> Your answer was right", false)
                        .setColor(Color.green)
                        .build()).queue();
            } else {
                channel.sendMessage(new EmbedBuilder()
                        .setTitle("TRIVIA")
                        .addField("Wrong answer ", "<@!" + userId + "> The correct answer was `" + question.correctAnswer + "`.", false)
                        .setColor(Color.red)
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
