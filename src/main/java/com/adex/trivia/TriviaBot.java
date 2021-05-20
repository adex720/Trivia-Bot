package com.adex.trivia;

import com.adex.trivia.commands.Invite;
import com.adex.trivia.commands.Prefix;
import com.adex.trivia.commands.Trivia;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class TriviaBot {

    public static Dotenv DOTENV = Dotenv.load();

    public final JDA jda;
    public final Logger logger;
    public final QuestionAsker questionAsker;

    public final PrefixList prefixList;
    public final Profiles profiles;

    public final DiscordListener discordListener;

    private final Timer timer;

    public TriviaBot(String token) throws LoginException, InterruptedException {
        discordListener = new DiscordListener(this);

        logger = LoggerFactory.getLogger(TriviaBot.class);

        this.jda = JDABuilder.createDefault(token)
                .setActivity(Activity.watching("-help"))
                .addEventListeners(discordListener)
                .build()
                .awaitReady();

        profiles = new Profiles(DOTENV.get("PROFILE_PATH"), logger);

        Question.load(logger);
        prefixList = new PrefixList("-", DOTENV.get("PREFIX_PATH"), logger);
        questionAsker = new QuestionAsker();

        loadCommands();

        timer = new Timer();
    }

    private void loadCommands() {
        discordListener.commands.add(new Trivia(this));
        discordListener.commands.add(new Invite(this));
        discordListener.commands.add(new Prefix(this));
    }

    public static void main(String[] args) throws LoginException, InterruptedException {
        TriviaBot bot = new TriviaBot(DOTENV.get("TOKEN"));

        Scanner scanner = new Scanner(System.in);
        String prefixPath = DOTENV.get("PREFIX_PATH");
        String profilePath = DOTENV.get("PROFILE_PATH");


        final int delay = 600000; // 10 minutes
        final TimerTask save = new TimerTask() {
            @Override
            public void run() {
                bot.prefixList.save(prefixPath, bot.logger);
                bot.profiles.save(profilePath, bot.logger);
            }
        };

        bot.timer.schedule(save, delay, delay);

    }
}
