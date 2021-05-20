package com.adex.trivia;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PrefixList {

    public final HashMap<Long, String> prefixes = new HashMap<>();

    public String defaultPrefix;

    public PrefixList(String defaultPrefix, String path, Logger logger) {
        this.defaultPrefix = defaultPrefix;
        load(path, logger);
    }

    private void load(String path, Logger logger) {

        // loading json file
        try {
            File prefixFile = new File(path);

            Object obj = new JSONParser().parse(new FileReader(prefixFile));

            // reading data
            JSONArray jsonArray = (JSONArray) obj;
            for (Object prefixObj : jsonArray) {
                JSONObject prefix = (JSONObject) prefixObj;

                long id = Long.parseLong(prefix.get("id").toString());
                prefixes.put(id, prefix.get("prefix").toString());
            }
        } catch (Exception exception) {
            logger.info(exception.getMessage() + ", " + Arrays.toString(exception.getStackTrace()));
        }

        logger.info("Loaded all prefixes!");
    }

    @SuppressWarnings("unchecked")
    public void save(String path, Logger logger) {
        // generating json file
        JSONArray json = new JSONArray();

        for (Map.Entry<Long, String> pair : prefixes.entrySet()) {
            JSONObject prefix = new JSONObject();

            prefix.put("id", pair.getKey());
            prefix.put("prefix", pair.getValue());
            json.add(prefix);
        }

        // writing to file
        try {
            File prefixFile = new File(path);

            FileWriter fileWriter = new FileWriter(prefixFile);

            fileWriter.write(json.toJSONString());
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            logger.info("""
                    Failed to write prefixes file!
                    Reason: {}
                    StackTrace: {}""", e.getMessage(), e.getStackTrace());
        }

        logger.info("Prefix list updated and saved!");
    }

    public String getPrefix(long id) {
        String prefix = prefixes.get(id);
        return prefix == null ? defaultPrefix : prefix;
    }

    public void setPrefix(long guildId, String prefix) {
        if (prefix.equals(defaultPrefix)) {
            prefixes.remove(guildId);
        } else {
            prefixes.put(guildId, prefix);
        }
    }
}
