package com.example.yacinehc.mplrss.model;

import android.os.Parcel;
import android.os.Parcelable;

public class FilRSS implements Parcelable {
    private long id;
    private String link;
    private String titre;
    private String description;

    public FilRSS() {

    }
    public FilRSS(long id, String link, String titre, String description) {
        this.id = id;
        this.link = link;
        this.titre = titre;
        this.description = description;
    }

    public FilRSS(Parcel parcel) {
        this.id = parcel.readLong();
        this.link = parcel.readString();
        this.titre = parcel.readString();
        this.description = parcel.readString();
    }

    public static final Creator<FilRSS> CREATOR = new Creator<FilRSS>() {
        @Override
        public FilRSS createFromParcel(Parcel in) {
            return new FilRSS(in);
        }

        @Override
        public FilRSS[] newArray(int size) {
            return new FilRSS[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(link);
        dest.writeString(titre);
        dest.writeString(description);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
}
