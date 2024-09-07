package org.example.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "authentications")
public class Authentication {
    @DatabaseField(generatedId = true)
    public int id;
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    public User user;
    @DatabaseField(canBeNull = false)
    public String salt;
    @DatabaseField(canBeNull = false)
    public String hashedToken;
    @DatabaseField(canBeNull = false)
    public long expiryUnix;
}
