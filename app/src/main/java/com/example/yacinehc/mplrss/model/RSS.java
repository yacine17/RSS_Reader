package com.example.yacinehc.mplrss.model;

import android.os.Parcel;
import android.os.Parcelable;

public class RSS implements Parcelable {
    private String link;
    private String titre;
    private String description;

    public RSS() {

    }

    public RSS(String link, String titre, String description) {
        this.link = link;
        this.titre = titre;
        this.description = description;
    }

    public RSS(Parcel parcel) {
        this.link = parcel.readString();
        this.titre = parcel.readString();
        this.description = parcel.readString();
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(link);
        dest.writeString(titre);
        dest.writeString(description);
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
