package edu.rose_hulman.trottasn.zambiancandlemakerinterface.Activities;

import java.util.HashMap;

import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models.DipProfile;

/**
 * Created by TrottaSN on 2/12/2016.
 */
public interface CallbackActivity {
    void savePathToProfileHash(HashMap<String, DipProfile> hashMap);
}
