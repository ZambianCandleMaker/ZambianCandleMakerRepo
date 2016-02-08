package edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models;

import java.util.ArrayList;
import java.util.List;

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

    public DipProgram(String title, String description, String path){
        this.title = title;
        this.description = description;
        this.path = path;
        this.profileList = new ArrayList<DipProfile>();
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

    public List<DipProfile> getProfileList(){
        return this.profileList;
    }
}
