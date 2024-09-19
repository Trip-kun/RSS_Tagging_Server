package org.example.data

import com.j256.ormlite.dao.ForeignCollection
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.field.ForeignCollectionField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "filters")
class Filter {
    @DatabaseField(generatedId = true)
    var id: Int = 0
    @Transient
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    var user: User? = null
    @DatabaseField(canBeNull = false)
    var name: String = ""
    @DatabaseField(canBeNull = false)
    var filter: String = ""
    @DatabaseField(canBeNull = false)
    var filterAll: Boolean = false
    @DatabaseField(canBeNull = false)
    var filterWeb: Boolean = false
    @DatabaseField(canBeNull = false)
    var useRegex: Boolean = false
    @ForeignCollectionField(eager = true)
    @Transient
    var filterChannelLinks: ForeignCollection<FilterChannelLink>? = null
    @ForeignCollectionField(eager = true)
    @Transient
    var filterEntryLinks: ForeignCollection<FilterEntryLink>? = null
}
@DatabaseTable(tableName = "filter_channel_links")
class FilterChannelLink {
    @DatabaseField(generatedId = true)
    var id: Int = 0
    @Transient
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    var filter: Filter? = null
    @Transient
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    var channel: Channel? = null
}
@DatabaseTable(tableName = "filter_entry_links")
class FilterEntryLink {
    @DatabaseField(generatedId = true)
    var id: Int = 0
    @Transient
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    var filter: Filter? = null
    @Transient
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    var entry: Entry? = null
}