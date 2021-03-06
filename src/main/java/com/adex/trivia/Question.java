package com.adex.trivia;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Question {

    private static final ArrayList<Question> QUESTIONS = new ArrayList<>();

    public final String question;
    public final String[] answers; // First one is correct answer
    public final Category category;
    public final Difficulty difficulty;
    public final int id;

    public Question(String question, String[] answers, Category category, Difficulty difficulty) {
        id = QUESTIONS.size();

        this.question = question;
        this.answers = answers;
        this.category = category;
        this.difficulty = difficulty;
        QUESTIONS.add(this);
    }

    public static Question fromJson(JSONObject json) {
        String question = Objects.toString(json.get("question"));
        JSONArray answersJson = (JSONArray) json.get("answers");
        String category = Objects.toString(json.get("category"));
        String difficulty = Objects.toString(json.get("difficulty"));

        int i = 0;
        int size = answersJson.size();
        String[] answers = new String[size];
        for (Object answer : answersJson) {
            answers[i] = Objects.toString(answer);

            i++;
        }

        return new Question(question,
                answers,
                Category.valueOf(category.toUpperCase()),
                Difficulty.valueOf(difficulty.toUpperCase()));
    }

    public static BuildQuestion getRandom(User user) {
        return new BuildQuestion(QUESTIONS.get(ThreadLocalRandom.current().nextInt(QUESTIONS.size())), user);
    }

    public static void load(Logger logger) {
        String[] paths = new String[]{"src/main/resources/questions/biology.json",
                "src/main/resources/questions/geography.json",
                "src/main/resources/questions/chemistry.json",
                "src/main/resources/questions/physics.json",
                "src/main/resources/questions/history.json",
                "src/main/resources/questions/video_games.json",
                "src/main/resources/questions/music.json",
                "src/main/resources/questions/technology.json",
                "src/main/resources/questions/culture.json",
                "src/main/resources/questions/language.json",
                "src/main/resources/questions/sport.json",
                "src/main/resources/questions/miscellaneous.json"};

        for (String path : paths) {
            try {
                File file = new File(path);
                FileReader reader = new FileReader(file);

                Object obj = new JSONParser().parse(reader);
                JSONArray json = (JSONArray) obj;

                int amount = 0;
                for (Object object : json) {
                    try {
                        Question question = Question.fromJson((JSONObject) object);
                        logger.info("Loaded question no. {} for category {} as difficulty {}", amount, question.category, question.difficulty);
                    } catch (Exception e) {
                        logger.info("""
                                Failed to load question id {} , getting next question with same id
                                Reason: {}
                                StackTrace: {}""", amount, e.getMessage(), e.getStackTrace());
                        continue;
                    }
                    amount++;
                }
            } catch (NullPointerException | IOException e) {
                logger.info("Failed to find file: " + path);
            } catch (ParseException e) {
                logger.info("Invalid json syntax on file " + path);
            }
            logger.info("Loaded all questions from " + path);
        }
    }

    public enum Category {

        BIOLOGY("39ab00"), GEOGRAPHY("012191"), CHEMISTRY("00ddcd"),
        PHYSICS("570101"), HISTORY("474747"), VIDEO_GAMES("e85858"),
        MUSIC("cc9808"), TECHNOLOGY("4f2b02"), CULTURE("66048a"),
        LANGUAGE("28de92"), SPORT("eb7831"), MISCELLANEOUS("bfbfbf"), EVENT("c77b08");

        /**
         * physics        : dark red
         * video games    : light red
         * technology     : brown
         * sports         : orange
         * music          : yellow
         * biology        : green
         * chemistry      : light turquoise
         * language       : turquoise
         * geography      : dark blue
         * culture        : dark purple
         * history        : dark gray
         * miscellaneous  : light gray
         * <p>
         * event          : varies with event
         */
        public final Color color;

        Category(String colorHex) {
            color = new Color(
                    Integer.valueOf(colorHex.substring(0, 2), 16),
                    Integer.valueOf(colorHex.substring(2, 4), 16),
                    Integer.valueOf(colorHex.substring(4, 6), 16));
        }
    }

    public enum Difficulty {

        EASY(20), MEDIUM(25), ADVANCED(30), HARD(35);

        public final int prize;

        Difficulty(int prize) {
            this.prize = prize;
        }

    }

    public static class BuildQuestion {

        public final MessageEmbed message;
        public final int correctId; // A=0 ; B=1 ; C=2 ; D=3
        public final String correctAnswer;
        public final Difficulty difficulty;

        public BuildQuestion(Question question, User user) {
            correctAnswer = question.answers[0];
            List<String> shuffledAnswers = Arrays.asList(Arrays.copyOf(question.answers, 4));
            Collections.shuffle(shuffledAnswers);

            correctId = shuffledAnswers.indexOf(correctAnswer);
            difficulty = question.difficulty;

            message = new EmbedBuilder()
                    .setColor(question.category.color)
                    .setTitle("TRIVIA")
                    .addField(question.question, "**A: **" + shuffledAnswers.get(0) +
                            "\n**B: **" + shuffledAnswers.get(1) + "\n**C: **" + shuffledAnswers.get(2) +
                            "\n**D: **" + shuffledAnswers.get(3) + "\nCategory: `" + question.category +
                            "` Difficulty: `" + difficulty + "`", false)
                    .setFooter(user.getName(), user.getAvatarUrl())
                    .setTimestamp(new Date().toInstant())
                    .build();
        }
    }

}
