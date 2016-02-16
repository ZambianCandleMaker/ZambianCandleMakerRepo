package edu.rose_hulman.trottasn.zambiancandlemakerinterface;

import android.os.Environment;

/**
 * Created by TrottaSN on 2/8/2016.
 */
public class CONSTANTS {
    public static final String PROFILES_PATH_MAIN = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/Profile_CSVs";
    public static final String PROGRAMS_PATH_MAIN_INTERNAL = "Internal storage/DCIM/Program_CSVs";
    public static final String PROGRAMS_PATH_MAIN = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/Program_CSVs";
}
