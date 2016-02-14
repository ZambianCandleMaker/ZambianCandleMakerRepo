package edu.rose_hulman.trottasn.zambiancandlemakerinterface.Activities;

import android.content.SharedPreferences;
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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Adapters.ProgModDelAdapter;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.CONSTANTS;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Fragments.EditProfileFragment;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Fragments.ProgramModDelFrag;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models.CSVUtility;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models.DipProgram;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Fragments.AdminProfileChooserFragment;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Fragments.OperatorFragment;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models.DipProfile;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Parcels.ProfileHashParcel;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.R;

public class MainActivity extends AppCompatActivity
        implements OperatorFragment.OperatorFragmentListener, NavigationView.OnNavigationItemSelectedListener, AdminProfileChooserFragment.OnAdminProfileChosenListener, CallbackActivity {

    private static Map<String, DipProfile> pathToProfileHash;
    private static Map<String, DipProgram> pathToProgramHash;

    public static final String PROFILE_HASH = "profileHash";
    public static final String PROGRAM_HASH = "PROGRAM_HASH";
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
        repopulateProfileHash();

        // After this is done, read in the program files and link them to the profile from the hash
        repopulateProgramHash();

        if(activityPrefs != null){
            SharedPreferences.Editor editor = activityPrefs.edit();
            Gson gson = new Gson();
            String pathToProfileHashJson = gson.toJson(pathToProfileHash);
            String pathToProgramHashJson = gson.toJson(pathToProgramHash);
            editor.putString(PROFILE_HASH, pathToProfileHashJson);
            editor.putString(PROGRAM_HASH, pathToProgramHashJson);
            editor.apply();
        }

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
            case R.id.nav_administrator_mod_del_program:
                if(getSupportFragmentManager().getBackStackEntryCount() == 0 || !getSupportFragmentManager().getBackStackEntryAt(0).getName().equals(getString(R.string.operator_frag_name))){
                    ft.addToBackStack(getString(R.string.operator_frag_name));
                }
                ProgramModDelFrag modDelFrag = ProgramModDelFrag.newInstance();
                switchTo = modDelFrag;
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
                break;
        }

        if (switchTo != null){
            ft.replace(R.id.fragment_container, switchTo);
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
                    DipProfile newProfile = CSVUtility.readProfileCSV(file, this);
                    if(newProfile != null){
                        pathToProfileHash.put(newProfile.getTitle(), newProfile);
                    }
                }
            }
        }
    }

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
                    DipProgram newProgram = CSVUtility.readProgramCSV(file, this, pathToProfileHash);
                    if(newProgram != null){
                        pathToProgramHash.put(newProgram.getTitle(), newProgram);
                    }
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
//        Gson gson = new Gson();
//        if(activityPrefs.contains(PROFILE_HASH)){
//            String json = activityPrefs.getString(PROFILE_HASH, "");
//            Type hashType = new TypeToken<Map<String ,DipProfile>>(){}.getType();
//            pathToProfileHash.putAll((Map<String, DipProfile>)gson.fromJson(json, hashType));
//        }
//        if(activityPrefs.contains(PROGRAM_HASH)){
//            String jsonPrograms = activityPrefs.getString(PROGRAM_HASH, "");
//            Type progHashType = new TypeToken<Map<String, DipProgram>>(){}.getType();
//            pathToProgramHash.putAll((Map<String, DipProgram>)gson.fromJson(jsonPrograms, progHashType));
//        }
    }

    @Override
    public void savePathToProfileHash(Map<String, DipProfile> hashMap){
        SharedPreferences.Editor prefsEditor = activityPrefs.edit();
        Gson gson = new Gson();
        String pathToProfileHashJson = gson.toJson(hashMap);

        prefsEditor.putString(PROFILE_HASH, pathToProfileHashJson);
        prefsEditor.apply();
    }
}
