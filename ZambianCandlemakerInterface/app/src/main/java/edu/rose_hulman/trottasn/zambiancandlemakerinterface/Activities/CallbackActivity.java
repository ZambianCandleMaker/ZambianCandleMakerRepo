package edu.rose_hulman.trottasn.zambiancandlemakerinterface.Activities;

import java.util.Map;

import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models.DipProfile;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models.DipProgram;

/**
 * Created by TrottaSN on 2/12/2016.
 */
public interface CallbackActivity {
    void savePathToProfileHash(Map<String, DipProfile> hashMap);
    void switchToProgramEdit(DipProgram dipProgram);
}
