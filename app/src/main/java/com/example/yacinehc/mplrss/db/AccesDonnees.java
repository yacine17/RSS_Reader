package com.example.yacinehc.mplrss.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class AccesDonnees {
    private ContentResolver contentResolver;

    public final static String DB_NAME = "base_rss";
    public final static String RSS_TABLE = "rss";
    public final static String LINK_COLUMN = "link";
    public final static String TITLE_COLUMN = "title";
    public final static String DESCRIPTION_COLUMN = "description";
    private final static String authority = "fr.diderot.yacinehc.mplrss";

    public AccesDonnees(Context context) {
        contentResolver = context.getContentResolver();
    }


    public void addRSSFeed(String link, String title, String description) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(LINK_COLUMN, link);
        contentValues.put(TITLE_COLUMN, link);
        contentValues.put(DESCRIPTION_COLUMN, description);
        Uri.Builder builder = new Uri.Builder();

        builder.scheme("content").authority(authority).appendPath(RSS_TABLE);
        Uri uri = builder.build();
        uri = contentResolver.insert(uri, contentValues);
    }


    public Cursor getRSSFeed() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath(RSS_TABLE);
        Uri uri = builder.build();
        return contentResolver.query(uri, null, null, null, null);
    }

}
