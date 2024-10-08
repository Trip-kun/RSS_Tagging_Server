package org.example

import io.javalin.http.Context
import org.example.data.Channel
import org.example.data.Entry
import org.example.data.User
import org.example.data.UserChannelLink
fun createChannel(context: Context) {
    val go = testAuthentication(context)
    if (!go) {
        return
    }
    val url = context.headerMap()["CHANNEL_url"]
    if (url == null) {
        context.status(400)
        context.result("No URL provided.")
        return
    }
    if (!validateUrl(url.toString())) {
        context.status(400)
        context.result("Invalid URL.")
        return
    }
    var userChannelLink: UserChannelLink?
    val user = userDao.queryForFieldValuesArgs(mapOf("username" to context.headerMap()["LOGIN_username"]))?.firstOrNull()
    val status = fetchChannel(url.toString(), user)
    if (status.first == "success") {
        context.status(200)
        context.result("Channel added.")
        context.header("CHANNEL_id", status.second?.id.toString())
        userChannelLink = userChannelLinkDao.queryForFieldValuesArgs(mapOf("user_id" to user?.id, "channel_id" to status.second?.id.toString()))
            ?.firstOrNull()
        if (userChannelLink==null) {
            userChannelLink = UserChannelLink()
        }
        if (userChannelLink.user == null) {
            userChannelLink.user = user
            userChannelLink.channel = status.second
        }
        userChannelLinkDao.createOrUpdate(userChannelLink)
    } else {
        context.status(400)
        context.result("Error adding channel. Check the URL.")
    }
}
fun getEntries(context: Context) {
    val go = testAuthentication(context)
    if (!go) {
        return
    }
    val username = context.headerMap()["LOGIN_username"]
        ?: // It won't be null, as it's checked in testAuthentication.
        return
    val user = userDao.queryForFieldValuesArgs(mapOf("username" to username))?.firstOrNull()
    var go2 = false
    val channelId = context.headerMap()["CHANNEL_id"]
    if (channelId == null) {
        context.status(400)
        context.result("No channel ID provided.")
        return
    }
    try {
        Integer.parseInt(channelId)
    } catch (e: Exception) {
        context.status(400)
        context.result("Invalid channel ID.")
        return
    }
    user?.userChannelLinks?.forEach {
        println('a')
        val channel = it.channel
        if (channel != null) {
            if (channel.id == channelId.toInt()) {
                go2 = true
            }
        }
    }
    if (!go2) {
        context.status(400)
        context.result("You haven't subscribed to this channel.")
        return
    }
   channelDao.queryForId(channelId.toInt())?.let {
        val entries = it.entries
        context.status(200)
       if (entries != null) {
           context.result(gson.toJson(entries))
       }
    } ?: run {
        context.status(400)
        context.result("Channel not found.")
    }
}
fun unsubscribe(context: Context) {
    val go = testAuthentication(context)
    if (!go) {
        return
    }
    val username = context.headerMap()["LOGIN_username"]
        ?: // It won't be null, as it's checked in testAuthentication.
        return
    val user = userDao.queryForFieldValuesArgs(mapOf("username" to username))?.firstOrNull()
    var go2 = false
    val channelId = context.headerMap()["CHANNEL_id"]
    if (channelId == null) {
        context.status(400)
        context.result("No channel ID provided.")
        return
    }
    try {
        Integer.parseInt(channelId)
    } catch (e: Exception) {
        context.status(400)
        context.result("Invalid channel ID.")
        return
    }
    cleanChannel(channelDao.queryForId(channelId.toInt())!!, user)
    user?.userChannelLinks?.forEach {
        val channel = it.channel
        if (channel != null) {
            if (channel.id == channelId.toInt()) {
                userChannelLinkDao.delete(it)
            }
        }
    }
    context.status(200)
    context.result("Unsubscribed.")
}

fun getChannels(context: Context) {
    val go = testAuthentication(context)
    if (!go) {
        return
    }
    val username = context.headerMap()["LOGIN_username"]
        ?: // It won't be null, as it's checked in testAuthentication.
        return
    val user = userDao.queryForFieldValuesArgs(mapOf("username" to username))?.firstOrNull()
    val userChannelLinks = user?.userChannelLinks
    val channels = mutableListOf<Channel>()
    userChannelLinks?.forEach {
        val channel = it.channel
        if (channel != null) {
            channels.add(channel)
        }
    }
    context.status(200)
    context.json(channels)
}

fun cleanChannel(channel: Channel, user: User?) {
    user?.userChannelLinks?.forEach{
        if (it.channel==channel) {
            userChannelLinkDao.delete(it)
        }
    }
    user?.filters?.forEach{
        if (it.filterChannelLinks!=null) {
            it.filterChannelLinks?.forEach{
                if (it.channel==channel) {
                    filterChannelLinkDao.delete(it)
                }
            }
        }
        it.filterEntryLinks?.forEach{
            if (it.entry?.channel==channel) {
                filterEntryLinkDao.delete(it)
            }
        }
    }
    var count = 0
    channel.userChannelLinks?.forEach{
        count++
    }
    if (count==0) {
        channel.entries?.forEach{
            cleanEntry(it)
        }
        channel.filterChannelLinks?.forEach{
            filterChannelLinkDao.delete(it)
        }
        channelDao.delete(channel)
    }
}

fun cleanEntry(entry: Entry) {
    entry.filterEntryLinks?.forEach{
        filterEntryLinkDao.delete(it)
    }
    entryDao.delete(entry)
}