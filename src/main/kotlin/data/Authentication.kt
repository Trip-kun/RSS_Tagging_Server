package org.example.data

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "authentications")
class Authentication {
    @DatabaseField(generatedId = true)
    var id: Int = 0
    @Transient
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    var user: User? = null
    @DatabaseField(canBeNull = false)
    var tokenSalt: String = ""
    @DatabaseField(canBeNull = false)
    var hashedToken: String = ""
    @DatabaseField(canBeNull = false)
    var expiryUnix: Long = 0
}