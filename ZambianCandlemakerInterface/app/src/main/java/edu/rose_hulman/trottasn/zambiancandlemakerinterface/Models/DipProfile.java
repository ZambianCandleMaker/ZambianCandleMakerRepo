package edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by TrottaSN on 2/4/2016.
 */
public class DipProfile implements Parcelable{
    private ArrayList<TimePosPair> pairList;
    private String title;
    private String description;
    private String path;

    public DipProfile(){
        this.pairList = new ArrayList<TimePosPair>();
    }

    public DipProfile(String title, String description, String path){
        this.path = path;
        this.title = title;
        this.description = description;
        this.pairList = new ArrayList<TimePosPair>();
    }

    protected DipProfile(Parcel in) {
        title = in.readString();
        description = in.readString();
        path = in.readString();
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

    public String getPath(){
        return this.path;
    }

    public void setPath(String newPath){
        this.path = newPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(path);
    }

    public LinkedList<TimePosPair> getLinkedList(){
        LinkedList<TimePosPair> linkedList = new LinkedList<TimePosPair>();
        for(TimePosPair p : pairList){
            linkedList.add(p);
        }
        return linkedList;
    }

    public LineGraphSeries<DataPoint> getLineGraphSeries(){

        ArrayList<DataPoint> arrayList = new ArrayList<DataPoint>();

        for(TimePosPair p: pairList){
            arrayList.add(new DataPoint(p.getTime(),p.getPosition()));
        }

        DataPoint[] dp = arrayList.toArray(new DataPoint[arrayList.size()]);

        return new LineGraphSeries<DataPoint>(dp);
    }

}
