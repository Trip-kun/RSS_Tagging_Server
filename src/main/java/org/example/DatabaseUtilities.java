package org.example;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.example.data.*;

import java.sql.SQLException;

public class DatabaseUtilities {
    private static final int VERSION = 1;
    private static JdbcPooledConnectionSource connectionSource;
    private static Dao<User, Integer> userDao;
    private static Dao<Authentication, Integer> authenticationDao;
    private static Dao<Channel, Integer> channelDao;
    private static Dao<Entry, Integer> entryDao;
    private static Dao<UserChannelLink, Integer> userChannelLinkDao;
    private static Dao<Filter, Integer> filterDao;
    private static Dao<FilterChannelLink, Integer> filterChannelLinkDao;
    private static Dao<FilterEntryLink, Integer> filterEntryLinkDao;
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
        tries = 0;
        while (tries<config.maxRetries) {
            try {
                TableUtils.createTableIfNotExists(connectionSource, Channel.class);
                channelDao = DaoManager.createDao(connectionSource, Channel.class);
                channelDao.setObjectCache(true);
                break;
            } catch (SQLException e) {
                tries++;
                try {
                    Thread.sleep(config.retryDelay);
                } catch (InterruptedException ignored) {
                }
            }
        }
        if (channelDao==null) {
            throw new RuntimeException("Failed to create DAO for Channel");
        }
        tries = 0;
        while (tries<config.maxRetries) {
            try {
                TableUtils.createTableIfNotExists(connectionSource, Entry.class);
                entryDao = DaoManager.createDao(connectionSource, Entry.class);
                entryDao.setObjectCache(true);
                break;
            } catch (SQLException e) {
                tries++;
                try {
                    Thread.sleep(config.retryDelay);
                } catch (InterruptedException ignored) {
                }
            }
        }
        if (entryDao==null) {
            throw new RuntimeException("Failed to create DAO for Entry");
        }
        tries = 0;
        while (tries<config.maxRetries) {
            try {
                TableUtils.createTableIfNotExists(connectionSource, UserChannelLink.class);
                userChannelLinkDao = DaoManager.createDao(connectionSource, UserChannelLink.class);
                userChannelLinkDao.setObjectCache(true);
                break;
            } catch (SQLException e) {
                tries++;
                try {
                    Thread.sleep(config.retryDelay);
                } catch (InterruptedException ignored) {
                }
            }
            tries = 0;
            while (tries<config.maxRetries) {
                try {
                    TableUtils.createTableIfNotExists(connectionSource, Filter.class);
                    filterDao = DaoManager.createDao(connectionSource, Filter.class);
                    filterDao.setObjectCache(true);
                    break;
                } catch (SQLException e) {
                    tries++;
                    try {
                        Thread.sleep(config.retryDelay);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
            if (filterDao==null) {
                throw new RuntimeException("Failed to create DAO for Filter");
            }
            tries = 0;
            while (tries<config.maxRetries) {
                try {
                    TableUtils.createTableIfNotExists(connectionSource, FilterChannelLink.class);
                    filterChannelLinkDao = DaoManager.createDao(connectionSource, FilterChannelLink.class);
                    filterChannelLinkDao.setObjectCache(true);
                    break;
                } catch (SQLException e) {
                    tries++;
                    try {
                        Thread.sleep(config.retryDelay);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
            if (filterChannelLinkDao==null) {
                throw new RuntimeException("Failed to create DAO for FilterChannelLink");
            }
            tries = 0;
            while (tries<config.maxRetries) {
                try {
                    TableUtils.createTableIfNotExists(connectionSource, FilterEntryLink.class);
                    filterEntryLinkDao = DaoManager.createDao(connectionSource, FilterEntryLink.class);
                    filterEntryLinkDao.setObjectCache(true);
                    break;
                } catch (SQLException e) {
                    tries++;
                    try {
                        Thread.sleep(config.retryDelay);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
            if (filterEntryLinkDao==null) {
                throw new RuntimeException("Failed to create DAO for FilterEntryLink");
            }
        }
        if (userChannelLinkDao==null) {
            throw new RuntimeException("Failed to create DAO for UserChannelLink");
        }
        tries = 0;
        while (tries<config.maxRetries) {
            try {
                TableUtils.createTableIfNotExists(connectionSource, Filter.class);
                filterDao = DaoManager.createDao(connectionSource, Filter.class);
                filterDao.setObjectCache(true);
                break;
            } catch (SQLException e) {
                tries++;
                try {
                    Thread.sleep(config.retryDelay);
                } catch (InterruptedException ignored) {
                }
            }
        }
        if (filterDao==null) {
            throw new RuntimeException("Failed to create DAO for Filter");
        }
        tries = 0;
        while (tries<config.maxRetries) {
            try {
                TableUtils.createTableIfNotExists(connectionSource, FilterChannelLink.class);
                filterChannelLinkDao = DaoManager.createDao(connectionSource, FilterChannelLink.class);
                filterChannelLinkDao.setObjectCache(true);
                break;
            } catch (SQLException e) {
                tries++;
                try {
                    Thread.sleep(config.retryDelay);
                } catch (InterruptedException ignored) {
                }
            }
        }
        if (filterChannelLinkDao==null) {
            throw new RuntimeException("Failed to create DAO for FilterChannelLink");
        }
        tries = 0;
        while (tries<config.maxRetries) {
            try {
                TableUtils.createTableIfNotExists(connectionSource, FilterEntryLink.class);
                filterEntryLinkDao = DaoManager.createDao(connectionSource, FilterEntryLink.class);
                filterEntryLinkDao.setObjectCache(true);
                break;
            } catch (SQLException e) {
                tries++;
                try {
                    Thread.sleep(config.retryDelay);
                } catch (InterruptedException ignored) {
                }
            }
        }
        if (filterEntryLinkDao==null) {
            throw new RuntimeException("Failed to create DAO for FilterEntryLink");
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
    public static Dao<Channel, Integer> getChannelDao() {
        return channelDao;
    }
    public static Dao<Entry, Integer> getEntryDao() {
        return entryDao;
    }
    public static Dao<UserChannelLink, Integer> getUserChannelLinkDao() {
        return userChannelLinkDao;
    }
    public static Dao<Filter, Integer> getFilterDao() {
        return filterDao;
    }
    public static Dao<FilterChannelLink, Integer> getFilterChannelLinkDao() {
        return filterChannelLinkDao;
    }
    public static Dao<FilterEntryLink, Integer> getFilterEntryLinkDao() {
        return filterEntryLinkDao;
    }
}
