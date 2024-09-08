package org.example.data;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "entries")
public class Entry {
    @DatabaseField(generatedId = true)
    public int id;
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    public transient Channel channel;
    @DatabaseField(canBeNull = true)
    public String title;
    @DatabaseField(canBeNull = true, dataType = DataType.LONG_STRING)
    public String description;
    @DatabaseField(canBeNull = true)
    public String url;
    @DatabaseField(canBeNull = true)
    public String authors;
    @DatabaseField(canBeNull = true)
    public String contributors;
    @DatabaseField(canBeNull = true)
    public String categories;
    @DatabaseField(canBeNull = true)
    public String comments;
    @DatabaseField(canBeNull = true)
    public String enclosures;
    @DatabaseField(canBeNull = true)
    public String contents;
    @DatabaseField(canBeNull = true)
    public String foreignMarkup;
    @DatabaseField(canBeNull = true)
    public String links;
    @DatabaseField(canBeNull = true)
    public Date publishedDate;
    @DatabaseField(canBeNull = true)
    public String source;
    @DatabaseField(canBeNull = true, dataType = DataType.LONG_STRING)
    public String modules;
    @ForeignCollectionField(eager = true)
    public transient ForeignCollection<FilterEntryLink> filterEntryLinks;
}
