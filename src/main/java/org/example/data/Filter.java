package org.example.data;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "filters")
public class Filter {
    @DatabaseField(generatedId = true)
    public int id;
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    public transient User user;
    @DatabaseField(canBeNull = false)
    public String name;
    @DatabaseField(canBeNull = true)
    public String filter;
    @DatabaseField(canBeNull = false)
    public Boolean filterAll = false;
    @DatabaseField(canBeNull = false)
    public Boolean filterWeb = false;
    @ForeignCollectionField(eager = true)
    public transient ForeignCollection<FilterEntryLink> filterEntryLinks;
    @ForeignCollectionField(eager = true)
    public transient ForeignCollection<FilterChannelLink> filterChannelLinks;
}
