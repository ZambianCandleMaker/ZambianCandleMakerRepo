package edu.rose_hulman.trottasn.zambiancandlemakerinterface;

import android.os.Environment;

/**
 * Created by TrottaSN on 2/8/2016.
 */
public class CONSTANTS {

    public static final String TIME_DELAY_KEY = "TIME_DELAY";
    public static final String MAX_ACCEL_VERT_KEY = "MAX_ACCEL_VERT";
    public static final String MAX_ACCEL_ROT_KEY = "MAX_ACCEL_ROT";
    public static final String MAX_VEL_VERT_KEY = "MAX_VEL_VERT";
    public static final String MAX_VEL_ROT_KEY = "MAX_VEL_ROT";
    public static final String PROFILES_PATH_MAIN = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/Profile_CSVs";
    public static final String PROGRAMS_PATH_MAIN = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/Program_CSVs";
}
