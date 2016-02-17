package edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.rose_hulman.trottasn.zambiancandlemakerinterface.CONSTANTS;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.R;

/**
 * Created by TrottaSN on 2/13/2016.
 */
public class CSVUtility {

    public static final String NEXT_SECTION_DELIMITER = "#####";

    public static final String PROFILE_TITLE_KEY = "TITLE";
    public static final String PROFILE_DESCRIPTION_KEY = "DESCRIPTION";

    public static final String PROGRAM_TITLE_KEY = "TITLE";
    public static final String PROGRAM_DESCRIPTION_KEY = "DESCRIPTION";
    public static final String PROGRAM_TIME_DELAY_KEY = "TIME_DELAY";
    public static final String PROGRAM_MAX_AV_KEY = "MAX_AV";
    public static final String PROGRAM_MAX_AR_KEY = "MAX_AR";
    public static final String PROGRAM_MAX_VV_KEY = "MAX_VV";
    public static final String PROGRAM_MAX_VR_KEY = "MAX_VR";

    public static DipProfile readProfileCSV(File file, Activity activity){
        file.setWritable(true);
        MediaScannerConnection.scanFile(activity, new String[]{file.toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
            public void onScanCompleted(String path, Uri uri) {
                Log.i("EXTERNAL STORAGE", "SCANNED");
            }
        });
        final String filename = file.toString();
        final DipProfile newProfile = new DipProfile();
        try {
            Map<String, String> typeToValueMapping = new HashMap<>();
            List<TimePosPair> timePosPairs = new ArrayList<>();
            CSVReader reader = new CSVReader(new FileReader(filename));
            String[] nextLine;
            boolean informationSectionRead = false;
            int pairing = 2;
            while ((nextLine = reader.readNext()) != null) {
                if(!informationSectionRead){
                    if(nextLine.length != pairing){
                        Log.d("CSVCHECK", "CSV has unequal \"pairing\" of values" + " " + nextLine.toString() + " " + String.valueOf(nextLine.length));
                        return null;
                    }
                    else if(nextLine[0].startsWith(NEXT_SECTION_DELIMITER)){
                        if(!nextLine[1].equals(NEXT_SECTION_DELIMITER)){
                            Log.d("CSVCHECK", "CSV needs double delimiter \"#####    #####\" for parsing");
                            return null;
                        }
                        informationSectionRead = true;
                    }
                    else{
                        typeToValueMapping.put(nextLine[0], nextLine[1]);
                    }
                }
                else{
                    if(nextLine.length != pairing){
                        Log.d("CSVCHECK", "Position and Time pairings must be of length 2");
                        return null;
                    }
                    else{
                        TimePosPair newTPP = new TimePosPair(Integer.parseInt(nextLine[0]), Integer.parseInt(nextLine[1]));
                        timePosPairs.add(newTPP);
                    }
                }
            }
            boolean success = newProfile.assignFromReading(typeToValueMapping, timePosPairs);
            if(success){
                return newProfile;
            }
            return null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static DipProgram readProgramCSV(File file, Activity activity, Map<String, DipProfile> pathToProfileHash){
        file.setWritable(true);
        MediaScannerConnection.scanFile(activity, new String[]{file.toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
            public void onScanCompleted(String path, Uri uri) {
            }
        });
        final String filename = file.toString();
        try {
            DipProgram newProgram = new DipProgram();
            Map<String, String> typeToValueMapping = new HashMap<>();
            List<DipProfile> profileList = new ArrayList<>();
            boolean informationSectionRead = false;
            int pairing = 2;
            int singular = 1;
            CSVReader reader = new CSVReader(new FileReader(filename));
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if(!informationSectionRead){
                    if(nextLine.length != pairing){
                        Log.d("INVALID_CSV_FOR_PROFILE", "CSV has unequal \"paring\" of values" + " " + nextLine[0] + " " + String.valueOf(nextLine.length));
                        return null;
                    }
                    else if(nextLine[0].startsWith(NEXT_SECTION_DELIMITER)){
                        if(!nextLine[1].equals(NEXT_SECTION_DELIMITER)){
                            Log.d("INVALID_CSV_FOR_PROFILE", "CSV needs double delimiter \"#####    #####\" for parsing");
                            return null;
                        }
                        informationSectionRead = true;
                    }
                    else{
                        typeToValueMapping.put(nextLine[0], nextLine[1]);
                    }
                }
                else{
                    if(nextLine.length != singular){
                        Log.d("INVALID_CSV_FOR_PROGRAM", "Should be a pointer to a profile title, got more than one entry.");
                        return null;
                    }
                    else{
                        DipProfile nextProfile = pathToProfileHash.get(nextLine[0]);
                        if(nextProfile == null){
                            Log.d("INVALID_CSV_FOR_PROGRAM", "Pointer to profile title did not correspond to an actual profile.");
                            return null;
                        }
                        profileList.add(nextProfile);
                    }
                }
            }
            boolean success = newProgram.assignFromReading(typeToValueMapping, profileList);
            if(success){
                return newProgram;
            }
            return null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean writeProgramCSV(Map<String, String> typeToValueMap, List<String> profileTitles, Activity activity){
        File finalFile = new File(CONSTANTS.PROGRAMS_PATH_MAIN + "/" + typeToValueMap.get(PROGRAM_TITLE_KEY) + ".csv");
        finalFile.setWritable(true);
        final String filename = finalFile.toString();
        CharSequence contentTitle = activity.getString(R.string.app_name);
        final ProgressDialog progDialog = ProgressDialog.show(
                activity, contentTitle, "Please Wait.",
                true);//please wait
        MediaScannerConnection.scanFile(activity, new String[]{filename}, null, new MediaScannerConnection.OnScanCompletedListener() {
            public void onScanCompleted(String path, Uri uri) {
                Log.i("EXTERNAL STORAGE", "SCANNED");
            }
        });
        int pairing = 2;
        int singular = 1;
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(filename));
            // feed in your array (or convert your data to an array)
            List<String> alphabeticalList = new ArrayList<>();
            alphabeticalList.addAll(typeToValueMap.keySet());
            Collections.sort(alphabeticalList);
            for(String key : alphabeticalList){
                String[] stringsToWrite = new String[pairing];
                stringsToWrite[0] = key;
                stringsToWrite[1] = typeToValueMap.get(key);
                writer.writeNext(stringsToWrite);
            }
            String[] delimiters = new String[pairing];
            delimiters[0] = NEXT_SECTION_DELIMITER;
            delimiters[1] = NEXT_SECTION_DELIMITER;
            writer.writeNext(delimiters);
            for(String profileName : profileTitles){
                String[] nameToWrite = new String[singular];
                nameToWrite[0] = profileName;
                writer.writeNext(nameToWrite);
            }
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        progDialog.dismiss();
        return true;
    }

    public static boolean writeProfileCSV(Map<String, String> typeToValueMap, List<TimePosPair> timePosPairs, Activity activity){
        File finalFile = new File(CONSTANTS.PROFILES_PATH_MAIN + "/" + typeToValueMap.get(PROFILE_TITLE_KEY) + ".csv");
        finalFile.setWritable(true);
        final String filename = finalFile.toString();
        CharSequence contentTitle = activity.getString(R.string.app_name);
        final ProgressDialog progDialog = ProgressDialog.show(
                activity, contentTitle, "Please Wait.",
                true);//please wait
        MediaScannerConnection.scanFile(activity, new String[]{filename}, null, new MediaScannerConnection.OnScanCompletedListener() {
            public void onScanCompleted(String path, Uri uri) {
                Log.i("EXTERNAL STORAGE", "SCANNED");
            }
        });
        int pairing = 2;
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(filename));
            // feed in your array (or convert your data to an array)
            List<String> alphabeticalList = new ArrayList<>();
            alphabeticalList.addAll(typeToValueMap.keySet());
            Collections.sort(alphabeticalList);
            for(String key : alphabeticalList){
                String[] stringsToWrite = new String[pairing];
                stringsToWrite[0] = key;
                stringsToWrite[1] = typeToValueMap.get(key);
                writer.writeNext(stringsToWrite);
            }
            String[] delimiters = new String[pairing];
            delimiters[0] = NEXT_SECTION_DELIMITER;
            delimiters[1] = NEXT_SECTION_DELIMITER;
            writer.writeNext(delimiters);
            for(TimePosPair pair : timePosPairs){
                String[] nameToWrite = new String[pairing];
                nameToWrite[0] = String.valueOf(pair.getPosition());
                nameToWrite[1] = String.valueOf(pair.getTime());
                writer.writeNext(nameToWrite);
            }
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        progDialog.dismiss();
        return true;
    }
}
