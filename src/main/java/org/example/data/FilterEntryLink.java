package org.example.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "filter_entry_links")
public class FilterEntryLink {
    @DatabaseField(generatedId = true)
    public int id;
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    public transient Filter filter;
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    public transient Entry entry;
}
