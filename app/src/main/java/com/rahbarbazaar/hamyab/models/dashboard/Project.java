package com.rahbarbazaar.hamyab.models.dashboard;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Project implements Parcelable {

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("title")
    @Expose
    public String title;


    protected Project(Parcel in) {
        id = in.readString();
        title = in.readString();
    }

    public static final Creator<Project> CREATOR = new Creator<Project>() {
        @Override
        public Project createFromParcel(Parcel in) {
            return new Project(in);
        }

        @Override
        public Project[] newArray(int size) {
            return new Project[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
    }
}
