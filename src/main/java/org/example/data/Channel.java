package org.example.data;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "channels")
public class Channel {
    @DatabaseField(generatedId = true)
    public int id;
    @DatabaseField(canBeNull = true)
    public String description;
    @DatabaseField(canBeNull = false)
    public String url;
    @DatabaseField(canBeNull = true)
    public String title;
    @DatabaseField(canBeNull = true)
    public String docs;
    @DatabaseField(canBeNull = true)
    public String language;
    @DatabaseField(canBeNull = true)
    public String categories;
    @DatabaseField(canBeNull = true)
    public String authors;
    @DatabaseField(canBeNull = true)
    public String contributors;
    @DatabaseField(canBeNull = true)
    public String copyright;
    @DatabaseField(canBeNull = true)
    public String generator;
    @DatabaseField(canBeNull = true)
    public String imageTitle;
    @DatabaseField(canBeNull = true)
    public String imageUrl;
    @DatabaseField(canBeNull = true)
    public String imageLink;
    @DatabaseField(canBeNull = true)
    public String imageDescription;
    @DatabaseField(canBeNull = true)
    public String foreignMarkup;
    @DatabaseField(canBeNull = true)
    public Date publishedDate;
    @DatabaseField(canBeNull = true)
    public String managingEditor;
    @DatabaseField(canBeNull = true)
    public Long lastBuildDate;
    @DatabaseField(canBeNull = true, dataType = DataType.LONG_STRING)
    public String modules;
    @ForeignCollectionField(eager = true)
    public transient ForeignCollection<Entry> entries;
    @ForeignCollectionField(eager = true)
    public transient ForeignCollection<UserChannelLink> userChannelLinks;
    @ForeignCollectionField(eager = true)
    public transient ForeignCollection<FilterChannelLink> filterChannelLinks;
}
