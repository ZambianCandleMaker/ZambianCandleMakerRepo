package edu.rose_hulman.trottasn.zambiancandlemakerinterface.Parcels;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models.DipProfile;

/**
 * Created by TrottaSN on 2/6/2016.
 */
public class ProfileHashParcel implements Parcelable {

    private Map<String, DipProfile> pathToProfileHash;

    public ProfileHashParcel(Map<String, DipProfile> inHash){
        this.pathToProfileHash = inHash;
    }

    protected ProfileHashParcel(Parcel in) {
    }

    public static final Creator<ProfileHashParcel> CREATOR = new Creator<ProfileHashParcel>() {
        @Override
        public ProfileHashParcel createFromParcel(Parcel in) {
            return new ProfileHashParcel(in);
        }

        @Override
        public ProfileHashParcel[] newArray(int size) {
            return new ProfileHashParcel[size];
        }
    };

    public Map<String, DipProfile> getHash(){
        return this.pathToProfileHash;
    }

    public void setHash(Map<String, DipProfile> inHash){
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
