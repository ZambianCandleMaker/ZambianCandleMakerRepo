package edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * Created by TrottaSN on 2/4/2016.
 */
public class DipProfile implements Parcelable{
    private String title;
    private String description;
    private String path;

    private ArrayList<TimePosPair> pairList;
    private ArrayList<TimePosPair> maxYCoord = new ArrayList<TimePosPair>(){{
        add(new TimePosPair(1,1000));
    }};

    public DipProfile(){
        this.pairList = new ArrayList<>();
        addPair(0,0);

    }

    public DipProfile(String title, String description, String path){
        this();
        this.path = path;
        this.title = title;
        this.description = description;

    }

    public DipProfile(DipProfile profile){
        this.path = profile.getPath();
        this.title = profile.getTitle();
        this.description = profile.getDescription();
        this.pairList = new ArrayList<>();
        this.pairList.addAll(profile.getPairList());

        ListIterator<TimePosPair> i = this.pairList.listIterator();
        while(i.hasNext()){
            TimePosPair current = i.next();
            setMaxPos(current);

        }
    }

    protected DipProfile(Parcel in) {
        pairList = in.createTypedArrayList(TimePosPair.CREATOR);
        title = in.readString();
        description = in.readString();
        path = in.readString();
        maxYCoord = in.createTypedArrayList(TimePosPair.CREATOR);
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

        setMaxPos(newPair);

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

    public int addPair(int pos, int time){
        return addPair(new TimePosPair(pos,time));
    }

    public int removePair(int time){
        for(TimePosPair current : this.pairList){
               if(current.getTime() == time){
                   if(maxYCoord.contains(current)){
                       maxYCoord.remove(current);
                   }
                   int pos = this.pairList.indexOf(current);
                   this.pairList.remove(current);
                   return pos;
               }
        }
        return -1;
    }

    private void setMaxPos(TimePosPair current) {
        if(getMaxYCoordinate() <= current.getPosition()) this.maxYCoord.add(current);
    }

    public List<TimePosPair> getPairList() {
        if(this.pairList == null) addPair(0,0);
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
        dest.writeTypedList(maxYCoord);
    }

//    public LinkedList<TimePosPair> getLinkedList(){
//        LinkedList<TimePosPair> linkedList = new LinkedList<TimePosPair>();
//        for(TimePosPair p : pairList){
//            linkedList.add(p);
//        }
//        return linkedList;
//    }

    public LineGraphSeries<DataPoint> getLineGraphSeries(){

        ArrayList<DataPoint> arrayList = new ArrayList<DataPoint>();

        for(TimePosPair p: pairList){
            arrayList.add(new DataPoint(p.getTime(),Math.abs(p.getPosition() - getMaxYCoordinate())));
        }

        DataPoint[] dp = arrayList.toArray(new DataPoint[arrayList.size()]);

        return new LineGraphSeries<>(dp);
    }

    public double getMaxTime() {
        if(pairList.size() == 0) return 1000;
        int time = this.pairList.get(this.pairList.size()-1).getTime();
        if(time < 1000) return 1000;
        return this.pairList.get(this.pairList.size()-1).getTime();
    }

    public int getMaxYCoordinate() {
        return this.maxYCoord.get(maxYCoord.size() - 1).getPosition();
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
//            else if(CSVUtility.PROFILE_MAX_POS_KEY.equals(key)){
//                this.maxPos = Integer.parseInt(typeToValueMapping.get(key));
//            }
//            else if(CSVUtility.PROFILE_MAX_TIME_KEY.equals(key)){
//                this.maxTime = Integer.parseInt(typeToValueMapping.get(key));
//            }
            else{
                Log.d("CSVCHECK", "Unexpected parameter passed to assignFromReading");
                return false;
            }
        }
        pairList.addAll(timePosPairs);
        return true;
    }
}
