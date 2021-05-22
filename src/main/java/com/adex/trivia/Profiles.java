package com.adex.trivia;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashMap;

public class Profiles {

    public final HashMap<Long, Profile> profiles;

    public Profiles(String pathToJson, Logger logger) {

        profiles = new HashMap<>();

        load(pathToJson, logger);
    }

    private void load(String pathToJson, Logger logger) {
        try {
            File prefixFile = new File(pathToJson);

            Object obj = new JSONParser().parse(new FileReader(prefixFile));

            // reading data
            JSONArray json = (JSONArray) obj;
            for (Object object : json) {
                JSONObject profile = (JSONObject) object;

                long id = (long) profile.get("id");
                int balance = (int) (long) profile.get("balance");
                int triviasTotal = (int) (long) profile.get("total");
                int triviasCorrect = (int) (long) profile.get("correct");

                profiles.put((long) profile.get("id"), new Profile(id, balance, triviasTotal, triviasCorrect));
            }
        } catch (Exception exception) {
            logger.info(exception.getMessage() + ", " + Arrays.toString(exception.getStackTrace()));
        }

        logger.info("Loaded all profiles!");
    }

    @SuppressWarnings("unchecked")
    public void save(String path, Logger logger) {
        JSONArray json = new JSONArray();
        for (Profile profile : profiles.values()) {

            json.add(profile.asJson());
        }

        try {
            File file = new File(path);

            FileWriter fileWriter = new FileWriter(file);

            fileWriter.write(json.toJSONString());
            fileWriter.flush();
            fileWriter.close();

        } catch (Exception e) {
            logger.info("""
                    Failed to write profile file!
                    Reason: {}
                    StackTrace: {}""", e.getMessage(), e.getStackTrace());
        }

        logger.info("Saved all profiles!");
    }

    public Profile create(long id) {
        Profile profile = new Profile(id);
        profiles.put(id, profile);
        return profile;
    }

    public void triviaAnswered(long userId, boolean correct, Question.Difficulty difficulty) {
        if (correct) {
            Profile profile = profiles.get(userId); // No need to check if null because profile is created when new trivia is asked.
            profile.triviasCorrect++;
            profile.balance += difficulty.prize;
        }
    }
                        
    public void triviaAsked(long userId) {
        Profile profile = profiles.get(userId);
        if (profile == null) {
            profile = create(userId);
        }

        profile.triviasTotal++;
    }
}
