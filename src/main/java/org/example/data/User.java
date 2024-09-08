package org.example.data;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import org.checkerframework.checker.units.qual.A;

import java.util.Collection;

@DatabaseTable(tableName = "users")
public class User {
    @DatabaseField(generatedId = true)
    public int id;
    @DatabaseField(canBeNull = false)
    public String username;
    @DatabaseField(canBeNull = false)
    public String salt;
    @DatabaseField(canBeNull = false)
    public String hashedPassword;
    @DatabaseField(canBeNull = false)
    public String email;
    @DatabaseField(canBeNull = false)
    public Boolean emailVerified=false;
    @DatabaseField(canBeNull = true)
    public String emailVerificationTokenHash;
    @DatabaseField(canBeNull = true)
    public String emailVerificationSalt;
    @ForeignCollectionField(eager = true)
    public transient ForeignCollection<Authentication> authentications;
    @ForeignCollectionField(eager = true)
    public transient ForeignCollection<UserChannelLink> userChannelLinks;
    @ForeignCollectionField(eager = true)
    public transient ForeignCollection<Filter> filters;
}
