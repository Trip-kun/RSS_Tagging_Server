package org.example;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.example.data.Authentication;
import org.example.data.User;

import java.sql.SQLException;

public class DatabaseUtilities {
    private static final int VERSION = 1;
    private static JdbcPooledConnectionSource connectionSource;
    private static Dao<User, Integer> userDao;
    private static Dao<Authentication, Integer> authenticationDao;

    public static void init() {
        Config config = Config.getConfig();
        int tries = 0;
        boolean success = false;
        while (tries<config.maxRetries) {
            try {
                connectionSource = new JdbcPooledConnectionSource("jdbc:mariadb://" + config.dbUrl + ":" + config.dbPort + "/" + config.dbDatabase, config.dbUser, config.dbPass);
                connectionSource.setMaxConnectionsFree(config.maxConnections);
                connectionSource.initialize();
                success = true;
                break;
            } catch (SQLException e) {
                tries++;
                try {
                    Thread.sleep(config.retryDelay);
                } catch (InterruptedException ignored) {
                }
            }
        }
        if (!success) {
            throw new RuntimeException("Failed to connect to the database");
        }
        tries = 0;
        while (tries<config.maxRetries) {
            try {
                TableUtils.createTableIfNotExists(connectionSource, User.class);
                userDao = DaoManager.createDao(connectionSource, User.class);
                userDao.setObjectCache(true);
                break;
            } catch (SQLException e) {
                tries++;
                try {
                    Thread.sleep(config.retryDelay);
                } catch (InterruptedException ignored) {
                }
            }
        }
        if (userDao==null) {
            throw new RuntimeException("Failed to create DAO for User");
        }
        tries = 0;
        while (tries<config.maxRetries) {
            try {
                TableUtils.createTableIfNotExists(connectionSource, Authentication.class);
                authenticationDao = DaoManager.createDao(connectionSource, Authentication.class);
                authenticationDao.setObjectCache(true);
                break;
            } catch (SQLException e) {
                tries++;
                try {
                    Thread.sleep(config.retryDelay);
                } catch (InterruptedException ignored) {
                }
            }
        }
        if (authenticationDao==null) {
            throw new RuntimeException("Failed to create DAO for Authentication");
        }

    }
    public static void upgrade() {
        Config config = Config.getConfig();
        if (config.version>VERSION) {
            throw new RuntimeException("Database version is too high");
        }
        switch (config.version) {
            case 1:
                // Nothing to upgrade yet as we are on the first version
        }
    }

    public static Dao<User, Integer> getUserDao() {
        return userDao;
    }
    public static Dao<Authentication, Integer> getAuthenticationDao() {
        return authenticationDao;
    }
}
