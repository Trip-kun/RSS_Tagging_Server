package org.example

import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource
import com.j256.ormlite.table.TableUtils
import org.example.data.*
import java.sql.SQLException

private val config: Config = Config.getConfig()
private const val version: Int = 1
private var connectionSource: JdbcPooledConnectionSource? = null
lateinit var userDao: Dao<User, Int>
lateinit var channelDao: Dao<Channel, Int>
lateinit var entryDao: Dao<Entry, Int>
lateinit var authenticationDao: Dao<Authentication, Int>
lateinit var filterDao: Dao<Filter, Int>
lateinit var filterChannelLinkDao: Dao<FilterChannelLink, Int>
lateinit var filterEntryLinkDao: Dao<FilterEntryLink, Int>
lateinit var userChannelLinkDao: Dao<UserChannelLink, Int>
fun initDatabase() {
    var tries: Int = 0
    var success: Boolean = false
    while (!success) {
        try {
            connectionSource = JdbcPooledConnectionSource("jdbc:mariadb://" + config.databaseSettings.databaseURL + ":" + config.databaseSettings.databasePort + "/" + config.databaseSettings.databaseName, config.databaseSettings.databaseUser, config.databaseSettings.databasePassword)
            connectionSource?.setMaxConnectionsFree(config.databaseSettings.databaseMaxConnections)
            connectionSource?.initialize()
            TableUtils.createTableIfNotExists(connectionSource, User::class.java)
            userDao = DaoManager.createDao(connectionSource, User::class.java)
            userDao.setObjectCache(true)
            TableUtils.createTableIfNotExists(connectionSource, Channel::class.java)
            channelDao = DaoManager.createDao(connectionSource, Channel::class.java)
            channelDao.setObjectCache(true)
            TableUtils.createTableIfNotExists(connectionSource, Entry::class.java)
            entryDao = DaoManager.createDao(connectionSource, Entry::class.java)
            entryDao.setObjectCache(true)
            TableUtils.createTableIfNotExists(connectionSource, Authentication::class.java)
            authenticationDao = DaoManager.createDao(connectionSource, Authentication::class.java)
            authenticationDao.setObjectCache(true)
            TableUtils.createTableIfNotExists(connectionSource, Filter::class.java)
            filterDao = DaoManager.createDao(connectionSource, Filter::class.java)
            filterDao.setObjectCache(true)
            TableUtils.createTableIfNotExists(connectionSource, FilterChannelLink::class.java)
            filterChannelLinkDao = DaoManager.createDao(connectionSource, FilterChannelLink::class.java)
            filterChannelLinkDao.setObjectCache(true)
            TableUtils.createTableIfNotExists(connectionSource, FilterEntryLink::class.java)
            filterEntryLinkDao = DaoManager.createDao(connectionSource, FilterEntryLink::class.java)
            filterEntryLinkDao.setObjectCache(true)
            TableUtils.createTableIfNotExists(connectionSource, UserChannelLink::class.java)
            userChannelLinkDao = DaoManager.createDao(connectionSource, UserChannelLink::class.java)
            userChannelLinkDao.setObjectCache(true)
            success = true
        } catch (e: SQLException) {
            tries++
            if (tries >= config.databaseSettings.databaseMaxRetries) {
                throw e
            }
            Thread.sleep(config.databaseSettings.databaseRetryDelay.toLong())
        }
    }
}
fun upgrade() {
    if (config.version > version) {
        throw RuntimeException("Database version is newer than application version")
    }
    when(config.version) {
        1-> println("Nothing to do for upgrade")
    }
}