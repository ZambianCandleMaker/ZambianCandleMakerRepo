package edu.rose_hulman.trottasn.zambiancandlemakerinterface;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

/**
 * Created by TrottaSN on 2/6/2016.
 */
public class HashMapParcel implements Parcelable {

    private HashMap<String, DipProfile> pathToProfileHash;

    public HashMapParcel(HashMap<String, DipProfile> inHash){
        this.pathToProfileHash = inHash;
    }

    protected HashMapParcel(Parcel in) {
    }

    public static final Creator<HashMapParcel> CREATOR = new Creator<HashMapParcel>() {
        @Override
        public HashMapParcel createFromParcel(Parcel in) {
            return new HashMapParcel(in);
        }

        @Override
        public HashMapParcel[] newArray(int size) {
            return new HashMapParcel[size];
        }
    };

    public HashMap<String, DipProfile> getHash(){
        return this.pathToProfileHash;
    }

    public void setHash(HashMap<String, DipProfile> inHash){
        this.pathToProfileHash = inHash;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
