package com.rahbarbazaar.hamyab.models.dashboard;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ProjectList implements Parcelable {

    @SerializedName("time")
    @Expose
    public Integer time;
    @SerializedName("project")
    @Expose
    public List<Project> project = null;  //or this code line is OK  instead of upper line

    public ProjectList() {

    }

    public ProjectList(Parcel in) {
        if (in.readByte() == 0) {
            time = null;
        } else {
            time = in.readInt();
        }

        project = in.readArrayList(Project.class.getClassLoader());  // to send arraylist
    }



    public static final Creator<ProjectList> CREATOR = new Creator<ProjectList>() {
        @Override
        public ProjectList createFromParcel(Parcel in) {
            return new ProjectList(in);
        }

        @Override
        public ProjectList[] newArray(int size) {
            return new ProjectList[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (time == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(time);
        }

        parcel.writeList(project);  // to pass arraylist
    }
}
