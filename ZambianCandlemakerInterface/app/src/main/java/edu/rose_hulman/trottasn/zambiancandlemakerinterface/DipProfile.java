package edu.rose_hulman.trottasn.zambiancandlemakerinterface;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by TrottaSN on 2/4/2016.
 */
public class DipProfile implements Parcelable{
    private ArrayList<TimePosPair> pairList;
    private String title;
    private String description;

    public DipProfile(){
        this.pairList = new ArrayList<TimePosPair>();
    }

    public DipProfile(String title, String description){
        this.title = title;
        this.description = description;
        this.pairList = new ArrayList<TimePosPair>();
    }

    protected DipProfile(Parcel in) {
        title = in.readString();
        description = in.readString();
    }

    public static final Creator<DipProfile> CREATOR = new Creator<DipProfile>() {
        @Override
        public DipProfile createFromParcel(Parcel in) {
            return new DipProfile(in);
        }

        @Override
        public DipProfile[] newArray(int size) {
            return new DipProfile[size];
        }
    };

    public String getTitle(){
        return this.title;
    }

    public String getDescription(){
        return this.description;
    }

    public void setTitle(String newTitle){
        this.title = newTitle;
    }

    public void setDescription(String newDescription){
        this.description = newDescription;
    }

    public void addPair(TimePosPair newPair){
        this.pairList.add(newPair);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
    }
}
