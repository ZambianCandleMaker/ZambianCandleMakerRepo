package edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by TrottaSN on 1/27/2016.
 */
public class TimePosPair implements Parcelable {
    private int position;
    private int time;

    public TimePosPair(int position, int time){
        this.position = position;
        this.time = time;
    }

    protected TimePosPair(Parcel in) {
        position = in.readInt();
        time = in.readInt();
    }

    public static final Creator<TimePosPair> CREATOR = new Creator<TimePosPair>() {
        @Override
        public TimePosPair createFromParcel(Parcel in) {
            return new TimePosPair(in);
        }

        @Override
        public TimePosPair[] newArray(int size) {
            return new TimePosPair[size];
        }
    };

    public void setPosition(int position){
        this.position = position;
    }

    public void setTime(int time){
        this.time = time;
    }

    public int getPosition(){
        return this.position;
    }

    public int getTime(){
        return this.time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(position);
        dest.writeInt(time);
    }

    public String getRepresentation(){
        return String.valueOf(this.time) + ":" + String.valueOf(this.position);
    }
}
