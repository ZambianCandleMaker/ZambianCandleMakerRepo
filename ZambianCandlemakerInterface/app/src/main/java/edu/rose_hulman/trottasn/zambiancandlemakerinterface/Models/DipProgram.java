package edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models;

/**
 * Created by TrottaSN on 2/8/2016.
 */
public class DipProgram {
    private int mTimeDelay;
    private int mMaxAccelVert;
    private int mMaxAccelRot;
    private int mMaxVelVert;
    private int mMaxVelRot;
    private String mPath;

    public DipProgram(){

    }

    public DipProgram(int timeDelay, int maxAccelVert, int maxAccelRot, int maxVelVert, int maxVelRot, String path){
        this.mTimeDelay = timeDelay;
        this.mMaxAccelVert = maxAccelVert;
        this.mMaxAccelRot = maxAccelRot;
        this.mMaxVelVert = maxVelVert;
        this.mMaxVelRot = maxVelRot;
        this.mPath = path;
    }

    public int getTimeDelay() {
        return mTimeDelay;
    }

    public void setTimeDelay(int mTimeDelay) {
        this.mTimeDelay = mTimeDelay;
    }

    public int getMaxAccelVert() {
        return mMaxAccelVert;
    }

    public void setMaxAccelVert(int mMaxAccelVert) {
        this.mMaxAccelVert = mMaxAccelVert;
    }

    public int getMaxAccelRot() {
        return mMaxAccelRot;
    }

    public void setMaxAccelRot(int mMaxAccelRot) {
        this.mMaxAccelRot = mMaxAccelRot;
    }

    public int getMaxVelVert() {
        return mMaxVelVert;
    }

    public void setMaxVelVert(int mMaxVelVert) {
        this.mMaxVelVert = mMaxVelVert;
    }

    public int getMaxVelRot() {
        return mMaxVelRot;
    }

    public void setMaxVelRot(int mMaxVelRot) {
        this.mMaxVelRot = mMaxVelRot;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String mPath) {
        this.mPath = mPath;
    }
}
