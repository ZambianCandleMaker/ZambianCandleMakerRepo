package edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.lang.reflect.Type;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * Created by TrottaSN on 2/4/2016.
 */
public class DipProfile implements Parcelable{
    private List<TimePosPair> pairList;
    private String title;
    private String description;
    private String path;

    private int maxTime = 0;
    private int maxPos = 0;

    public DipProfile(){
        this.pairList = new ArrayList<>();
    }

    public DipProfile(String title, String description, String path){
        this();
        this.path = path;
        this.title = title;
        this.description = description;
        this.pairList = new ArrayList<>();
    }

    public DipProfile(DipProfile profile){
        this(profile.getTitle(),profile.getDescription(),profile.getPath());
        this.pairList.addAll(profile.getPairList());
    }

    protected DipProfile(Parcel in) {
        pairList = in.createTypedArrayList(TimePosPair.CREATOR);
        title = in.readString();
        description = in.readString();
        path = in.readString();
        maxTime = in.readInt();
        maxPos = in.readInt();
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

    //returns position to notify insert
    public int addPair(TimePosPair newPair){
        setMaxTimeAndPos(newPair.getTime(),newPair.getPosition());
        for(TimePosPair current: this.pairList){
                if(current.getTime() > newPair.getTime()){
                    this.pairList.add(this.pairList.indexOf(current),newPair);
                    return this.pairList.indexOf(newPair);
            }else if(current.getTime() == newPair.getTime()){
                    this.pairList.add(this.pairList.indexOf(current),newPair);
                    this.pairList.remove(this.pairList.indexOf(current));
                    return this.pairList.indexOf(newPair);
                }
        }
        this.pairList.add(newPair);
        return this.pairList.size()-1;
    }

    private void setMaxTimeAndPos(int time, int position) {
        if(this.maxTime < time) this.maxTime = time;
        if(this.maxPos < position) this.maxPos = position;
    }

    public List<TimePosPair> getPairList() {
        return this.pairList;
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
        dest.writeTypedList(pairList);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(path);
        dest.writeInt(maxTime);
        dest.writeInt(maxPos);
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

        return new LineGraphSeries<>(dp);
    }

    public int getMaxTime() {
        return maxTime;
    }

    public int getMaxPos() {
        return maxPos;
    }

    protected boolean assignFromReading(Map<String, String> typeToValueMapping, List<TimePosPair> timePosPairs){
        if(typeToValueMapping.size() == 0){
            return false;
        }
        for(String key : typeToValueMapping.keySet()){
            if(CSVUtility.PROFILE_TITLE_KEY.equals(key)){
                this.title = typeToValueMapping.get(key);
            }
            else if(CSVUtility.PROFILE_DESCRIPTION_KEY.equals(key)){
                this.description = typeToValueMapping.get(key);
            }
            else if(CSVUtility.PROFILE_MAX_POS_KEY.equals(key)){
                this.maxPos = Integer.parseInt(typeToValueMapping.get(key));
            }
            else if(CSVUtility.PROFILE_MAX_TIME_KEY.equals(key)){
                this.maxTime = Integer.parseInt(typeToValueMapping.get(key));
            }
            else{
                Log.d("CSVCHECK", "Unexpected parameter passed to assignFromReading");
                return false;
            }
        }
        pairList.addAll(timePosPairs);
        return true;
    }
}
