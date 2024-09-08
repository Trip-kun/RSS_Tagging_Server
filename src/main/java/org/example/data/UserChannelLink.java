package org.example.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "user_channel_links")
public class UserChannelLink {
    @DatabaseField(generatedId = true)
    public int id;
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    public transient User user;
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    public transient Channel channel;
}
