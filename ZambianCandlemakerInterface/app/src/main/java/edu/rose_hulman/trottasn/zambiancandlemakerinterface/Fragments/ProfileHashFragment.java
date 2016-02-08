package edu.rose_hulman.trottasn.zambiancandlemakerinterface.Fragments;

import java.util.HashMap;

import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models.DipProfile;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models.DipProgram;

/**
 * Created by TrottaSN on 2/6/2016.
 */
public interface ProfileHashFragment{
    void setNewProfileHash(HashMap<String, DipProfile> newHash);
    void setNewProgramHash(HashMap<String, DipProgram> newHash);
}
