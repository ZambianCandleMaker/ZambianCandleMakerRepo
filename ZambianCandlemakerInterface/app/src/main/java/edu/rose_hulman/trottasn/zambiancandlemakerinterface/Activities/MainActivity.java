package edu.rose_hulman.trottasn.zambiancandlemakerinterface.Activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import edu.rose_hulman.trottasn.zambiancandlemakerinterface.CONSTANTS;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Fragments.AdminProfileChooserFragment;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Fragments.EditProfileFragment;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Fragments.OperatorFragment;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Fragments.ProgramModDelFrag;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models.CSVUtility;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models.DipProfile;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models.DipProgram;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Parcels.ProfileHashParcel;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.R;

public class MainActivity extends AppCompatActivity
        implements OperatorFragment.OperatorFragmentListener, NavigationView.OnNavigationItemSelectedListener, AdminProfileChooserFragment.OnProgramSavedListener, CallbackActivity {

    private static Map<String, DipProfile> pathToProfileHash;
    private static Map<String, DipProgram> pathToProgramHash;

    private ActionBarDrawerToggle mToggle;

    public static final String PROFILE_HASH = "profileHash";
    public static final String PROGRAM_HASH = "PROGRAM_HASH";
    private SharedPreferences activityPrefs;
    private int tempIdSave;
    private FloatingActionButton fab;
    public static final String PASSWORD_KEEPING_KEY = "PASSWORD_KEEPING";
    private boolean canAllow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set Main Activity Layout to Be activity_main
        setContentView(R.layout.activity_main);

        // Save the Default SharedPreferences off to activityPrefs variable
        activityPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        SharedPreferences.Editor editor = activityPrefs.edit();
        editor.putBoolean(PASSWORD_KEEPING_KEY, false);
        this.canAllow = false;

        // Create the pathToProfileHash HashMap
        pathToProfileHash = new HashMap<>();
        pathToProgramHash = new HashMap<>();

        //Begin the Process of Reading in New Files and Saving Them
        repopulateProfileHash();

        // After this is done, read in the program files and link them to the profile from the hash
        repopulateProgramHash();

        Gson gson = new Gson();
        String pathToProfileHashJson = gson.toJson(pathToProfileHash);
        String pathToProgramHashJson = gson.toJson(pathToProgramHash);
        editor.putString(PROFILE_HASH, pathToProfileHashJson);
        editor.putString(PROGRAM_HASH, pathToProgramHashJson);
        editor.apply();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //changed scope so can hide it
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(mToggle);
        mToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(savedInstanceState == null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, new OperatorFragment());
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
            return;
        };
//        ViewGroup fragCont = ((ViewGroup)findViewById(R.id.fragment_container));
//        if(fragCont != null){
//            fragCont.removeAllViews();
//        }
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

        if (id == R.id.action_logout){
            this.canAllow = false;
            SharedPreferences.Editor editor = activityPrefs.edit();
            editor.putBoolean(PASSWORD_KEEPING_KEY, false);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        this.tempIdSave = item.getItemId();
        if(true){
//        if(this.tempIdSave == R.id.nav_operator || this.canAllow){
            this.allowFragmentToReplace();
            return true;
        }
//        displayPasswordDialog();
        return true;
    }

    public void displayPasswordDialog(){
        final DialogFragment df = new DialogFragment() {

            @Override
            public Dialog onCreateDialog(Bundle b) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setTitle(getString(R.string.administrator_lock));
                View view = getActivity().getLayoutInflater().inflate(R.layout.semisecure_login_between_fragments, null, false);
                final EditText passwordBox = (EditText)view.findViewById(R.id.password_edit_text);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Need to check for invalidity first
                        String password = passwordBox.getText().toString();
                        File passwordFile = new File(CONSTANTS.PASSWORD_FILE_LOCATION);
                        String line = "";
                        if(!passwordFile.exists()){
                            try {
                                File passwordDir = new File(CONSTANTS.ADMINISTRATOR_FILES);
                                passwordDir.setWritable(true);
                                passwordDir.mkdirs();
                                passwordFile.createNewFile();
                                Toast.makeText(MainActivity.this, "Administrator needed to initialize password! Sorry.", Toast.LENGTH_SHORT).show();
                                Log.e("PSWRD", "Created new password location, Administrator needed to initialize");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            try {
                                FileReader fileReader = new FileReader(passwordFile.getPath());

                                BufferedReader bufferedReader = new BufferedReader(fileReader);

                                line = bufferedReader.readLine();
                                
                                if(line == null){
                                    Toast.makeText(MainActivity.this, "Administrator Needs to Initialize Password", Toast.LENGTH_SHORT).show();
                                }
                                
                                bufferedReader.close();
                            }
                            catch(FileNotFoundException ex) {
                                Log.e("PSWRD", "Unable to access password location");
                            }
                            catch(IOException ex) {
                                Log.e("PSWR", "Found password, but unable to read password");
                            }
                            if(password.equals(line.trim())){
                                canAllow = true;
                                activityPrefs.edit().putBoolean(PASSWORD_KEEPING_KEY, true);
                                allowFragmentToReplace();
                            }
                            else{
                                Toast.makeText(MainActivity.this, "Sorry, wrong password!", Toast.LENGTH_SHORT).show();
                                displayPasswordDialog();
                            }
                        }
                    }
                });

                builder.setView(view);

                return builder.create();
            }
        };
        df.show(getSupportFragmentManager(), "");
    }

    private void allowFragmentToReplace(){
        Fragment switchTo = null;
        String typeString = "";
        switch (this.tempIdSave){
            case R.id.nav_operator:
                //Switch without adding to backstack
                this.fab.setVisibility(View.VISIBLE);
                OperatorFragment opFrag = new OperatorFragment();
                switchTo = opFrag;
                typeString = getString(R.string.operator_frag_name);
                break;
            case R.id.nav_administrator_mod_del_program:
                this.fab.setVisibility(View.VISIBLE);
                ProgramModDelFrag modDelFrag = ProgramModDelFrag.newInstance();
                switchTo = modDelFrag;
                typeString = getString(R.string.admin_mod_del_frag_name);
                break;
            case R.id.nav_administrator_program:
                //Add to backstack like above
                this.fab.setVisibility(View.VISIBLE);
                AdminProfileChooserFragment adminFrag = AdminProfileChooserFragment.newInstance(null);
                typeString = getString(R.string.admin_program_frag_name);
                switchTo = adminFrag;
                break;
            case R.id.nav_graph_make_profile:

                this.fab.setVisibility(View.GONE);
                EditProfileFragment editFrag = EditProfileFragment.newInstance(new ProfileHashParcel(pathToProfileHash));
                switchTo = editFrag;
                typeString = getString(R.string.graph_make_prog_frag_name);
                break;
        }

        ViewGroup fragCont = ((ViewGroup)findViewById(R.id.fragment_container));
        if(fragCont != null){
            fragCont.removeAllViews();
        }

        if (switchTo != null){
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment myFragment = fm.findFragmentByTag(typeString);
            if(myFragment != null){
                ft.addToBackStack(typeString);
                ft.replace(R.id.fragment_container, myFragment);
            }
            ft.addToBackStack(typeString);
            ft.replace(R.id.fragment_container, switchTo);
            ft.commit();
        };
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }



    @Override
    public void onProgramSaved() {
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        storeHashData();
    }

    public void storeHashData(){
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
    }

    @Override
    public void savePathToProfileHash(Map<String, DipProfile> hashMap){
        SharedPreferences.Editor prefsEditor = activityPrefs.edit();
        Gson gson = new Gson();
        String pathToProfileHashJson = gson.toJson(hashMap);

        prefsEditor.putString(PROFILE_HASH, pathToProfileHashJson);
        prefsEditor.apply();
    }

    @Override
    public void switchToProgramEdit(DipProgram dipProgram) {
        Fragment adminFrag = AdminProfileChooserFragment.newInstance(dipProgram);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        for(int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++){
            fm.popBackStack();
        }
        ft.addToBackStack("prev_page");
        ft.replace(R.id.fragment_container, adminFrag);
        ft.commit();
    }
}
