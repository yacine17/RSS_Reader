package com.example.yacinehc.mplrss.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.yacinehc.mplrss.model.RSS;
import com.example.yacinehc.mplrss.model.RssItem;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AccesDonnees {
    public final static String DB_NAME = "base_rss";
    public final static String RSS_TABLE = "rss";
    public final static String LINK_COLUMN = "link";
    public final static String TITLE_COLUMN = "title";
    public final static String DESCRIPTION_COLUMN = "description";
    public final static String PATH_COLUMN = "path";
    public final static String TIME_COLUMN = "time";

    public final static String ITEMS_TABLE = "items";
    public final static String PUB_DATE_COLUMN = "pubDate";
    public final static String LINK_FOREIGN_KEY_COLUMN = "link_foreign";

    public final static String authority = "fr.diderot.yacinehc.mplrssserver";
    private ContentResolver contentResolver;

    public AccesDonnees(Context context) {
        contentResolver = context.getContentResolver();
    }


    public void addRSSFeed(RSS rss) {
        addRSSFeed(rss.getLink(), rss.getTitre(), rss.getDescription(), rss.getPath(), rss.getTime());
    }

    public void addRSSFeed(String link, String title, String description, String path,
                           LocalDateTime time) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(LINK_COLUMN, link);
        contentValues.put(TITLE_COLUMN, title);
        contentValues.put(DESCRIPTION_COLUMN, description);
        contentValues.put(PATH_COLUMN, path);
        contentValues.put(TIME_COLUMN, time.toString());

        Uri.Builder builder = new Uri.Builder();

        builder.scheme("content").authority(authority).appendPath(RSS_TABLE);
        Uri uri = builder.build();
        contentResolver.insert(uri, contentValues);
    }

    public void addRssItems(List<RssItem> rssItems, RSS rss) {

        for (RssItem rssItem : rssItems) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(LINK_COLUMN, rssItem.getLink());
            contentValues.put(TITLE_COLUMN, rssItem.getTitle());
            contentValues.put(DESCRIPTION_COLUMN, rssItem.getDescription());
            contentValues.put(PUB_DATE_COLUMN, rssItem.getPubDate().toString());
            contentValues.put(LINK_FOREIGN_KEY_COLUMN, rss.getLink());

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("content").authority(authority).appendPath(ITEMS_TABLE);
            Uri uri = builder.build();
            contentResolver.insert(uri, contentValues);
        }
    }

    public List<RssItem> getRssItems(RSS rss) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath(ITEMS_TABLE);
        Uri uri = builder.build();
        Cursor cursor = contentResolver.query(uri, null, LINK_FOREIGN_KEY_COLUMN + " = ?",
                new String[]{rss.getLink()}, null);

        List<RssItem> list = new ArrayList<>();

        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            LocalDateTime time = LocalDateTime.parse(cursor.getString(cursor.getColumnIndex(PUB_DATE_COLUMN)));
            long days = ChronoUnit.DAYS.between(time, LocalDateTime.now());
            System.out.println("days = " + days);

            if (days > 3) {
                String link = cursor.getString(cursor.getColumnIndex(LINK_COLUMN));
                contentResolver.delete(uri, LINK_COLUMN + " = ?", new String[]{link});
            } else {
                RssItem rssItem = new RssItem();
                rssItem.setLink(cursor.getString(cursor.getColumnIndex(LINK_COLUMN)));
                rssItem.setDescription(cursor.getString(cursor.getColumnIndex(DESCRIPTION_COLUMN)));
                rssItem.setTitle(cursor.getString(cursor.getColumnIndex(TITLE_COLUMN)));
                rssItem.setPubDate(LocalDateTime.parse(cursor.getString(cursor.getColumnIndex(PUB_DATE_COLUMN))));

                list.add(rssItem);
            }
        }

        list.sort(new Comparator<RssItem>() {
            @Override
            public int compare(RssItem o1, RssItem o2) {
                return o1.getPubDate().compareTo(o2.getPubDate());
            }
        });

        return list;
    }

    public void removeItems(List<RSS> items) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath(RSS_TABLE);
        Uri uri = builder.build();

        for (RSS rss : items) {
            System.out.println("contentResolver.delete(uri, \"title in (?)\", new String[{args}]) = "
                    + contentResolver.delete(uri, "link  = ?", new String[]{rss.getLink()}));
        }
    }


}
