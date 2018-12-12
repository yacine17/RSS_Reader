package com.example.yacinehc.mplrss.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.yacinehc.mplrss.CheckedLinearLayout;
import com.example.yacinehc.mplrss.model.RSS;

import java.util.ArrayList;
import java.util.List;

public class AccesDonnees {
    public final static String DB_NAME = "base_rss";
    public final static String RSS_TABLE = "rss";
    public final static String LINK_COLUMN = "link";
    public final static String TITLE_COLUMN = "title";
    public final static String DESCRIPTION_COLUMN = "description";
    public final static String authority = "fr.diderot.yacinehc.mplrssserver";
    private ContentResolver contentResolver;

    public AccesDonnees(Context context) {
        contentResolver = context.getContentResolver();
    }


    public void addRSSFeed(RSS rss) {
        addRSSFeed(rss.getLink(), rss.getTitre(), rss.getDescription());
    }


    public void addRSSFeed(String link, String title, String description) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(LINK_COLUMN, link);
        contentValues.put(TITLE_COLUMN, title);
        contentValues.put(DESCRIPTION_COLUMN, description);
        Uri.Builder builder = new Uri.Builder();

        builder.scheme("content").authority(authority).appendPath(RSS_TABLE);
        Uri uri = builder.build();
        uri = contentResolver.insert(uri, contentValues);
    }


    public ArrayList<RSS> getRSSFeed() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath(RSS_TABLE);
        Uri uri = builder.build();
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        ArrayList<RSS> list = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                RSS rss = new RSS(cursor.getString(cursor.getColumnIndex(AccesDonnees.LINK_COLUMN)),
                        cursor.getString(cursor.getColumnIndex(AccesDonnees.TITLE_COLUMN)),
                        cursor.getString(cursor.getColumnIndex(AccesDonnees.DESCRIPTION_COLUMN)));
                list.add(rss);
            }
            cursor.close();
        } catch (NullPointerException e) {
            e.getMessage();
        }

        return list;
    }

    public void removeItems(List<CheckedLinearLayout> items) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath(RSS_TABLE);
        Uri uri = builder.build();

        for (CheckedLinearLayout rss : items) {
            System.out.println("contentResolver.delete(uri, \"title in (?)\", new String[{args}]) = "
                    + contentResolver.delete(uri, "link  = ?", new String[]{rss.getRss().getLink()}));
        }
    }

}
