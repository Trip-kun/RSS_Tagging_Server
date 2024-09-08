package org.example

import io.javalin.http.Context
import org.example.data.*
import java.net.URI
import java.util.*

fun createOrUpdateTag(context: Context) {
    val go = testAuthentication(context)
    if (!go) {
        return
    }
    val FilterName = context.headerMap()["TAG_name"]
    if (FilterName == null) {
        context.status(400)
        context.result("No filter name provided.")
        return
    }
    val filterDao = DatabaseUtilities.getFilterDao()
    var filter: Filter = Filter()
    filterDao.queryForFieldValuesArgs(mapOf("name" to FilterName))?.forEach {
        filter = it
    }

    filter.name = FilterName
    var rawFilter = context.headerMap()["TAG_filter"]
    var filterWeb = false
    val filterWebString = context.headerMap()["TAG_filterWeb"]
    if (filterWebString != null) {
        filterWeb = filterWebString.toBoolean()
    }
    filter.filterWeb = filterWeb
    val userDao = DatabaseUtilities.getUserDao()
    val username = context.headerMap()["LOGIN_username"]
        ?: // It won't be null, as it's checked in testAuthentication.
        return
    val user = userDao.queryForFieldValuesArgs(mapOf("username" to username)).firstOrNull()
    filter.user = user
    filter.filter = rawFilter;
    var filterAll: Boolean = false
    val filterAllString = context.headerMap()["TAG_filterAll"]
    if (filterAllString != null) {
        filterAll = filterAllString.toBoolean()
    }
    filter.filterAll = filterAll
    filterDao.createOrUpdate(filter)
    filterDao.refresh(filter)
    val filterChannelLinkDao = DatabaseUtilities.getFilterChannelLinkDao()
    var count = 1;
    while (context.headerMap()["TAG_channel$count"] != null) {
        val channelId = context.headerMap()["TAG_channel$count"]
        try {
            Integer.parseInt(channelId)
        } catch (e: Exception) {
            context.status(400)
            context.result("Invalid channel ID: $channelId")
            return
        }
        val channel = DatabaseUtilities.getChannelDao().queryForId(channelId?.toInt())
        var filterChannelLink: FilterChannelLink = FilterChannelLink()
        filterChannelLinkDao.queryForFieldValuesArgs(mapOf("channel_id" to channel.id, "filter_id" to filter.id)).forEach {
            filterChannelLink = it;
        }
        filterChannelLink.channel = channel
        filterChannelLink.filter = filter
        filterChannelLinkDao.create(filterChannelLink)
        count++
    }
    count = 1;
    while (context.headerMap()["TAG_removeChannel$count"] != null) {
        val channelId = context.headerMap()["TAG_removeChannel$count"]
        try {
            Integer.parseInt(channelId)
        } catch (e: Exception) {
            context.status(400)
            context.result("Invalid channel ID: $channelId")
            return
        }
        val channel = DatabaseUtilities.getChannelDao().queryForId(channelId?.toInt())
        var filterChannelLink: FilterChannelLink = FilterChannelLink()
        filterChannelLinkDao.queryForFieldValuesArgs(mapOf("channel_id" to channel.id, "filter_id" to filter.id)).forEach {
            filterChannelLink = it;
        }
        if (filterChannelLink.filter!=null) {
            filterChannelLinkDao.delete(filterChannelLink)
        }
        count++
    }
    context.status(200)
    context.result("Tag created or updated.")
}

fun runTagOnEntry(filter: Filter, entry: Entry): Boolean {
    val filterEntryLinkDao = DatabaseUtilities.getFilterEntryLinkDao()
    filterEntryLinkDao.queryForFieldValuesArgs(mapOf("entry_id" to entry.id, "filter_id" to filter.id)).forEach {
        return true
    }
    if (!filter.filterAll) {
        var go = false
        val filterChannelLinkDao = DatabaseUtilities.getFilterChannelLinkDao()
        filterChannelLinkDao.queryForFieldValuesArgs(mapOf("channel_id" to entry.channel.id, "filter_id" to filter.id)).forEach {
            go = true
        }
        if (!go) {
            return false
        }
    } else {
        var go = false
        filter.user?.userChannelLinks?.forEach {
            if (it.channel == entry.channel) {
                go = true
            }
        }
        if (!go) {
            return false
        }
    }
    var urlContent = ""
    if (filter.filterWeb) {
        val urlConnection = URI(entry.url).toURL().openConnection()
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0")
        urlConnection.connect()
        val scanner = Scanner(urlConnection.getInputStream())
        scanner.useDelimiter("\\Z")
        if (scanner.hasNext()) {
            urlContent = scanner.next()
        }
        scanner.close()
    }
    val fullEntryContent = entry.title + entry.description + urlContent + entry.url + entry.authors + entry.categories + entry.contributors + entry.comments + entry.enclosures + entry.contents + entry.foreignMarkup + entry.links + entry.publishedDate + entry.source + entry.modules
    if (!runFilter(filter, fullEntryContent)) {
        return false
    }
    val filterEntryLink = FilterEntryLink()
    filterEntryLink.filter = filter
    filterEntryLink.entry = entry
    filterEntryLinkDao.create(filterEntryLink)
    return true
}

fun runFilter(filter: Filter, input: String): Boolean {
    return input.lowercase().contains(filter.filter.lowercase())
}

fun getTags(context: Context) {
    val go = testAuthentication(context)
    if (!go) {
        return
    }
    val userDao = DatabaseUtilities.getUserDao()
    val username = context.headerMap()["LOGIN_username"]
        ?: // It won't be null, as it's checked in testAuthentication.
        return
    val user = userDao.queryForFieldValuesArgs(mapOf("username" to username)).firstOrNull()
    val filterDao = DatabaseUtilities.getFilterDao()
    val filters = filterDao.queryForFieldValuesArgs(mapOf("user_id" to user?.id))
    val filterChannelLinkDao = DatabaseUtilities.getFilterChannelLinkDao()
    val filtersJson = mutableListOf<Map<String, Any>>()
    filters.forEach { filter ->
        filtersJson.add(mapOf(
            "id" to filter.id,
            "name" to filter.name,
            "filter" to filter.filter,
            "filterAll" to filter.filterAll
        ))
    }
    context.json(filtersJson)
}

fun getTaggedEntriesForUser(context: Context) {
    val go = testAuthentication(context)
    if (!go) {
        return
    }
    val userDao = DatabaseUtilities.getUserDao()
    val username = context.headerMap()["LOGIN_username"]
        ?: // It won't be null, as it's checked in testAuthentication.
        return
    val user = userDao.queryForFieldValuesArgs(mapOf("username" to username)).firstOrNull()
    val filterDao = DatabaseUtilities.getFilterDao()
    val filters = user?.filters;
    val filterEntryLinkDao = DatabaseUtilities.getFilterEntryLinkDao()
    val entriesJson = mutableListOf<Map<String, Any>>()
    filters?.forEach { filter ->
        val filterEntryLinks = filterEntryLinkDao.queryForFieldValuesArgs(mapOf("filter_id" to filter.id))
        filterEntryLinks.forEach {
            val entry = it.entry
            entriesJson.add(mapOf(
                "entryid" to entry.id,
                "filterid" to filter.id,
            ))
        }
    }
    context.json(entriesJson)
}

// TODO: Autodelete channels and cull entries.
// TODO: Delete tags and filter entries.
// TODO: "Rerun tag" endpoint
// TODO: Only run tags if entries are new
// TODO: Option to run tags on all applicable content when created