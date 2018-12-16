package com.example.yacinehc.mplrss.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.time.LocalDateTime;
import java.util.Objects;

public class RSS implements Parcelable {
    public static final Creator<RSS> CREATOR = new Creator<RSS>() {
        @Override
        public RSS createFromParcel(Parcel in) {
            return new RSS(in);
        }

        @Override
        public RSS[] newArray(int size) {
            return new RSS[size];
        }
    };
    private String link;
    private String titre;
    private String description;
    private String path;
    private LocalDateTime time;

    public RSS(String link, String titre, String description, String path, LocalDateTime time) {
        this.link = link;
        this.titre = titre;
        this.description = description;
        this.path = path;
        this.time = time;
    }

    public RSS(Parcel parcel) {
        this.link = parcel.readString();
        this.titre = parcel.readString();
        this.description = parcel.readString();
        this.path = parcel.readString();
        this.time = (LocalDateTime) parcel.readSerializable();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(link);
        dest.writeString(titre);
        dest.writeString(description);
        dest.writeString(path);
        dest.writeSerializable(time);
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "RSS{" +
                "link='" + link + '\'' +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", path='" + path + '\'' +
                ", time=" + time +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RSS rss = (RSS) o;
        return Objects.equals(link, rss.link) &&
                Objects.equals(titre, rss.titre) &&
                Objects.equals(description, rss.description);
    }

    @Override
    public int hashCode() {

        return Objects.hash(link, titre, description);
    }
}
