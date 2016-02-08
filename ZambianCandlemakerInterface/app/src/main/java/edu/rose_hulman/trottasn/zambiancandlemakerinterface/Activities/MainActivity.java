package edu.rose_hulman.trottasn.zambiancandlemakerinterface.Activities;

import android.app.ProgressDialog;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileObserver;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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

import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.rose_hulman.trottasn.zambiancandlemakerinterface.CONSTANTS;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models.DipProgram;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Parcels.FileObserverResponder;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Fragments.AdminProfileChooserFragment;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Fragments.OperatorFragment;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Fragments.ProfileHashFragment;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models.DipProfile;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models.TimePosPair;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Parcels.FileObserverParcel;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Parcels.ProfileHashParcel;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Parcels.ProgramHashParcel;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.R;

public class MainActivity extends AppCompatActivity
        implements OperatorFragment.OperatorFragmentListener, FileObserverResponder, NavigationView.OnNavigationItemSelectedListener, AdminProfileChooserFragment.OnAdminProfileChosenListener {

    private static HashMap<String, DipProfile> pathToProfileHash;
    private static HashMap<String, DipProgram> pathToProgramHash;
    private static FileObserver mProfileObserver;
    private static FileObserver mProgramObserver;
    private ProfileHashFragment currFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        repopulateProfileHash();

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                repopulateProgramHash();
            }
        };

        new Thread(){
            public void run(){
                Looper.prepare();
                pathToProfileHash = new HashMap<>();
                File innerDir = new File(CONSTANTS.PROFILES_PATH_MAIN);
                innerDir.mkdirs();
                innerDir.setWritable(true);
                innerDir.setReadable(true);
                List<Thread> threadList = new ArrayList<Thread>();
                if(innerDir.exists()) {
                    File[] files = innerDir.listFiles();
                    for (int i = 0; i < files.length; ++i) {
                        File file = files[i];
                        if (file.isDirectory()) {
                            Log.d("NO_DIRECTORIES_ALLOWED", "There should not be a directory in this folder");
                        } else {
                            threadList.add(readInProfileCSVFile(file));
                        }
                    }
                }
                for(Thread toStart : threadList){
                    toStart.start();
                }
                while(true){
                    boolean oneAlive = false;
                    for(Thread thread : threadList){
                        if(thread.isAlive()){
                            oneAlive = true;
                        }
                    }
                    if(!oneAlive){
                        break;
                    }
                }
                if(currFragment != null){
                    currFragment.setNewProfileHash(pathToProfileHash);
                }
                handler.sendEmptyMessage(0);
            }
        }.start();

        mProfileObserver = new FileObserver(CONSTANTS.PROFILES_PATH_MAIN) {
            @Override
            public void onEvent(int event, String path) {
                if(event == FileObserver.ALL_EVENTS){
                    repopulateProfileHash();
                }
            }
        };
        mProgramObserver = new FileObserver(CONSTANTS.PROGRAMS_PATH_MAIN) {
            @Override
            public void onEvent(int event, String path) {
                if(event == FileObserver.ALL_EVENTS){
                    repopulateProgramHash();
                }
            }
        };

        mProfileObserver.startWatching();
        mProgramObserver.startWatching();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

    public Thread readInProfileCSVFile(File file){
        file.setWritable(true);
        MediaScannerConnection.scanFile(this, new String[]{file.toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
            public void onScanCompleted(String path, Uri uri) {
                Log.i("EXTERNAL STORAGE", "SCANNED");
            }
        });
        final String filename = file.toString();
        CharSequence contentTitle = getString(R.string.app_name);
        final List<String> strList = new ArrayList<String>();
        final ProgressDialog progDialog = ProgressDialog.show(
                this, contentTitle, "Please Wait.",
                true);//please wait
        final DipProfile newProfile = new DipProfile();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
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
        };
        Thread thread = new Thread(){
            public void run(){
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
                handler.sendEmptyMessage(0);
                progDialog.dismiss();
            }
        };
        return thread;
    }

    public void readInProgramCSVFile(File file){
        file.setWritable(true);
        MediaScannerConnection.scanFile(this, new String[]{file.toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
            public void onScanCompleted(String path, Uri uri) {
            }
        });
        final String filename = file.toString();
        CharSequence contentTitle = getString(R.string.app_name);
        final List<String> strList = new ArrayList<String>();
        final ProgressDialog progDialog = ProgressDialog.show(
                this, contentTitle, "Please Wait.",
                true);//please wait
        final DipProgram newProgram = new DipProgram();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
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
        };
        new Thread(){
            public void run(){
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
                handler.sendEmptyMessage(0);
                progDialog.dismiss();
            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(getSupportFragmentManager().getBackStackEntryCount() == 0) {
                drawer.setSelected(false);
                return;
            }
            super.onBackPressed();
            drawer.setSelected(false);
        }
    }

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
                switchTo = new OperatorFragment();
                break;
            case R.id.nav_administrator_profile:
                if(getSupportFragmentManager().getBackStackEntryCount() == 0 || !getSupportFragmentManager().getBackStackEntryAt(0).getName().equals(getString(R.string.operator_frag_name))) {
                    ft.addToBackStack(getString(R.string.operator_frag_name));
                }
                switchTo = null;
                break;
            case R.id.nav_administrator_program:
                if(getSupportFragmentManager().getBackStackEntryCount() == 0 || !getSupportFragmentManager().getBackStackEntryAt(0).getName().equals(getString(R.string.operator_frag_name))) {
                    ft.addToBackStack(getString(R.string.operator_frag_name));
                }
                AdminProfileChooserFragment adminFrag = AdminProfileChooserFragment.newInstance(new ProfileHashParcel(pathToProfileHash), new ProgramHashParcel(pathToProgramHash), new FileObserverParcel(mProfileObserver), new FileObserverParcel(mProgramObserver));
                switchTo = (Fragment) adminFrag;
                currFragment = (ProfileHashFragment) adminFrag;
        }
        if (switchTo != null){
            ft.replace(R.id.fragment_container, switchTo);
            for(int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++){
                getSupportFragmentManager().popBackStackImmediate();
            }
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onProfileChosen(Uri uri) {
        // Nothing - Possibly Always Nothing
    }

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
        if(currFragment != null){
            currFragment.setNewProfileHash(pathToProfileHash);
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
        if(currFragment != null){
            currFragment.setNewProgramHash(pathToProgramHash);
        }
    }
}
