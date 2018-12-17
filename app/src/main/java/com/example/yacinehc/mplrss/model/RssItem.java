package com.example.yacinehc.mplrss.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.time.LocalDateTime;

public class RssItem implements Parcelable {
    public static final Creator<RssItem> CREATOR = new Creator<RssItem>() {
        @Override
        public RssItem createFromParcel(Parcel in) {
            return new RssItem(in);
        }

        @Override
        public RssItem[] newArray(int size) {
            return new RssItem[size];
        }
    };
    private String link;
    private String title;
    private String description;
    private LocalDateTime pubDate;

    public RssItem(Parcel in) {
        this.link = in.readString();
        this.description = in.readString();
        this.title = in.readString();
        this.pubDate = (LocalDateTime) in.readSerializable();
    }

    public RssItem() {

    }

    public RssItem(String link, String title, String description, LocalDateTime pubDate) {
        this.link = link;
        this.title = title;
        this.description = description;
        this.pubDate = pubDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(link);
        dest.writeString(description);
        dest.writeString(title);
        dest.writeSerializable(pubDate);
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return "RssItem{" +
                "link='" + link + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", pubDate='" + pubDate + '\'' +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getPubDate() {
        return pubDate;
    }

    public void setPubDate(LocalDateTime pubDate) {
        this.pubDate = pubDate;
    }
}
