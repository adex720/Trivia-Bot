package com.adex.trivia;

import com.adex.trivia.commands.Profile;
import com.adex.trivia.commands.*;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.util.Timer;
import java.util.TimerTask;

public class TriviaBot {

    public static Dotenv DOTENV = Dotenv.load();

    public static final int DELAY = 600000; // 10 minutes

    private static final String PREFIX_PATH = DOTENV.get("PREFIX_PATH");
    private static final String PROFILE_PATH = DOTENV.get("PROFILE_PATH");

    public final JDA jda;
    public final Logger logger;
    public final QuestionAsker questionAsker;

    public final PrefixList prefixList;
    public final Profiles profiles;

    public final DiscordListener discordListener;

    private final Timer timer;


    private final Trivia trivia;
    private final Invite invite;
    private final Server server;
    private final Github github;
    private final Profile profile;
    private final Prefix prefix;
    private final Leaderboard leaderboard;
    private final Help help;


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
        questionAsker = new QuestionAsker(this);


        trivia = new Trivia(this);
        invite = new Invite(this);
        server = new Server(this);
        github = new Github(this);
        profile = new Profile(this);
        prefix = new Prefix(this);
        leaderboard = new Leaderboard(this);
        help = new Help(this);

        loadCommands();

        timer = new Timer();
    }

    private void loadCommands() {
        discordListener.commands.add(trivia);
        discordListener.commands.add(invite);
        discordListener.commands.add(server);
        discordListener.commands.add(github);
        discordListener.commands.add(profile);
        discordListener.commands.add(prefix);
        discordListener.commands.add(leaderboard);
        discordListener.commands.add(help);
    }

    public static void main(String[] args) throws LoginException, InterruptedException {
        TriviaBot bot = new TriviaBot(DOTENV.get("TOKEN"));

        TimerTask save = new TimerTask() {
            @Override
            public void run() {
                bot.save();
            }
        };

        bot.timer.schedule(save, DELAY, DELAY);
    }

    public void save() {
        prefixList.save(PREFIX_PATH, logger);
        profiles.save(PROFILE_PATH, logger);
    }

    public void stop() {
        timer.cancel();
        leaderboard.stopTimer();
    }
}
