package edu.rose_hulman.trottasn.zambiancandlemakerinterface;

<<<<<<< HEAD
=======
import android.app.ProgressDialog;
import android.media.MediaScannerConnection;
>>>>>>> master
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
<<<<<<< HEAD
=======
import android.util.Log;
>>>>>>> master
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

public class MainActivity extends AppCompatActivity
        implements OperatorFragment.Callback, NavigationView.OnNavigationItemSelectedListener, AdminProfileChooserFragment.OnAdminProfileChosenListener {
<<<<<<< HEAD
=======

    private static final HashMap<String, DipProfile> pathToProfileHash = new HashMap<String, DipProfile>();
>>>>>>> master

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

<<<<<<< HEAD
=======
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/CSVs");
        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; ++i) {
                File file = files[i];
                if (file.isDirectory()) {
                    Log.d("NO_DIRECTORIES_ALLOWED", "There should not be a directory in this folder");
                } else {
                    readInCSVFile(file);
                }
            }
        }

>>>>>>> master
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

    public void readInCSVFile(File file){
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
                    Log.d("INVALID_CSV_FOR_PROGRAM", "CSV has less than two entries (no title / description)");
                    return;
                };
                if(strList.size()%2 != 0){
                    Log.d("INVALID_CSV_FOR_PROGRAM", "CSV has an odd number of entries (there exists an unequal pair)");
                }
                newProfile.setTitle(strList.get(0));
                newProfile.setDescription(strList.get(1));
                for(int i = 2; i < strList.size(); i+=2){
                    TimePosPair newPair = new TimePosPair(Integer.parseInt(strList.get(i)), Integer.parseInt(strList.get(i+1)));
                    newProfile.addPair(newPair);
                }
                pathToProfileHash.put(filename, newProfile);
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
            if(getSupportFragmentManager().getBackStackEntryCount() == 0){
                return;
            }
            super.onBackPressed();
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
                switchTo = new AdministratorProfileFragment();
                break;
            case R.id.nav_administrator_program:
                if(getSupportFragmentManager().getBackStackEntryCount() == 0 || !getSupportFragmentManager().getBackStackEntryAt(0).getName().equals(getString(R.string.operator_frag_name))) {
<<<<<<< HEAD
                    ft.addToBackStack(getString(R.string.operator_frag_name));
                }
                switchTo = new AdminProfileChooserFragment();
                break;
            case R.id.nav_graph_make_profile:
                if(getSupportFragmentManager().getBackStackEntryCount() == 0 || !getSupportFragmentManager().getBackStackEntryAt(0).getName().equals(getString(R.string.operator_frag_name))) {
                    ft.addToBackStack(getString(R.string.operator_frag_name));
                }
                switchTo = new GraphFragment();
                break;
            default:
                break;
=======
                    ft.addToBackStack(getString(R.string.operator_frag_name));
                }
                switchTo = AdminProfileChooserFragment.newInstance(new HashMapParcel(pathToProfileHash));
>>>>>>> master
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
}
