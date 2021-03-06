package edu.rose_hulman.trottasn.zambiancandlemakerinterface.Parcels;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models.DipProgram;

/**
 * Created by TrottaSN on 2/6/2016.
 */
public class ProgramHashParcel implements Parcelable {

    private Map<String, DipProgram> pathToProgramHash;

    public ProgramHashParcel(Map<String, DipProgram> inHash){
        this.pathToProgramHash = inHash;
    }

    protected ProgramHashParcel(Parcel in) {
    }

    public static final Creator<ProgramHashParcel> CREATOR = new Creator<ProgramHashParcel>() {
        @Override
        public ProgramHashParcel createFromParcel(Parcel in) {
            return new ProgramHashParcel(in);
        }

        @Override
        public ProgramHashParcel[] newArray(int size) {
            return new ProgramHashParcel[size];
        }
    };

    public Map<String, DipProgram> getHash(){
        return this.pathToProgramHash;
    }

    public void setHash(Map<String, DipProgram> inHash){
        this.pathToProgramHash = inHash;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}