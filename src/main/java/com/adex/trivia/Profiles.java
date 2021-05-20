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
import java.util.Map;

public class Profiles {

    public final HashMap<Long, Integer> balance;

    public Profiles(String pathToJson, Logger logger) {

        balance = new HashMap<>();

        try {
            File prefixFile = new File(pathToJson);

            Object obj = new JSONParser().parse(new FileReader(prefixFile));

            // reading data
            JSONArray json = (JSONArray) obj;
            for (Object object : json) {
                JSONObject profile = (JSONObject) object;
                balance.put((long) profile.get("id"),
                        (int) (long) profile.get("balance"));
            }
        } catch (Exception exception) {
            logger.info(exception.getMessage() + ", " + Arrays.toString(exception.getStackTrace()));
        }

        logger.info("Loaded all prefixes!");


    }

    @SuppressWarnings("unchecked")
    public void save(String path, Logger logger) {
        JSONArray json = new JSONArray();
        for (Map.Entry<Long, Integer> entry : balance.entrySet()) {
            JSONObject profileJson = new JSONObject();

            profileJson.put("id", entry.getKey());
            profileJson.put("balance", entry.getValue());

            json.add(profileJson);
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

        logger.info("Saved all connect profiles!");
    }
}
