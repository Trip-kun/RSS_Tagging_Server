package org.example.data

import com.j256.ormlite.dao.ForeignCollection
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.field.ForeignCollectionField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "users")
class User {
    @DatabaseField(generatedId = true)
    var id: Int = 0
    @DatabaseField(canBeNull = false)
    var username: String = ""
    @DatabaseField(canBeNull = false)
    var passwordSalt: String = ""
    @DatabaseField(canBeNull = false)
    var hashedPassword: String = ""
    @DatabaseField(canBeNull = false)
    var email: String = ""
    @DatabaseField(canBeNull = true)
    var emailTokenSalt: String? = null
    @DatabaseField(canBeNull = true)
    var hashedEmailToken: String? = null
    @DatabaseField(canBeNull = false)
    var emailVerified: Boolean = false
    @DatabaseField(canBeNull = false)
    var emailTokenExpiryUnix: Long = 0
    @ForeignCollectionField(eager = true)
    @Transient
    var authentications: ForeignCollection<Authentication>? = null
    @ForeignCollectionField(eager = true)
    @Transient
    var userChannelLinks: ForeignCollection<UserChannelLink>? = null
    @ForeignCollectionField(eager = true)
    @Transient
    var filters: ForeignCollection<Filter>? = null
}