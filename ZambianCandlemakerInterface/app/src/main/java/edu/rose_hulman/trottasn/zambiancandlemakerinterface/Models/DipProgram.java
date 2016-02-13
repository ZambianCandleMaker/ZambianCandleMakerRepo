package edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by TrottaSN on 2/8/2016.
 */
public class DipProgram {
    private String title;
    private String description;
    private int timeDelay;
    private int maxAccelVert;
    private int maxAccelRot;
    private int maxVelVert;
    private int maxVelRot;
    private String path;
    private List<DipProfile> profileList;
    public DipProgram(){
    }

    public DipProgram(List<String> inputStrings){
        this.title = inputStrings.get(1);
        this.description = inputStrings.get(2);
        this.maxAccelVert = Integer.parseInt(inputStrings.get(3));
        this.maxAccelRot = Integer.parseInt(inputStrings.get(4));
        this.maxVelVert = Integer.parseInt(inputStrings.get(5));
        this.maxVelRot = Integer.parseInt(inputStrings.get(6));
        this.profileList = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTimeDelay() {
        return this.timeDelay;
    }

    public void setTimeDelay(int timeDelay) {
        this.timeDelay = timeDelay;
    }

    public int getMaxAccelVert() {
        return this.maxAccelVert;
    }

    public void setMaxAccelVert(int maxAccelVert) {
        this.maxAccelVert = maxAccelVert;
    }

    public int getMaxAccelRot() {
        return this.maxAccelRot;
    }

    public void setMaxAccelRot(int maxAccelRot) {
        this.maxAccelRot = maxAccelRot;
    }

    public int getMaxVelVert() {
        return this.maxVelVert;
    }

    public void setMaxVelVert(int maxVelVert) {
        this.maxVelVert = maxVelVert;
    }

    public int getMaxVelRot() {
        return this.maxVelRot;
    }

    public void setMaxVelRot(int maxVelRot) {
        this.maxVelRot = maxVelRot;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void addToProfileList(DipProfile newProfile){
        if(this.profileList == null){
            this.profileList = new ArrayList<DipProfile>();
        }
        else{
            this.profileList.add(newProfile);
        }
    }

    public List<DipProfile> getProfileList() {
        return this.profileList;
    }

    public boolean assignFromReading(Map<String, String> typeToValueMapping, List<DipProfile> readProfiles){
        if(typeToValueMapping.size() == 0){
            return false;
        }
        for(String key : typeToValueMapping.keySet()){
            if(CSVUtility.PROGRAM_TITLE_KEY.equals(key)){
                this.title = typeToValueMapping.get(key);
            }
            else if(CSVUtility.PROGRAM_DESCRIPTION_KEY.equals(key)){
                this.description = typeToValueMapping.get(key);
            }
            else if(CSVUtility.PROGRAM_TIME_DELAY_KEY.equals(key)){
                this.timeDelay = Integer.parseInt(typeToValueMapping.get(key));
            }
            else if(CSVUtility.PROGRAM_MAX_AV_KEY.equals(key)){
                this.maxAccelVert = Integer.parseInt(typeToValueMapping.get(key));
            }
            else if(CSVUtility.PROGRAM_MAX_AR_KEY.equals(key)){
                this.maxAccelRot = Integer.parseInt(typeToValueMapping.get(key));
            }
            else if(CSVUtility.PROGRAM_MAX_VV_KEY.equals(key)){
                this.maxVelVert = Integer.parseInt(typeToValueMapping.get(key));
            }
            else if(CSVUtility.PROGRAM_MAX_VR_KEY.equals(key)){
                this.maxVelRot = Integer.parseInt(typeToValueMapping.get(key));
            }
            else{
                Log.d("UNEXPECTED_PARAMETER", "AssignFromReading got an unexpected parameter in DipProgram");
                return false;
            }
        }
        return true;
    }
}
