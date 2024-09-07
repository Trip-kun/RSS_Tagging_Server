package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {
    protected static final GsonBuilder gsonBuilder = new GsonBuilder();
    protected static final Gson gson = gsonBuilder.create();
    private static Config config;

    public long threadLimit = 50; // The maximum number of threads to use
    public long threadSleep = 100; // The time to sleep between thread checks

    public int version = 1; // The version of the database
    public String dbUrl = ""; // The URL of the database
    public String dbUser = ""; // The username of the database
    public String dbPass = ""; // The password of the database
    public String dbDatabase = ""; // The database to use
    public String webUrl = "http://localhost:8081"; // The URL of the web server
    public String emailHost = ""; // The host of the email server
    public String emailPort = ""; // The port of the email server
    public String emailUsername = ""; // The username of the email server
    public String emailPassword = ""; // The password of the email server
    public int dbPort = 3306; // The port of the database
    public int maxResponses = 10; // The maximum number of responses to store
    public int maxConnections = 10; // The maximum number of connections to the database
    public int maxRetries = 3; // The maximum number of retries to connect to the database
    public int retryDelay = 1000; // The delay between retries
    public int saltSize = 16; // The size of the salt
    public int hashCost = 12; // The cost of the hash
    public int authExpiry = 60 * 60 * 24 * 7; // The expiry time of the authentication token, in epoch seconds
    public static synchronized Config getConfig() {
        if (config!=null) return config;
        String configPath = System.getProperty("user.dir") + "/config.json";
        Path path = Path.of(configPath);
        boolean exists = Files.exists(path);
        try {
            if (!exists) {
                new FileOutputStream(configPath, true).close();
            }
            FileRW fileRW = new FileRW(path);
            String content = fileRW.read();
            if (content == null || content.isEmpty() || content.equals("null")) {
                config = new Config();
                fileRW.write(gson.toJson(config));
            } else {
                config = gson.fromJson(content, Config.class);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read config file", e);
        }
        return config;
    }
}
