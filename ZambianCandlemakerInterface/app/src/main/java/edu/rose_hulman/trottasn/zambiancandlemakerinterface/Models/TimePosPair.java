package edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models;

/**
 * Created by TrottaSN on 1/27/2016.
 */
public class TimePosPair {
    private int position;
    private int time;

    public TimePosPair(int position, int time){
        this.position = position;
        this.time = time;
    }

    public void setPosition(int position){
        this.position = position;
    }

    public void setTime(int time){
        this.time = time;
    }

    public int getPosition(){
        return this.position;
    }

    public int getTime(){
        return this.time;
    }
}
