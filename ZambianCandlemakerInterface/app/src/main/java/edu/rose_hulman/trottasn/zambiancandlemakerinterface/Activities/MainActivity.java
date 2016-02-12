package edu.rose_hulman.trottasn.zambiancandlemakerinterface.Activities;

import android.content.SharedPreferences;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.rose_hulman.trottasn.zambiancandlemakerinterface.CONSTANTS;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Fragments.EditProfileFragment;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models.DipProgram;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Parcels.FileObserverResponder;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Fragments.AdminProfileChooserFragment;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Fragments.OperatorFragment;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models.DipProfile;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models.TimePosPair;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Parcels.ProfileHashParcel;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.R;

public class MainActivity extends AppCompatActivity
        implements OperatorFragment.OperatorFragmentListener, FileObserverResponder, NavigationView.OnNavigationItemSelectedListener, AdminProfileChooserFragment.OnAdminProfileChosenListener {

    private static HashMap<String, DipProfile> pathToProfileHash;
    private static HashMap<String, DipProgram> pathToProgramHash;

    private static String PREFS = "activity_prefs";
    public static String PROFILE_HASH = "profileHash";
    public static String PROGRAM_HASH = "PROGRAM_HASH";
    private SharedPreferences activityPrefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set Main Activity Layout to Be activity_main
        setContentView(R.layout.activity_main);

        // Save the Default SharedPreferences off to activityPrefs variable
        activityPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        // Create the pathToProfileHash HashMap
        pathToProfileHash = new HashMap<>();

        //Begin the Process of Reading in New Files and Saving Them
        File innerDir = new File(CONSTANTS.PROFILES_PATH_MAIN);
        innerDir.mkdirs();
        innerDir.setWritable(true);
        innerDir.setReadable(true);
        if(innerDir.exists()) {
            File[] files = innerDir.listFiles();
            for (int i = 0; i < files.length; ++i) {
                File file = files[i];
                if (file.isDirectory()) {
                    Log.d("NO_DIRECTORIES_ALLOWED", "There should not be a directory in this folder");
                } else {
                    readInProfileCSVFile(file);
                }
            }
        }

        // After this is done, read in the program files and link them to the profile from the hash
        repopulateProgramHash();

        // May be able to use this soon
//        mProfileObserver = new FileObserver(CONSTANTS.PROFILES_PATH_MAIN) {
//            @Override
//            public void onEvent(int event, String path) {
//                if(event == FileObserver.ALL_EVENTS){
//                    repopulateProfileHash();
//                }
//            }
//        };
//        mProgramObserver = new FileObserver(CONSTANTS.PROGRAMS_PATH_MAIN) {
//            @Override
//            public void onEvent(int event, String path) {
//                if(event == FileObserver.ALL_EVENTS){
//                    repopulateProgramHash();
//                }
//            }
//        };
//        mProfileObserver.startWatching();
//        mProgramObserver.startWatching();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //changed scope so can hide it
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if(savedInstanceState == null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.fragment_container, new OperatorFragment());
            ft.commit();
        }
    }

    /*
        READS IN A SINGLE PROFILE FROM A PROFILE CSV IN THE APPROPRIATE DIRECTORY
     */
    public void readInProfileCSVFile(File file){
        file.setWritable(true);
        MediaScannerConnection.scanFile(this, new String[]{file.toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
            public void onScanCompleted(String path, Uri uri) {
                Log.i("EXTERNAL STORAGE", "SCANNED");
            }
        });
        final String filename = file.toString();
        final List<String> strList = new ArrayList<String>();
        final DipProfile newProfile = new DipProfile();
        try {
            CSVReader reader = new CSVReader(new FileReader(filename));
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                for(int i = 0; i < nextLine.length; i++){
                    strList.add(nextLine[i]);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("PATHNAME", filename);
        if(strList.size() < 2){
            Log.d("INVALID_CSV_FOR_PROFILE", "CSV has less than two entries (no title / description)");
            return;
        };
        if(strList.size()%2 != 0){
            Log.d("INVALID_CSV_FOR_PROFILE", "CSV has an odd number of entries (there exists an unequal pair)");
        }
        newProfile.setTitle(strList.get(0));
        newProfile.setDescription(strList.get(1));
        newProfile.setPath(filename);
        for(int i = 2; i < strList.size(); i+=2){
            TimePosPair newPair = new TimePosPair(Integer.parseInt(strList.get(i)), Integer.parseInt(strList.get(i+1)));
            newProfile.addPair(newPair);
        }
        pathToProfileHash.put(newProfile.getPath(), newProfile);
    }

    /*
    READS IN A SINGLE PROGRAM FROM THE APPROPRIATE PROGRAM DIRECTORY AND LINKS TO PROFILES
     */
    public void readInProgramCSVFile(File file){
        file.setWritable(true);
        MediaScannerConnection.scanFile(this, new String[]{file.toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
            public void onScanCompleted(String path, Uri uri) {
            }
        });
        final String filename = file.toString();
        final List<String> strList = new ArrayList<String>();
        final DipProgram newProgram = new DipProgram();
        try {
            CSVReader reader = new CSVReader(new FileReader(filename));
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                for(int i = 0; i < nextLine.length; i++){
                    strList.add(nextLine[i]);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(strList.size() < 6){
            Log.d("INVALID_CSV_FOR_PROGRAM", "CSV has less than six entries (not enough information)");
            return;
        };

        newProgram.setTitle(strList.get(0));
        newProgram.setDescription(strList.get(1));
        newProgram.setPath(filename);
        newProgram.setMaxAccelVert(Integer.parseInt(strList.get(2)));
        newProgram.setMaxAccelRot(Integer.parseInt(strList.get(3)));
        newProgram.setMaxVelVert(Integer.parseInt(strList.get(4)));
        newProgram.setMaxVelRot(Integer.parseInt(strList.get(5)));
        for(int i = 6; i < strList.size(); i+=1){
            DipProfile nextProfile = pathToProfileHash.get(strList.get(i));
            Log.d("TEST", strList.get(i));
            if(nextProfile == null){
                Log.d("INVALID_CSV_FOR_PROGRAM", "CSV has unidentified path to Profile (non-existent profile)");
                return;
            }
            newProgram.addToProfileList(nextProfile);
        }
        Log.d("Name: ", newProgram.getTitle());
        Log.d("Desc: ", newProgram.getDescription());
        Log.d("Path: ", newProgram.getPath());
        Log.d("MAV: ", String.valueOf(newProgram.getMaxAccelVert()));
        Log.d("MAR: ", String.valueOf(newProgram.getMaxAccelRot()));
        Log.d("MVV: ", String.valueOf(newProgram.getMaxVelVert()));
        Log.d("MVR: ", String.valueOf(newProgram.getMaxVelRot()));
        for(DipProfile dP : newProgram.getProfileList()){
            Log.d("DPT: ", dP.getTitle());
            Log.d("DPD: ", dP.getDescription());
        }
        pathToProgramHash.put(filename, newProgram);
    }

    /*
    Overriding onBackPressed in order to close drawer and possibly, in the future, prevent exiting app from backpress in a meaningful way.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        };
        drawer.setSelected(false);
        super.onBackPressed();
    }

    /*
    Creates the Options Menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_choose_program) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment switchTo = null;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        int id = item.getItemId();
        switch (id){
            case R.id.nav_operator:
                //Switch without adding to backstack
                switchTo = new OperatorFragment();
                break;
            case R.id.nav_administrator_profile:
                //Add to backstack if nothing there or if Operator is not there
                if(getSupportFragmentManager().getBackStackEntryCount() == 0 || !getSupportFragmentManager().getBackStackEntryAt(0).getName().equals(getString(R.string.operator_frag_name))) {
                    ft.addToBackStack(getString(R.string.operator_frag_name));
                }
                switchTo = null;
                break;
            case R.id.nav_administrator_program:
                //Add to backstack like above
                if(getSupportFragmentManager().getBackStackEntryCount() == 0 || !getSupportFragmentManager().getBackStackEntryAt(0).getName().equals(getString(R.string.operator_frag_name))) {
                    ft.addToBackStack(getString(R.string.operator_frag_name));
                }
                AdminProfileChooserFragment adminFrag = AdminProfileChooserFragment.newInstance();
                switchTo = adminFrag;
                break;

            case R.id.nav_graph_make_profile:
                if(getSupportFragmentManager().getBackStackEntryCount() == 0 || !getSupportFragmentManager().getBackStackEntryAt(0).getName().equals(getString(R.string.operator_frag_name))) {
                    ft.addToBackStack(getString(R.string.operator_frag_name));
                }
                EditProfileFragment editFrag = EditProfileFragment.newInstance(new ProfileHashParcel(pathToProfileHash));
                switchTo = editFrag;
                editFrag.setNewProfileHash(pathToProfileHash);
                break;
        }

        if (switchTo != null){
            ft.replace(R.id.fragment_container, switchTo);
//            for(int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++){
//                getSupportFragmentManager().popBackStackImmediate();
//            }
            ft.commit();
        };

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onProfileChosen(Uri uri) {
        // Nothing - Possibly Always Nothing
    }

    /*
    Simply reassigns/reads in the profiles to the hash file from the profile directory
     */
    @Override
    public void repopulateProfileHash() {
        pathToProfileHash = new HashMap<>();
        File innerDir = new File(CONSTANTS.PROFILES_PATH_MAIN);
        innerDir.mkdirs();
        innerDir.setWritable(true);
        innerDir.setReadable(true);
        if(innerDir.exists()) {
            File[] files = innerDir.listFiles();
            for (int i = 0; i < files.length; ++i) {
                File file = files[i];
                if (file.isDirectory()) {
                    Log.d("NO_DIRECTORIES_ALLOWED", "There should not be a directory in this folder");
                } else {
                    readInProfileCSVFile(file);
                }
            }
        }
    }

    @Override
    public void repopulateProgramHash() {
        pathToProgramHash = new HashMap<>();
        File innerDir = new File(CONSTANTS.PROGRAMS_PATH_MAIN);
        innerDir.mkdirs();
        innerDir.setWritable(true);
        innerDir.setReadable(true);
        if(innerDir.exists()) {
            File[] files = innerDir.listFiles();
            for (int i = 0; i < files.length; ++i) {
                File file = files[i];
                if (file.isDirectory()) {
                    Log.d("NO_DIRECTORIES_ALLOWED", "There should not be a directory in this folder");
                } else {
                    readInProgramCSVFile(file);
                }
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences.Editor prefsEditor = activityPrefs.edit();
        Gson gson = new Gson();
        String pathToProfileHashJson = gson.toJson(pathToProfileHash);
        String pathToProgramHashJson = gson.toJson(pathToProgramHash);
        prefsEditor.putString(PROFILE_HASH, pathToProfileHashJson);
        prefsEditor.putString(PROGRAM_HASH, pathToProgramHashJson);
        prefsEditor.apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor prefsEditor = activityPrefs.edit();
        Gson gson = new Gson();
        String pathToProfileHashJson = gson.toJson(pathToProfileHash);
        String pathToProgramHashJson = gson.toJson(pathToProgramHash);
        prefsEditor.putString(PROFILE_HASH, pathToProfileHashJson);
        prefsEditor.putString(PROGRAM_HASH, pathToProgramHashJson);
        prefsEditor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Gson gson = new Gson();
        if(activityPrefs.contains(PROFILE_HASH)){
            String json = activityPrefs.getString(PROFILE_HASH, "");
            Type hashType = new TypeToken<HashMap<String ,DipProfile>>(){}.getType();
            pathToProfileHash = gson.fromJson(json, hashType);
        }
        if(activityPrefs.contains(PROGRAM_HASH)){
            String jsonPrograms = activityPrefs.getString(PROGRAM_HASH, "");
            Type progHashType = new TypeToken<HashMap<String, DipProgram>>(){}.getType();
            pathToProgramHash = gson.fromJson(jsonPrograms, progHashType);
        }
    }

    public void savePathToProfileHash(HashMap<String, DipProfile> hashMap){
        SharedPreferences.Editor prefsEditor = activityPrefs.edit();
        Gson gson = new Gson();
        String pathToProfileHashJson = gson.toJson(pathToProfileHash);

        prefsEditor.putString(PROFILE_HASH, pathToProfileHashJson);
        prefsEditor.apply();
    }
}
