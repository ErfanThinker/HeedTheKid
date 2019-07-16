package net.crowmaster.cardasmarto.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkContinuation;
import androidx.work.WorkManager;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.balysv.materialmenu.extras.toolbar.MaterialMenuIconToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.opencsv.CSVWriter;

import net.crowmaster.cardasmarto.R;
import net.crowmaster.cardasmarto.constant.Constants;
import net.crowmaster.cardasmarto.contracts.DBContract;
import net.crowmaster.cardasmarto.fragments.DebugFragment;
import net.crowmaster.cardasmarto.fragments.HistoryFragment;
import net.crowmaster.cardasmarto.fragments.PreferencesFragment;
import net.crowmaster.cardasmarto.fragments.TestPerformerFragment;
import net.crowmaster.cardasmarto.utils.DBHelper;
import net.crowmaster.cardasmarto.utils.InternetUtils;
import net.crowmaster.cardasmarto.widgets.ProgressiveToast;
import net.crowmaster.cardasmarto.workers.DataSyncWorker;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileWriter;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int MY_PERMISSIONS_REQUEST_STORAGE = 2937;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 2938;
    private MaterialMenuIconToolbar materialMenu;
    private boolean isAtHome;
    private DrawerLayout drawer;
    private CoordinatorLayout drawerInner;
    private static Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        materialMenu = new MaterialMenuIconToolbar(this, Color.WHITE, MaterialMenuDrawable.Stroke.THIN) {
            @Override public int getToolbarViewId() {
                return R.id.toolbar;
            }
        };
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_perform_test);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, new TestPerformerFragment(),
                        getString(R.string.test_performer_fragment_tag))
                .addToBackStack(getString(R.string.test_performer_fragment_tag)).
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
        toolbar.setSubtitle(R.string.perform_test_title);
        isAtHome = true;

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isAtHome) {
                    goToTestPerformer();
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

        drawerInner = findViewById(R.id.drawer_content_holder);


    }





    private void goToTestPerformer() {
        if(getSupportFragmentManager().findFragmentByTag(getString(R.string.test_performer_fragment_tag)) == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new TestPerformerFragment(),
                            getString(R.string.test_performer_fragment_tag))
                    .addToBackStack(getString(R.string.test_performer_fragment_tag)).
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
        } else {
            getSupportFragmentManager().popBackStack(getString(R.string.test_performer_fragment_tag), 0);
        }
        getSupportActionBar().setSubtitle(R.string.perform_test_title);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        materialMenu.animateState(MaterialMenuDrawable.IconState.BURGER);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_perform_test);
        isAtHome = true;
        clearMenuVisibility();
    }

    private void goToPreferences() {
        if(getSupportFragmentManager().findFragmentByTag(getString(R.string.preferences_fragment_tag)) == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new PreferencesFragment(),
                            getString(R.string.preferences_fragment_tag))
                    .addToBackStack(getString(R.string.preferences_fragment_tag)).
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
        } else {
            getSupportFragmentManager().popBackStack(getString(R.string.preferences_fragment_tag), 0);
        }

        if(getSupportActionBar() != null)
            getSupportActionBar().setSubtitle(R.string.action_preferences);

        materialMenu.animateState(MaterialMenuDrawable.IconState.X);
        isAtHome = false;
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        restoreMenuVisibility();

    }

    private void goToDebug() {
        if(getSupportFragmentManager().findFragmentByTag(getString(R.string.debug_fragment_tag)) == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new DebugFragment(),
                            getString(R.string.debug_fragment_tag))
                    .addToBackStack(getString(R.string.debug_fragment_tag)).
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
        } else {
            getSupportFragmentManager().popBackStack(getString(R.string.debug_fragment_tag), 0);
        }

        if(getSupportActionBar() != null)
            getSupportActionBar().setSubtitle(R.string.action_debug);

        materialMenu.animateState(MaterialMenuDrawable.IconState.X);
        isAtHome = false;
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        restoreMenuVisibility();

    }

    private void goToHistory() {
        if(getSupportFragmentManager().findFragmentByTag(getString(R.string.history_fragment_tag)) == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new HistoryFragment(),
                            getString(R.string.history_fragment_tag))
                    .addToBackStack(getString(R.string.history_fragment_tag)).
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
        } else {
            getSupportFragmentManager().popBackStack(getString(R.string.history_fragment_tag), 0);
        }

        if(getSupportActionBar() != null)
            getSupportActionBar().setSubtitle(R.string.action_history);

        materialMenu.animateState(MaterialMenuDrawable.IconState.ARROW);
        isAtHome = false;
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        clearMenuVisibility();
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(isAtHome) {
                this.finish();
            } else {
                goToTestPerformer();
            }

        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        materialMenu.syncState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        materialMenu.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        mMenu = menu;
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.ic_action_done) {
            if(getSupportFragmentManager()
                    .getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1)
                    .getName().equals(getString(R.string.preferences_fragment_tag))) {
                ((PreferencesFragment)getSupportFragmentManager()
                        .findFragmentByTag(getString(R.string.preferences_fragment_tag))).saveInfo();
                goToTestPerformer();
                return true;
            }

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_perform_test) {
            goToTestPerformer();
        } else if (id == R.id.nav_history) {
            goToHistory();
        } else if (id == R.id.nav_how_it_works) {
            startActivity(new Intent(MainActivity.this, GuideActivity.class));
        } else if (id == R.id.nav_preferences) {
            goToPreferences();

        } else if (id == R.id.nav_debug) {
            goToDebug();

        } else if (id == R.id.nav_about_us) {
            materialMenu.animateState(MaterialMenuDrawable.IconState.ARROW);
            isAtHome = false;
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else if (id == R.id.nav_sync) {
            //First check internet access
            if (InternetUtils.internetConnectionAvailable(500)) {
                Toast.makeText(this, "Connected!", Toast.LENGTH_SHORT).show();
            } else {
                final Snackbar snackbar = Snackbar.make(drawerInner, "Please connect to internet to proceed."
                        , Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("Got it!", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                }).show();
            }

        } else if (id == R.id.nav_export) {
            new AsyncExportDbAsCsv().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_perform_test);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        SharedPreferences.Editor spe = getSharedPreferences("permissions", MODE_PRIVATE).edit();
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    TestPerformerFragment mFragment = (TestPerformerFragment) getSupportFragmentManager()
                            .findFragmentByTag(getString(R.string.test_performer_fragment_tag));
                    if(mFragment != null && mFragment.isVisible()) {
                        mFragment.toggleButtonState(android.R.drawable.ic_media_pause);
                        TestPerformerFragment.isRecording = true;
                        mFragment.startServer();

                    }

                } else {
                    Log.e("TAG", "Permission denied");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    if(drawerInner != null) {
                        final Snackbar snackbar = Snackbar.make(drawerInner, "The app will not be able to function, please grant permission."
                                , Snackbar.LENGTH_INDEFINITE);
                        snackbar.setAction("Got it!", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbar.dismiss();
                            }
                        }).show();
                    }

                }
                return;
            }

            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    TestPerformerFragment mFragment = (TestPerformerFragment) getSupportFragmentManager()
                            .findFragmentByTag(getString(R.string.test_performer_fragment_tag));
                    if(mFragment != null && mFragment.isVisible()) {
                        mFragment.toggleButtonState(android.R.drawable.ic_media_pause);
                        TestPerformerFragment.isRecording = true;
                        mFragment.startServer();

                    }

                } else {
                    Log.e("TAG", "Permission denied");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    if(drawerInner != null) {
                        final Snackbar snackbar = Snackbar.make(drawerInner, "The app will not be able to function, please grant permission."
                                , Snackbar.LENGTH_INDEFINITE);
                        snackbar.setAction("Got it!", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbar.dismiss();
                            }
                        }).show();
                    }

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void clearMenuVisibility() {
        if(mMenu != null && mMenu.findItem(R.id.ic_action_done) != null) {
            mMenu.findItem(R.id.ic_action_done).setVisible(false);
        }
    }

    public void restoreMenuVisibility() {
        if(mMenu != null && mMenu.findItem(R.id.ic_action_done) != null) {
            mMenu.findItem(R.id.ic_action_done).setVisible(true);
        }
    }


    private void testFunction() throws JSONException {
        JSONArray test = new JSONArray("[{\"key0\":1},{\"key1\":2}]");
        Log.e("jsonArray", test.toString());
    }

    public void requestHistory(long sessionSerial) {
        /*Bundle bundle = new Bundle();
        bundle.putLong("sessionSerial", sessionSerial );
        
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, new HistoryFragment().setArguments(bundle);,
                        getString(R.string.history_fragment_tag))
                .addToBackStack(getString(R.string.history_fragment_tag)).
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();*/
    }

    private class AsyncExportDbAsCsv extends AsyncTask<Activity, Void, Void>{
        ProgressiveToast mProgressiveToast;

        @Override
        protected Void doInBackground(Activity... activities) {
            exportDB(activities[0]);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.mProgressiveToast = ProgressiveToast.getInstance();
            this.mProgressiveToast.show(MainActivity.this, "Please be patient, this may take a while...",
                    -1);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            this.mProgressiveToast.dismiss();
        }


    }

    private void exportDB(Context context) {

        //File dbFile = getDatabasePath(DBContract.DATABASE_NAME);
        DBHelper dbhelper = new DBHelper(context);
        File exportDir = new File(Environment.getExternalStorageDirectory() + File.separator + getString(R.string.app_name) + File.separator);
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        File file = new File(exportDir, "records.csv");
        try {
            if (file.exists()) {
                file.delete(); //you might want to check if delete was successful
            }
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            SQLiteDatabase db = dbhelper.getReadableDatabase();
            Cursor curCSV = db.rawQuery("SELECT * FROM " + DBContract.DataTable.TABLE_NAME, null);
            csvWrite.writeNext(curCSV.getColumnNames());
            while (curCSV.moveToNext()) {
                //Which column you want to export
                String arrStr[] = {curCSV.getString(0), curCSV.getString(1), curCSV.getString(2),
                        curCSV.getString(3), curCSV.getString(4), curCSV.getString(5),
                        curCSV.getString(6), curCSV.getString(7), curCSV.getString(8),  curCSV.getString(9)};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
        } catch (Exception sqlEx) {
            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
        }
    }
}
