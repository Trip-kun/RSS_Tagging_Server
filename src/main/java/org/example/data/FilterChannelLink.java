package org.example.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "filter_channel_links")
public class FilterChannelLink {
    @DatabaseField(generatedId = true)
    public int id;
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    public Filter filter;
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    public Channel channel;
}
