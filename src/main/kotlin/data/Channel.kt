package org.example.data

import com.j256.ormlite.dao.ForeignCollection
import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.field.ForeignCollectionField
import com.j256.ormlite.table.DatabaseTable
import java.util.Date

@DatabaseTable(tableName = "channels")
class Channel {
    @DatabaseField(generatedId = true)
    var id: Int = 0
    @DatabaseField(canBeNull = false)
    var url: String = ""
    @DatabaseField(canBeNull = true)
    var title: String? = null
    @DatabaseField(canBeNull = true)
    var description: String? = null
    @DatabaseField(canBeNull = true)
    var docs: String? = null
    @DatabaseField(canBeNull = true)
    var language: String? = null
    @DatabaseField(canBeNull = true)
    var authors: String? = null
    @DatabaseField(canBeNull = true)
    var categories: String? = null
    @DatabaseField(canBeNull = true)
    var generator: String? = null
    @DatabaseField(canBeNull = true)
    var contributors: String? = null
    @DatabaseField(canBeNull = true)
    var copyright: String? = null
    @DatabaseField(canBeNull = true)
    var imageTitle: String? = null
    @DatabaseField(canBeNull = true)
    var imageUrl: String? = null
    @DatabaseField(canBeNull = true)
    var imageLink: String? = null
    @DatabaseField(canBeNull = true)
    var imageDescription: String? = null
    @DatabaseField(canBeNull = true)
    var foreignMarkup: String? = null
    @DatabaseField(canBeNull = true)
    var publishedDate: Date? = null
    @DatabaseField(canBeNull = true)
    var managingEditor: String? = null
    @DatabaseField(canBeNull = false)
    var lastUpdated: Long = 0
    @DatabaseField(canBeNull = true, dataType= DataType.LONG_STRING)
    var modules: String? = null
    @ForeignCollectionField(eager = true)
    @Transient
    var entries: ForeignCollection<Entry>? = null
    @ForeignCollectionField(eager = true)
    @Transient
    var userChannelLinks: ForeignCollection<UserChannelLink>? = null
    @ForeignCollectionField(eager = true)
    @Transient
    var filterChannelLinks: ForeignCollection<FilterChannelLink>? = null
}

@DatabaseTable(tableName = "user_channel_links")
class UserChannelLink {
    @DatabaseField(generatedId = true)
    var id: Int = 0
    @Transient
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    var user: User? = null
    @Transient
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    var channel: Channel? = null
}