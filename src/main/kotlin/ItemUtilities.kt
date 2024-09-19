package org.example

import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.io.SyndFeedInput
import com.rometools.rome.io.XmlReader
import org.example.data.Channel
import org.example.data.Entry
import org.example.data.User
import java.net.URI
import java.time.Instant
import java.util.Scanner

fun fetchURIConnection(url : String): String {
    val urlContent: String
    try {
        val urlConnection = URI(url).toURL().openConnection()
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0")
        urlConnection.connect()
        val scanner = Scanner(urlConnection.getInputStream())
        scanner.useDelimiter("\\A")
        urlContent = if (scanner.hasNext()) {
            scanner.next()
        } else {
            ""
        }
        scanner.close()
        return urlContent
    } catch (e: Exception) {
        println("Error fetching URI: $e")
        return ""
    }
}
fun fetchChannel(url : String, user: User?): Pair<String, Channel?> {
    var channel: Channel?
    try {
        val urlConnection = URI(url).toURL().openConnection()
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0")
        urlConnection.connect()
        val a = SyndFeedInput().build(XmlReader(urlConnection.getInputStream()))
        val uri = a.uri
        channel = channelDao.queryForFieldValuesArgs(mapOf("url" to uri))?.firstOrNull()
        if ((channel) == null) {
            channel = Channel()
        }
        channel.url = uri
        channel.title = a.title
        channel.description = a.description
        if (a.docs !=null) {
            channel.docs = fetchURIConnection(a.docs)
        }
        channel.language = a.language
        channel.authors = ""
        a.authors?.forEach { channel.authors += it.name + ", " }
        channel.categories = ""
        a.categories?.forEach { channel.categories += it.name + ", " }
        channel.contributors = ""
        a.contributors?.forEach { channel.contributors += it.name + ", " }
        channel.copyright = a.copyright
        channel.generator = a.generator
        channel.imageUrl = a.image?.url
        channel.imageLink = a.image?.link
        channel.imageTitle = a.image?.title
        channel.imageDescription = a.image?.description
        channel.foreignMarkup = ""
        a.foreignMarkup.forEach { channel.foreignMarkup += it.name + ", " }
        channel.publishedDate = a.publishedDate
        channel.managingEditor = a.managingEditor
        channel.lastUpdated= Instant.now().epochSecond
        channel.modules = ""
        a.modules.forEach { channel.modules += "$it, " }
        channelDao.createOrUpdate(channel)
        handleEntries(a, channel, user)
    } catch (e: Exception) {
        println("Error fetching URI: " + e.toString() + " " + e.printStackTrace().toString())
        return Pair("Error fetching URI: $e", null)
    }
    return Pair("success", channel)
}

fun handleEntries(feed: SyndFeed, channel: Channel, user: User?) {
    feed.entries.forEach { syndEntry ->
        var entry: Entry
        entry = Entry()
        entryDao.queryForFieldValuesArgs(mapOf("url" to syndEntry.uri))?.forEach {
            if (channel == it.channel) {
                entry = it
                // Don't do filtering
            }
        }
        entryDao.queryForFieldValuesArgs(mapOf("title" to syndEntry.title))?.forEach {
            if (it.channel==channel) {
                entry = it
                // Don't do filtering
            }
        }
        entry.channel = channel
        entry.title = syndEntry.title
        entry.description = syndEntry.description.value
        entry.url = syndEntry.uri
        entry.authors = ""
        syndEntry.authors?.forEach { entry.authors += it.name + ", " }
        entry.categories = ""
        syndEntry.categories?.forEach { entry.categories += it.name + ", " }
        entry.comments = syndEntry.comments
        entry.contents = ""
        syndEntry.contents?.forEach { entry.contents += it.value + ", " }
        entry.contributors = ""
        syndEntry.contributors?.forEach { entry.contributors += it.name + ", " }
        entry.enclosures = ""
        syndEntry.enclosures?.forEach { entry.enclosures += it.url + ", " + it.length + ", " + it.type + ", "}
        entry.foreignMarkup = ""
        syndEntry.foreignMarkup.forEach { entry.foreignMarkup += it.name + ", " }
        entry.links = ""
        syndEntry.links.forEach { entry.links += it.href + ", " }
        entry.publishedDate = syndEntry.publishedDate
        entry.source = ""
        syndEntry.source?.let { entry.source = it.title }
        entry.modules = ""
        syndEntry.modules.forEach { entry.modules += "$it, " }
        entryDao.createOrUpdate(entry)
        entryDao.refresh(entry)
        channel.filterChannelLinks?.forEach { filterChannelLink ->
            val filter = filterChannelLink.filter
            if (filter != null) {
                runTagOnEntry(filter, entry)
            }
        }
        user?.filters?.forEach {
            if (it.filterAll) {
                runTagOnEntry(it, entry)
            }
        }
    }
}

fun validateUrl(url: String): Boolean {
    return try {
        URI(url)
        true
    } catch (e: Exception) {
        false
    }
}