package edu.rose_hulman.trottasn.zambiancandlemakerinterface.Parcels;

import android.os.FileObserver;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by TrottaSN on 2/6/2016.
 */
public class FileObserverParcel implements Parcelable{

    private FileObserver fO;

    public FileObserverParcel(FileObserver fO){
        this.fO = fO;
    }

    protected FileObserverParcel(Parcel in) {
    }

    public static final Creator<FileObserverParcel> CREATOR = new Creator<FileObserverParcel>() {
        @Override
        public FileObserverParcel createFromParcel(Parcel in) {
            return new FileObserverParcel(in);
        }

        @Override
        public FileObserverParcel[] newArray(int size) {
            return new FileObserverParcel[size];
        }
    };

    public void setFileObserver(FileObserver fO){
        this.fO = fO;
    }

    public FileObserver getFileObserver(){
        return this.fO;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
