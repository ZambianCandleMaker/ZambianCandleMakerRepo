package edu.rose_hulman.trottasn.zambiancandlemakerinterface;

import java.util.ArrayList;

/**
 * Created by TrottaSN on 2/4/2016.
 */
public class DipProfile {
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
}
