package org.example.data

import com.j256.ormlite.dao.ForeignCollection
import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.field.ForeignCollectionField
import com.j256.ormlite.table.DatabaseTable
import java.util.Date

@DatabaseTable(tableName = "entries")
class Entry {
    @DatabaseField(generatedId = true)
    var id: Int = 0
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    @Transient var channel: Channel? = null
    @DatabaseField(canBeNull = true)
    var title: String? = null
    @DatabaseField(canBeNull = true, dataType= DataType.LONG_STRING)
    var description: String? = null
    @DatabaseField(canBeNull = true)
    var url: String? = null
    @DatabaseField(canBeNull = true)
    var authors: String? = null
    @DatabaseField(canBeNull = true)
    var categories: String? = null
    @DatabaseField(canBeNull = true)
    var comments: String? = null
    @DatabaseField(canBeNull = true)
    var contributors: String? = null
    @DatabaseField(canBeNull = true)
    var enclosures: String? = null
    @DatabaseField(canBeNull = true)
    var contents: String? = null
    @DatabaseField(canBeNull = true)
    var foreignMarkup: String? = null
    @DatabaseField(canBeNull = true)
    var links: String? = null
    @DatabaseField(canBeNull = true)
    var publishedDate: Date? = null
    @DatabaseField(canBeNull = true)
    var source: String? = null
    @DatabaseField(canBeNull = true, dataType = DataType.LONG_STRING)
    var modules: String? = null
    @ForeignCollectionField(eager = true)
    @Transient
    var filterEntryLinks: ForeignCollection<FilterEntryLink>? = null
}